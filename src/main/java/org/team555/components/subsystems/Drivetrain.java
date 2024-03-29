package org.team555.components.subsystems;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ProxyCommand;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import org.team555.ChargedUp;
import org.team555.constants.ControlScheme;
import org.team555.constants.Constants.Auto;
import org.team555.inputs.JoystickInput;
import org.team555.math.Math555;
import org.team555.structure.DetectionType;
import org.team555.util.HashMaps;
import org.team555.util.Lazy;
import org.team555.util.LazyDouble;
import org.team555.util.frc.Logging;
import org.team555.util.frc.PIDMechanism;
import org.team555.util.frc.Trajectories;
import org.team555.util.frc.can.CANSafety;
import org.team555.util.frc.commandrobot.CommandRobot;
import org.team555.util.frc.commandrobot.ManagerSubsystemBase;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import com.revrobotics.CANSparkMax;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SwerveModule;

import static org.team555.constants.DriveConstants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class Drivetrain extends ManagerSubsystemBase
{
    private final SwerveModule[] modules;

    // RATE LIMITERS //
    // These limit the rate at which the driver can change their inputs,
    // in order to prevent tippage.
    public final SlewRateLimiter xInputRateLimiter;
    public final SlewRateLimiter yInputRateLimiter;
    public final SlewRateLimiter thetaInputRateLimiter;

    // PID MECHANISMS //
    // These store the speeds which the drivetrain should move at
    // as a percentage of the maximum speed along that axis.
    // Theta is in radians.
    public final PIDMechanism xPID;
    public final PIDMechanism yPID;
    public final PIDMechanism thetaPID;
    
    private final SwerveDrivePoseEstimator poseEstimator;

    private final FieldObject2d moduleObject;

    private boolean useFieldRelative = true;
    private boolean isStraightPidding;

    private int speedIndex = SPEEDS.length - 1;
    private Rotation2d currentStraightAngle;

    private boolean isXMode = false;
    
    // Simulation information //
    private static class SimulationData
    {
        private ChassisSpeeds targetSpeeds = new ChassisSpeeds();
        private ChassisSpeeds currentSpeeds = new ChassisSpeeds();

        private Pose2d resetPose = new Pose2d();
        private Pose2d accumulatedPose = new Pose2d();
    }
    private SimulationData simulation;

    // Construction //
    public Drivetrain()
    {
        if(RobotBase.isSimulation())
        {
            simulation = new SimulationData();
            simulation.accumulatedPose = new Pose2d(5, 5, new Rotation2d());
        }

        // Build Modules //
        modules = new SwerveModule[MODULE_COUNT];
        moduleObject = ChargedUp.field.getObject("Swerve Modules");

        Pose2d[] modPoses = new Pose2d[MODULE_COUNT];

        for (int i = 0; i < MODULE_COUNT; i++)
        {
            modules[i] = MODULES[i].build(
                ChargedUp.getDebugTab()
                    .getLayout("Module " + MODULE_NAMES[i], BuiltInLayouts.kList)
                    .withSize(2, 3)
                    .withPosition(2*i, 0)
            );

            CANSafety.monitor(modules[i].getDriveMotor());
            CANSafety.monitor(modules[i].getSteerMotor());
            CANSafety.monitor(modules[i].getSteerEncoder());

            modPoses[i] = new Pose2d(MOD_POSITIONS[i], new Rotation2d());

            assert STEER_TYPE == MotorType.NEO
                 : "This code assumes that the steer type of the robot is a neo.";

            CANSparkMax mot = (CANSparkMax) modules[i].getSteerMotor();
            mot.burnFlash();
        }

        moduleObject.setPoses(modPoses);

        DRIVE_INVERT.whenUpdate(modules[0].getDriveMotor()::setInverted)
                    .whenUpdate(modules[1].getDriveMotor()::setInverted)
                    .whenUpdate(modules[2].getDriveMotor()::setInverted)
                    .whenUpdate(modules[3].getDriveMotor()::setInverted);
        STEER_INVERT.whenUpdate(modules[0].getSteerMotor()::setInverted)
                    .whenUpdate(modules[1].getSteerMotor()::setInverted)
                    .whenUpdate(modules[2].getSteerMotor()::setInverted)
                    .whenUpdate(modules[3].getSteerMotor()::setInverted);
        
        // Build Odometry //
        poseEstimator = new SwerveDrivePoseEstimator(
            KINEMATICS, 
            getRobotRotation(), 
            getModulePositions(), 
            new Pose2d()
        );

        // Build PID Controllers //
        PIDController xController = new PIDController(
            PosPID.consts().kP,
            PosPID.consts().kI, 
            PosPID.consts().kD
        );

        PIDController yController = new PIDController(
            PosPID.consts().kP,
            PosPID.consts().kI, 
            PosPID.consts().kD
        );

        PIDController thetaController = new PIDController(
            ThetaPID.consts().kP,
            ThetaPID.consts().kI, 
            ThetaPID.consts().kD
        );

        

        thetaController.setTolerance(Math.toRadians(1.5), Math.toRadians(0.5));
        thetaController.enableContinuousInput(0, 2*Math.PI);
        
        PosPID.KP.whenUpdate(xController::setP).whenUpdate(yController::setP);
        PosPID.KI.whenUpdate(xController::setI).whenUpdate(yController::setI);
        PosPID.KD.whenUpdate(xController::setD).whenUpdate(yController::setD);

        ThetaPID.KP.whenUpdate(thetaController::setP);
        ThetaPID.KI.whenUpdate(thetaController::setI);
        ThetaPID.KD.whenUpdate(thetaController::setD);

        // Build Slew Rate Limiters //
        xInputRateLimiter     = new SlewRateLimiter(inputRateLimit());
        yInputRateLimiter     = new SlewRateLimiter(inputRateLimit());
        thetaInputRateLimiter = new SlewRateLimiter(inputRateLimit());

        // Build PID Mechanisms //
        xPID     = new PIDMechanism(xController);
        yPID     = new PIDMechanism(yController);
        thetaPID = new PIDMechanism(thetaController);
        thetaPID.disableOutputClamping();
        // thetaPID.setMaxOutput(MAX_TURN_SPEED_RAD_PER_S);
    }

    /**
     * Enables field relative
     */
    public void enableFieldRelative()  
    {
        useFieldRelative = true;
        Logging.info("Field relative enabled");
    }
    /**
     * Disables field relative
     */
    public void disableFieldRelative() 
    {
        useFieldRelative = false;
        Logging.info("Field relative disabled");
    }

    /**
     * Gets if field relative is enabled
     */
    public boolean usingFieldRelative() 
    {
        return useFieldRelative;
    }

    public double getChargeStationAngle()
    {
        return ChargedUp.gyroscope.getRoll();
    }

    /**
     * Gets the final chassis speeds relative to its forward
     * @param adjusted_omega rotational velocity
     * @param adjusted_vx x direction velocity
     * @param adjusted_vy y direction velocity
     * @return Chassis speeds
     */
    public ChassisSpeeds getSpeedsFromMode(double omega, double x, double y)
    {
        if(useFieldRelative) return ChassisSpeeds.fromFieldRelativeSpeeds(x, y, omega, getRobotRotation());
        return new ChassisSpeeds(x, y, omega);
    }

    /**
     * Takes joystick inputs for turning and driving and converts them to velocities for the robot.
     * Should be called in order to manually control the robot.
     * Sets the speeds of the motors directly
     * @param turn the turn axis of the joystick
     * @param drive the driving axis of the joystick
     */
    public void setInput(JoystickInput turn, JoystickInput drive)
    {
        ControlScheme.TURN_ADJUSTER.adjustX(turn);
        ControlScheme.DRIVE_ADJUSTER.adjustMagnitude(drive);

        // double turnRL = thetaInputRateLimiter.calculate(turn.getX());
        // double xRL    = xInputRateLimiter.calculate(drive.getX());
        // double yRL    = yInputRateLimiter.calculate(drive.getY());

        if (turn.getX() != 0 || drive.getX() != 0 || drive.getY() != 0)
        {
            disableXMode();
        }

        setChassisSpeeds(getSpeedsFromMode(
            turn.getX()  * SPEEDS[speedIndex][1] * MAX_TURN_SPEED_RAD_PER_S,
            drive.getY() * SPEEDS[speedIndex][0] * MAX_SPEED_MPS,
            drive.getX() * SPEEDS[speedIndex][0] * MAX_SPEED_MPS
        ));
    }

    /**
     * Set to use the given velocities in robot-wise coordinates:
     * <ul>
     *      <li>+x is forward</li>
     *      <li>+y is left</li>
     *      <li>+O is CCW</li>
     * </ul>
     * 
     * @param omega rotational velocity
     * @param vx x direction velocity
     * @param vy y direction velocity
     */
    public void setChassisSpeeds(double omega, double vx, double vy)
    {
        vx    /= MAX_SPEED_MPS;
        vy    /= MAX_SPEED_MPS;
        omega /= MAX_TURN_SPEED_RAD_PER_S;

        vx    = Math555.clamp1(vx);
        vy    = Math555.clamp1(vy);
        omega = Math555.clamp1(omega);

        xPID    .setSpeed(vx);
        yPID    .setSpeed(vy);
        thetaPID.setSpeed(omega);
    }

    /**
     * Set to use the given velocities in robot-wise coordinates:
     * <ul>
     *      <li>+x is forward</li>
     *      <li>+y is left</li>
     *      <li>+O is CCW</li>
     * </ul>
     * 
     * @param omega rotational velocity
     * @param vx x direction velocity
     * @param vy y direction velocity
     */
    public void setChassisSpeeds(ChassisSpeeds speeds)
    {
        setChassisSpeeds(speeds.omegaRadiansPerSecond, speeds.vxMetersPerSecond, speeds.vyMetersPerSecond);
    }

    /**
     * Pause driver input: this resets all rate limiters for joystick inputs
     */
    public void pauseDriverInput()
    {
        xInputRateLimiter.reset(0);
        yInputRateLimiter.reset(0);
        thetaInputRateLimiter.reset(0);
    }

    /**
     * Converts speed of each swerve module from meters/second to voltage and converts angle to radians. 
     * Then, updates odometry with new values. 
     * @param states array of {@link SwerveModuleState}, contains all four states
     */
    private void driveFromStates(SwerveModuleState[] states)
    {
        SwerveDriveKinematics.desaturateWheelSpeeds(states, MAX_SPEED_MPS);
        
        for(int i = 0; i < MODULE_COUNT; i++)
        {
            // states[i] = SwerveModuleState.optimize(
            //     states[i], 
            //     new Rotation2d(modules[i].getSteerAngle())
            // );

            modules[i].set(
                states[i].speedMetersPerSecond / MAX_SPEED_MPS * MAX_VOLTAGE_V,
                states[i].angle.getRadians()
            );
        }

        // Simulate swerve drive using average forces
        if(RobotBase.isSimulation())
        {
            simulation.targetSpeeds = KINEMATICS.toChassisSpeeds(states);

            final double targetX = simulation.targetSpeeds.vxMetersPerSecond;
            final double targetY = simulation.targetSpeeds.vyMetersPerSecond;
            final double targetO = simulation.targetSpeeds.omegaRadiansPerSecond;
            
            double currentX = simulation.currentSpeeds.vxMetersPerSecond;
            double currentY = simulation.currentSpeeds.vyMetersPerSecond;
            double currentO = simulation.currentSpeeds.omegaRadiansPerSecond;

            final double dt = CommandRobot.deltaTime();

            final double acc = MAX_ACCEL_MPS2 * dt;
            final double dec = acc * 3;

            final double alp = MAX_TURN_ACCEL_RAD_PER_S2 * dt;
            final double dlp = alp * 3;

            currentX = Math555.accClamp(targetX, currentX, acc, dec);
            currentY = Math555.accClamp(targetY, currentY, acc, dec);
            currentO = Math555.accClamp(targetO, currentO, alp, dlp);

            simulation.currentSpeeds = new ChassisSpeeds(currentX, currentY, currentO);

            Transform2d transform = new Transform2d(
                new Translation2d(currentX, currentY),//.unaryMinus(), 
                new Rotation2d(currentO)//.unaryMinus()
            );
            transform = transform.times(dt);

            simulation.accumulatedPose = simulation.accumulatedPose.plus(transform);

            Pose2d[] modPoses = new Pose2d[MODULE_COUNT];

            for(int i = 0; i < MODULE_COUNT; i++)
            {
                modPoses[i] = simulation.accumulatedPose.plus(
                    new Transform2d(MOD_POSITIONS[i], states[i].angle)
                );
            }

            moduleObject.setPoses(modPoses);
        }

        // Update pose
        poseEstimator.update(
            getRobotRotation(), 
            getModulePositions()
        );

        // Use april tag info
        if(ChargedUp.vision.getTargetType() == DetectionType.APRIL_TAG)
        {
            ChargedUp.vision.updateEstimatedPose(poseEstimator.getEstimatedPosition());

            if(poseEstimator.getEstimatedPosition().minus(ChargedUp.vision.getEstimatedPose()).getTranslation().getNorm() <= VISION_ESTIMATE_MAX_DISPLACEMENT)
            {
                poseEstimator.addVisionMeasurement(
                    ChargedUp.vision.getEstimatedPose(), 
                    ChargedUp.vision.getTimestampSeconds()
                );
            }
        }
    }

    /**
     * Maps all of the x-positions a module goes to and makes an array containing these positions
     * @return an object of class SwerveModulePosition
     */
    public SwerveModulePosition[] getModulePositions()
    {
        return Arrays.stream(modules)
            .map(x -> x.getPosition())
            .toArray(SwerveModulePosition[]::new);
    }

    /**
     * Begin the process of pidding to the current angle.
     */
    public void startStraightPidding() 
    {
        isStraightPidding = true;
        currentStraightAngle = getRobotRotation();
    }

    /**
     * Stop pidding to the current angle.
     */
    public void stopStraightPidding() 
    {
        isStraightPidding = false;
    }

    @Override 
    public void periodic() 
    {
        // Update PID mesaurements
        xPID.setMeasurement(getRobotPose().getX());
        yPID.setMeasurement(getRobotPose().getY());
        thetaPID.setMeasurement(getRobotRotationInCircle().getRadians());

        // Handle straight pid
        if (isStraightPidding) 
        {
            if (thetaPID.getTarget() != currentStraightAngle.getRadians()) 
            {
                thetaPID.setTarget(currentStraightAngle.getRadians());
            }
            else 
            {
                thetaPID.updateTarget(currentStraightAngle.getRadians());
            }
        }

        // Update PID
        xPID.update();
        yPID.update();
        thetaPID.update();

        double xRL = xInputRateLimiter.calculate(xPID.getSpeed());
        double yRL = yInputRateLimiter.calculate(yPID.getSpeed());
        double thetaRL = thetaInputRateLimiter.calculate(thetaPID.getSpeed());

        if (isXMode && xRL == 0 && yRL == 0 && thetaRL == 0)
        {
            modules[0].set(0, 1*Math.PI/4);
            modules[1].set(0, 3*Math.PI/4);
            modules[2].set(0, 5*Math.PI/4);
            modules[3].set(0, 7*Math.PI/4);
            return;            
        }
        
        // Construct chassis speeds
        ChassisSpeeds chassisSpeeds = new ChassisSpeeds(
            xRL     * MAX_SPEED_MPS, 
            yRL     * MAX_SPEED_MPS,
            thetaRL * MAX_TURN_SPEED_RAD_PER_S
        );

        // Drive from states
        SwerveModuleState[] states = KINEMATICS.toSwerveModuleStates(chassisSpeeds);
        driveFromStates(states);
        
        // Update field pose
        Pose2d pose = getRobotPose();
        ChargedUp.field.setRobotPose(pose);
    }
    
    /**
     * Reset the position of the robot to that provided, updating 
     * odometry as is necessary.
     */
    public void setRobotPose(Pose2d pose)
    {
        poseEstimator.resetPosition(
            getRobotRotation(),
            getModulePositions(),
            pose
        );

        ChargedUp.vision.resetPose(pose);
        
        if(RobotBase.isSimulation())
        {
            simulation.resetPose = pose;    
        }
    }

    /**
     * Get the rotation of the robot within the unit circle.
     */
    public Rotation2d getRobotRotationInCircle()
    {
        return Rotation2d.fromRadians(getRobotRotation().getRadians() % (2*Math.PI));
    }

    /**
     * Get the total rotation of the robot.
     */
    public Rotation2d getRobotRotation()
    {
        if(RobotBase.isReal())
        {
            return ChargedUp.gyroscope.getRotation2d();
        }
        else 
        {
            return getRobotPose().getRotation();
        }
    }

    /**
     * Get the pose of the robot.
     */
    public Pose2d getRobotPose()
    {
        if(RobotBase.isReal())
        {
            return poseEstimator.getEstimatedPosition();
        }
        else 
        {
            return new Pose2d().plus(simulation.accumulatedPose.minus(simulation.resetPose));
        }
    }
    
    /**
     * Increase the current driver maximum speed limit.
     */
    public void increaseMaxSpeed()
    {
        speedIndex = (speedIndex == SPEEDS.length-1) ? speedIndex : speedIndex + 1;
    }
    /**
     * Decrease the current driver maximum speed limit.
     */
    public void decreaseMaxSpeed() 
    {
        speedIndex = (speedIndex == 0) ? 0 : speedIndex - 1;
    }

    /**
     * Get the current speed limits. {@code [0]} is for translation and {@code [1]} is for rotation.s
     * @return
     */
    public double[] getCurrentSpeedLimits() {return SPEEDS[speedIndex];}

    /**
     * Get the approximate angle at which the currently seen object exists.
     */
    public double getObjectAngle() 
    {
        return getRobotRotationInCircle().getRadians() - Math.toRadians(ChargedUp.vision.getObjectAX()); 
    }
    /**
     * Get the approximate position at which the currently seen object exists.
     */
    public double getObjectHorizontalPosition() 
    {
        return getRobotPose().getY() + ChargedUp.vision.getObjectAX() * 1; 
    }

    public void enableXMode()
    {
        isXMode = true;
    }
    public void disableXMode()
    {
        isXMode = false;
    }

    @Override
    public void reset() 
    {
        stopStraightPidding();
        disableXMode();
    }

    public final DriveCommands commands = this.new DriveCommands();
    public class DriveCommands 
    {
        private DriveCommands() {}

        public CommandBase increaseSpeed()
        {
            return Commands.runOnce(() -> Drivetrain.this.increaseMaxSpeed());
        }
        public CommandBase decreaseSpeed()
        {
            return Commands.runOnce(() -> Drivetrain.this.decreaseMaxSpeed());
        }

        public CommandBase driveInstant(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.runOnce(() -> Drivetrain.this.setChassisSpeeds(omega_rad_per_second, vx_meter_per_second, -vy_meter_per_second), Drivetrain.this);
        }

        public CommandBase driveForTimeRelative(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return disableFieldRelative()
                .andThen(driveForTime(time, omega_rad_per_second, vx_meter_per_second, vy_meter_per_second))
                .finallyDo(__ -> Drivetrain.this.enableFieldRelative());
        }
        public CommandBase driveForTimeAbsolute(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return enableFieldRelative()
                .andThen(driveForTime(time, omega_rad_per_second, vx_meter_per_second, vy_meter_per_second));
        }

        public CommandBase driveForTime(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.run(() -> Drivetrain.this.setChassisSpeeds(omega_rad_per_second, vx_meter_per_second, vy_meter_per_second), Drivetrain.this)
                .raceWith(Commands.waitSeconds(time))
                .finallyDo(__ -> Drivetrain.this.setChassisSpeeds(0, 0, 0));

        }

        /**
         * Lock the robot's angle
         * @return
         */
        public CommandBase enableStraightPidding() 
        {
            return Commands.runOnce(() -> Drivetrain.this.startStraightPidding());
        }

        /**
         * Stop locking the robot's angle
         * @return
         */
        public CommandBase disableStraightPidding() 
        {
            return Commands.runOnce(() -> Drivetrain.this.stopStraightPidding());
        }

        public CommandBase enableFieldRelative()
        {
            return Commands.runOnce(Drivetrain.this::enableFieldRelative, Drivetrain.this);
        }
        public CommandBase disableFieldRelative()
        {
            return Commands.runOnce(Drivetrain.this::disableFieldRelative, Drivetrain.this);
        }
        public CommandBase toggleFieldRelative()
        {
            return Commands.runOnce(() -> useFieldRelative = !useFieldRelative, Drivetrain.this);
        }

        public SwerveAutoBuilder autoBuilder(HashMap<String, Command> markers)
        {
            return new SwerveAutoBuilder(
                Drivetrain.this::getRobotPose,
                Drivetrain.this::setRobotPose,
                PosPID.consts(), 
                ThetaPID.autoconsts(),
                Drivetrain.this::setChassisSpeeds,
                markers,
                true,
                Drivetrain.this
            );
        }

        /**
         * Create a full autonomous command using the given path planner trajectory.
         * @param trajectory The trajectory
         * @param markers The autonomous markers
         * @return The command
         */
        public CommandBase trajectory(SwerveAutoBuilder autoBuilder, PathPlannerTrajectory trajectory)
        {
            return autoBuilder.followPathWithEvents(trajectory);
        }
        /**
         * Create a full autonomous command using the given path planner trajectory name.
         * @param trajectoryName The trajectory name
         * @return The command
         */
        public CommandBase trajectory(SwerveAutoBuilder autoBuilder, String trajectoryName)
        {
            return trajectory(autoBuilder, Trajectories.get(trajectoryName, Auto.constraints()))
                .withName("Trajectory " + trajectoryName);
        }
        
        /**
         * Create a full autonomous command using the given path planner trajectory name.
         * @param trajectoryName The trajectory name
         * @return The command
         */
        public CommandBase testTrajectory(String trajectoryName)
        {
            SwerveAutoBuilder autoBuilder = autoBuilder(HashMaps.of());
            return trajectory(autoBuilder, trajectoryName)
                .beforeStarting(autoBuilder.resetPose(Trajectories.get(trajectoryName, new PathConstraints(1, 1))))
                .withName("Test Trajectory " + trajectoryName);
        }

        /**
         * Creates a command which goes to the given angle in absolute coordinates.
         * @return The command
         */
        public CommandBase goToAngleAbsolute(Rotation2d angle)
        {
            return thetaPID.goToSetpoint(angle.getRadians()).withTimeout(1.8);
        }

        // GO TO POSITION ABSOLUTE //
        /**
         * Creates a command which goes to the given dynamically changing point in field space.
         * @return The command
         */
        public CommandBase goToDynamicPositionAbsolute(Supplier<Translation2d> xy)
        {
            return Commands.parallel(
                xPID.goToSetpoint(() -> xy.get().getX()),
                yPID.goToSetpoint(() -> xy.get().getY())
            );
        }
        
        /**
         * Creates a command which goes to a dynamically changing point in field space defined by ({@code x.get()}, {@code y.get()}).
         * @return The command
         */
        public CommandBase goToDynamicPositionAbsolute(DoubleSupplier x, DoubleSupplier y)
        {
            return goToDynamicPositionAbsolute(() -> new Translation2d(x.getAsDouble(), y.getAsDouble()));
        }

        /**
         * Creates a command which goes to the point defined by {@code xy.get()} when the command starts in field space.
         * @return The command
         */
        public CommandBase goToPositionAbsolute(Supplier<Translation2d> xy)
        {
            return goToPositionAbsolute(new Lazy<>(xy));
        }

        /**
         * Creates a command which goes to the given point in field space.
         * @return The command
         */
        public CommandBase goToPositionAbsolute(Translation2d xy)
        {
            return goToPositionAbsolute(() -> xy);
        }

        /**
         * Creates a command which goes to the point defined by ({@code x}, {@code y}) in field space.
         * @return The command
         */
        public CommandBase goToPositionAbsolute(double x, double y)
        {
            return goToPositionAbsolute(new Translation2d(x, y));
        }

        // GO TO POSITION RELATIVE //
        /**
         * Creates a command which goes to the given dynamically changing point in robot-relative space.
         * @return The command
         */
        public CommandBase goToDynamicPositionRelative(Supplier<Translation2d> xy)
        {
            return goToDynamicPositionAbsolute(() -> 
                // robotxy + targetxy (rotate) robotrotation
                getRobotPose().getTranslation()
                    .plus(xy.get().rotateBy(getRobotRotation()))
            );
        }

        /**
         * Creates a command which goes to a dynamically changing point in robot-relative space defined by ({@code x.get()}, {@code y.get()}).
         * @return The command
         */
        public CommandBase goToDynamicPositionRelative(DoubleSupplier x, DoubleSupplier y)
        {
            return goToDynamicPositionRelative(() -> new Translation2d(x.getAsDouble(), y.getAsDouble()));
        }

        /**
         * Creates a command which goes to the point defined by {@code xy.get()} when the command starts in robot-relative space.
         * @return The command
         */
        public CommandBase goToPositionRelative(Supplier<Translation2d> xy)
        {
            return goToDynamicPositionRelative(new Lazy<>(xy));
        }

        /**
         * Creates a command which goes to the given point in robot-relative space.
         * @return The command
         */
        public CommandBase goToPositionRelative(Translation2d xy)
        {
            return goToPositionRelative(() -> xy);
        }

        /**
         * Creates a command which goes to the point defined by ({@code x}, {@code y}) in robot-relative space.
         * @return The command
         */
        public CommandBase goToPositionRelative(double x, double y)
        {
            return goToPositionRelative(new Translation2d(x, y));
        }
    }
}
