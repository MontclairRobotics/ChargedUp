package frc.robot.components.subsystems;

import edu.wpi.first.wpilibj2.command.Commands;
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
import frc.robot.ChargedUp;
import frc.robot.constants.ControlScheme;
import frc.robot.inputs.JoystickInput;
import frc.robot.math.Math555;
import frc.robot.structure.DetectionType;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.can.CANSafety;
import frc.robot.util.frc.commandrobot.CommandRobot;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import com.revrobotics.CANSparkMax;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SwerveModule;

import static frc.robot.constants.DriveConstants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class Drivetrain extends ManagerSubsystemBase
{
    private final SwerveModule[] modules;

    public final PIDController xController;
    public final PIDController yController;
    public final PIDController thetaController;

    public final SlewRateLimiter xInputRateLimiter;
    public final SlewRateLimiter yInputRateLimiter;
    public final SlewRateLimiter thetaInputRateLimiter;

    public final PIDMechanism xPID;
    public final PIDMechanism yPID;
    public final PIDMechanism thetaPID;

    private boolean useFieldRelative = true;

    private final FieldObject2d moduleObject;
    private final SwerveDrivePoseEstimator poseEstimator;
    private double currentStraightAngle;
    private boolean isStraightPidding;

    /**
     * Store whether or not the drivetrain has already piped values into its motors, signalling an autonomous command
     * if control reaches "periodic" and this is true.
     */
    private boolean hasDrivenThisUpdate = false;
    private boolean hasUsedDriverInputThisUpdate = false;

    //sets the speedIndex to speeds length
    private static int speedIndex = SPEEDS.length-1;
    
    private static class SimulationData
    {
        private ChassisSpeeds targetSpeeds = new ChassisSpeeds();
        private ChassisSpeeds currentSpeeds = new ChassisSpeeds();

        private Pose2d resetPose = new Pose2d();
        private Pose2d accumulatedPose = new Pose2d();
    }
    private SimulationData simulation;

    // private Consumer<Double> funcMul(Consumer<Double> cd, double factor)
    // {
    //     return x -> cd.accept(x * factor);
    // }
    // private Consumer<Double> drivePid(Consumer<Double> cd)
    // {
    //     return funcMul(cd, SdsModuleConfigurations.MK4I_L1.getDriveReduction());
    // }
    // private Consumer<Double> steerPid(Consumer<Double> cd)
    // {
    //     return funcMul(cd, SdsModuleConfigurations.MK4I_L1.getSteerReduction());
    // }

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
        xController = new PIDController(
            PosPID.consts().kP,
            PosPID.consts().kI, 
            PosPID.consts().kD
        );

        yController = new PIDController(
            PosPID.consts().kP,
            PosPID.consts().kI, 
            PosPID.consts().kD
        );

        thetaController = new PIDController(
            ThetaPID.consts().kP,
            ThetaPID.consts().kI, 
            ThetaPID.consts().kD
        );

        thetaController.setTolerance(1, 0.5);
        thetaController.enableContinuousInput(0, 360);
        
        PosPID.KP.whenUpdate(xController::setP).whenUpdate(yController::setP);
        PosPID.KI.whenUpdate(xController::setI).whenUpdate(yController::setI);
        PosPID.KD.whenUpdate(xController::setD).whenUpdate(yController::setD);

        ThetaPID.KP.whenUpdate(thetaController::setP);
        ThetaPID.KI.whenUpdate(thetaController::setI);
        ThetaPID.KD.whenUpdate(thetaController::setD);

        // Build slew rate limiters //
        xInputRateLimiter     = new SlewRateLimiter(inputRateLimit());
        yInputRateLimiter     = new SlewRateLimiter(inputRateLimit());
        thetaInputRateLimiter = new SlewRateLimiter(inputRateLimit());

        // Build PID Mechanisms //
        xPID     = new PIDMechanism(xController);
        yPID     = new PIDMechanism(yController);
        thetaPID = new PIDMechanism(thetaController);
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
     * Takes joystick inputs for turning and driving and converts them to velocities for the robot.
     * Should be called in order to manually control the robot.
     * Sets the speeds of the motors directly
     * @param turn the turn axis of the joystick
     * @param drive the driving axis of the joystick
     */
    public void setInput(JoystickInput turn, JoystickInput drive)
    {
        hasUsedDriverInputThisUpdate = true;

        ControlScheme.TURN_ADJUSTER.adjustX(turn);
        ControlScheme.DRIVE_ADJUSTER.adjustMagnitude(drive);

        double turnRL = thetaInputRateLimiter.calculate(turn.getX());
        double xRL    = xInputRateLimiter.calculate(drive.getX());
        double yRL    = yInputRateLimiter.calculate(drive.getY());

        set(
            turnRL / SPEEDS[speedIndex][1],
            yRL    / SPEEDS[speedIndex][0],
            xRL    / SPEEDS[speedIndex][0]
        );
    }
    
    /**
     * Set to use the given velocities in robot-wise coordinates:
     * <ul>
     *      <li>+y is forward</li>
     *      <li>+x is left</li>
     *      <li>+O is CCW</li>
     * </ul>
     * 
     * TODO: is this correct?
     * 
     * @param omega rotational velocity percentage
     * @param vx x direction velocity percentage
     * @param vy y direction velocity percentage
     */
    public void set(double omega, double vx, double vy)
    {
        vx    = Math555.clamp1(vx);
        vy    = Math555.clamp1(vy);
        omega = Math555.clamp1(omega);

        xPID    .setSpeed(vx);
        yPID    .setSpeed(vy);
        thetaPID.setSpeed(omega);
    }

    public void setVelocities(double omega, double vx, double vy)
    {
        vx    /= MAX_SPEED_MPS;
        vy    /= MAX_SPEED_MPS;
        omega /= Math.toDegrees(MAX_TURN_SPEED_RAD_PER_S);

        vx    = Math555.clamp1(vx);
        vy    = Math555.clamp1(vy);
        omega = Math555.clamp1(omega);

        xPID    .setSpeed(vx);
        yPID    .setSpeed(vy);
        thetaPID.setSpeed(omega);
    }

    /**
     * Gets the final chassis speeds relative to its forward
     * @param adjusted_omega rotational velocity
     * @param adjusted_vx x direction velocity
     * @param adjusted_vy y direction velocity
     * @return Chassis speeds
     */
    private ChassisSpeeds getChassisSpeeds(double adjusted_omega, double adjusted_vx, double adjusted_vy)
    {
        if(useFieldRelative)
        {
            return ChassisSpeeds.fromFieldRelativeSpeeds(
                adjusted_vx,
                adjusted_vy,
                adjusted_omega, 
                getRobotRotation()
            );
        }
        else
        {
            return new ChassisSpeeds(
                adjusted_vx, 
                adjusted_vy, 
                adjusted_omega
            );
        }
    }
    /**
     * Uses the chassis speeds to  
     * Only works when the robot is real currently
     * @param speeds ChassisSpeeds object
     */
    public void driveFromChassisSpeeds(ChassisSpeeds speeds) 
    {
        SwerveModuleState[] states = KINEMATICS.toSwerveModuleStates(speeds);
        driveFromStates(states);
    }

    /**
     * Converts speed of each swerve module from meters/second to voltage and converts angle to radians. 
     * Then, updates odometry with new values. 
     * @param states array of {@link SwerveModuleState}, contains all four states
     */
    private void driveFromStates(SwerveModuleState[] states)
    {
        SwerveDriveKinematics.desaturateWheelSpeeds(states, MAX_SPEED_MPS);
        hasDrivenThisUpdate = true;
        
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

            if(poseEstimator.getEstimatedPosition().minus(ChargedUp.vision.getEstimatedPose()).getTranslation().getNorm() <= POSE_MAX_DISPLACEMENT)
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

    public void startStraightPidding() 
    {
        isStraightPidding = true;
        currentStraightAngle = getRobotRotation().getDegrees();
    }

    public void stopStraightPidding() 
    {
        isStraightPidding = false;
    }

    @Override 
    public void periodic() 
    {
        // Logging.info("" + PosPID.KP.get());

        if(!hasDrivenThisUpdate)
        {
            xPID.setMeasurement(getRobotPose().getX());
            yPID.setMeasurement(getRobotPose().getY());
            thetaPID.setMeasurement(getRobotRotationModRotation().getDegrees());

            if (isStraightPidding) 
            {
                if (thetaPID.getTarget() != currentStraightAngle) 
                {
                    thetaPID.setTarget(currentStraightAngle);
                }
                else 
                {
                    thetaPID.updateTarget(currentStraightAngle);
                }
            }

            xPID.update();
            yPID.update();
            thetaPID.update();
            
            ChassisSpeeds c = getChassisSpeeds(
                thetaPID.getSpeed() * MAX_TURN_SPEED_RAD_PER_S, 
                xPID.getSpeed()     * MAX_SPEED_MPS, 
                yPID.getSpeed()     * MAX_SPEED_MPS
            );

            driveFromChassisSpeeds(c);
        }

        if(!hasUsedDriverInputThisUpdate)
        {
            xInputRateLimiter.reset(0);
            yInputRateLimiter.reset(0);
            thetaInputRateLimiter.reset(0);
        }
        
        Pose2d pose = getRobotDisplayPose();
        ChargedUp.field.setRobotPose(pose);

        hasDrivenThisUpdate = false;
        hasUsedDriverInputThisUpdate = false;
    }
    
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

    public Rotation2d getRobotRotationModRotation()
    {
        return Rotation2d.fromDegrees(getRobotRotation().getDegrees() % 360);
    }

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

    public void setTargetAngle(double angle)
    {
        thetaPID.setTarget(angle);
    }

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
    
    public Pose2d getRobotDisplayPose()
    {
        if(RobotBase.isReal())
        {
            return poseEstimator.getEstimatedPosition();
        }
        else 
        {
            return simulation.accumulatedPose;
        }
    }
    
    public void increaseMaxSpeed()
    {
        speedIndex = (speedIndex == SPEEDS.length-1) ? speedIndex : speedIndex + 1;
    }
    public void decreaseMaxSpeed() 
    {
        speedIndex = (speedIndex == 0) ? 0 : speedIndex - 1;
    }

    public double[] getCurrentSpeedLimits() {return SPEEDS[speedIndex];}

    public boolean isThetaPIDFree()
    {
        return !thetaPID.active();
    }

    public double getObjectAngle() 
    {
        return getRobotRotation().getDegrees() + ChargedUp.vision.getObjectAX() * 20; 
    }

    @Override
    public void reset() {
        stopStraightPidding();
    }

    public final DriveCommands commands = this.new DriveCommands();
    public class DriveCommands 
    {
        private DriveCommands() {}

        public Command increaseSpeed()
        {
            return Commands.runOnce(() -> Drivetrain.this.increaseMaxSpeed());
        }
        public Command decreaseSpeed()
        {
            return Commands.runOnce(() -> Drivetrain.this.decreaseMaxSpeed());
        }

        public Command driveInstant(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.runOnce(() -> Drivetrain.this.set(omega_rad_per_second, vx_meter_per_second, -vy_meter_per_second), Drivetrain.this);
        }
        public Command driveForTime(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.sequence
            (
                disableFieldRelative(),
                Commands.run(() -> Drivetrain.this.set(omega_rad_per_second, vx_meter_per_second, vy_meter_per_second), Drivetrain.this)
                    .raceWith(Commands.waitSeconds(time))
            ).finallyDo(__ -> ChargedUp.drivetrain.enableFieldRelative());
        }

        /**
         * Lock the robot's angle
         * @return
         */
        public Command enableStraightPidding() 
        {
            return Commands.runOnce(() -> Drivetrain.this.startStraightPidding());
        }

        /**
         * Stop locking the robot's angle
         * @return
         */
        public Command disableStraightPidding() 
        {
            return Commands.runOnce(() -> Drivetrain.this.stopStraightPidding());
        }

        public Command enableFieldRelative()
        {
            return Commands.runOnce(Drivetrain.this::enableFieldRelative, Drivetrain.this);
        }
        public Command disableFieldRelative()
        {
            return Commands.runOnce(Drivetrain.this::disableFieldRelative, Drivetrain.this);
        }
        public Command toggleFieldRelative()
        {
            return Commands.runOnce(() -> useFieldRelative = !useFieldRelative, Drivetrain.this);
        }


        // public Command follow(PathPlannerTrajectory trajectory)
        // {
        //     return Commands.race(
        //         new PPSwerveControllerCommand(
        //             trajectory, 
        //             Drivetrain.this::getRobotPose,
        //             KINEMATICS, 
        //             xController, 
        //             yController, 
        //             thetaController,
        //             Drivetrain.this::driveFromStates, 
        //             Drivetrain.this
        //         ),
        //         Commands.run(() -> ChargedUp.field.getObject("trajectory").setTrajectory(trajectory))
        //     );
        // }

        public SwerveAutoBuilder autoBuilder(HashMap<String, Command> markers)
        {
            return new SwerveAutoBuilder(
                Drivetrain.this::getRobotPose,
                Drivetrain.this::setRobotPose,
                KINEMATICS,
                PosPID.consts(), 
                ThetaPID.consts(),
                Drivetrain.this::driveFromStates,
                markers,
                false,
                Drivetrain.this
            );
        }

        public Command auto(PathPlannerTrajectory trajectory, HashMap<String, Command> markers)
        {
            SwerveAutoBuilder b = autoBuilder(markers);
            return b.fullAuto(trajectory);
        }
        public Command goToAngle(double angle)
        {
            return thetaPID.goToSetpoint(angle);
        }

    }
}
