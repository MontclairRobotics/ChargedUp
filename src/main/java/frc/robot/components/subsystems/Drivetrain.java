package frc.robot.components.subsystems;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.ChargedUp;
import frc.robot.constants.ControlScheme;
import frc.robot.inputs.JoystickInput;
import frc.robot.math.Math555;
import frc.robot.structure.DetectionType;
import frc.robot.structure.Tracking;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.commandrobot.CommandRobot;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;
import frc.robot.vision.VisionSystem;

import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import com.revrobotics.CANSparkMax;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SwerveModule;

import static frc.robot.constants.Constants.*;
import static frc.robot.constants.DriveConstants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.photonvision.EstimatedRobotPose;

public class Drivetrain extends ManagerSubsystemBase
{
    private final SwerveModule[] modules;

    public final PIDController xController;
    public final PIDController yController;
    public final PIDController thetaController;

    public final PIDMechanism xPID;
    public final PIDMechanism yPID;
    public final PIDMechanism thetaPID;

    private boolean useFieldRelative = true;

    private final FieldObject2d moduleObject;
    private final SwerveDrivePoseEstimator poseEstimator;

    /**
     * Store whether or not the drivetrain has already piped values into its motors, signalling an autonomous command
     * if control reaches "periodic" and this is true.
     */
    private boolean hasDrivenThisUpdate = false;

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

            modPoses[i] = getRobotPose().plus(new Transform2d(MOD_POSITIONS[i], new Rotation2d()));

            assert STEER_TYPE == MotorType.NEO
                 : "This code assumes that the steer type of the robot is a neo.";

            CANSparkMax mot = (CANSparkMax) modules[i].getSteerMotor();
            mot.burnFlash();
        }

        moduleObject.setPoses(modPoses);

        // Build Odometry //
        poseEstimator = new SwerveDrivePoseEstimator(
            KINEMATICS, 
            getRobotRotation(), 
            getModulePositions(), 
            new Pose2d()
        );

        // Build PID Controllers //
        xController = new PIDController(
            PosPID.KP,
            PosPID.KI, 
            PosPID.KD
        );

        yController = new PIDController(
            PosPID.KP,
            PosPID.KI, 
            PosPID.KD
        );

        thetaController = new PIDController(
            ThetaPID.KP,
            ThetaPID.KI, 
            ThetaPID.KD
        );

        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        // Build PID Mechanisms //
        xPID = new PIDMechanism(xController);
        yPID = new PIDMechanism(yController);
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
    public boolean usingFieldRelative() {
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
        ControlScheme.TURN_ADJUSTER.adjustX(turn);
        ControlScheme.DRIVE_ADJUSTER.adjustMagnitude(drive);

        set(
            +turn.getX()  * MAX_TURN_SPEED_RAD_PER_S,
            +drive.getY() * MAX_SPEED_MPS,
            +drive.getX() * MAX_SPEED_MPS
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
     * @param omega_rad_per_second rotational velocity in radians per second
     * @param vx_meter_per_second x direction velocity 
     * @param vy_meter_per_second y direction velocity
     */
    public void set(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        vx_meter_per_second  = +Math555.clamp(vx_meter_per_second,  -MAX_SPEED_MPS,            MAX_SPEED_MPS);
        vy_meter_per_second  = +Math555.clamp(vy_meter_per_second,  -MAX_SPEED_MPS,            MAX_SPEED_MPS);
        omega_rad_per_second = +Math555.clamp(omega_rad_per_second, -MAX_TURN_SPEED_RAD_PER_S, MAX_TURN_SPEED_RAD_PER_S);
        
        vx_meter_per_second  *= SPEEDS[speedIndex][0];
        vy_meter_per_second  *= SPEEDS[speedIndex][0];
        omega_rad_per_second *= SPEEDS[speedIndex][1];

        xPID.setSpeed(vx_meter_per_second);
        yPID.setSpeed(vy_meter_per_second);
        thetaPID.setSpeed(omega_rad_per_second);
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
            if(RobotBase.isReal())
            {
                states[i] = SwerveModuleState.optimize(
                    states[i], 
                    new Rotation2d(modules[i].getSteerAngle())
                );
            }

            modules[i].set(
                states[i].speedMetersPerSecond / MAX_SPEED_MPS * MAX_VOLTAGE_V,
                states[i].angle.getRadians()
            );
        }

        if(RobotBase.isSimulation())
        {
            simulation.targetSpeeds = KINEMATICS.toChassisSpeeds(states);

            // System.out.println(simulation.targetSpeeds);

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

        poseEstimator.update(
            getRobotRotation(), 
            getModulePositions()
        );

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

    @Override 
    public void periodic() 
    {
        if(!hasDrivenThisUpdate)
        {
            xPID.setMeasurement(getRobotPose().getX());
            yPID.setMeasurement(getRobotPose().getY());
            thetaPID.setMeasurement(getRobotRotation().getDegrees());

            xPID.update();
            yPID.update();
            thetaPID.update();

            ChassisSpeeds c = getChassisSpeeds(thetaPID.getSpeed(), xPID.getSpeed(), yPID.getSpeed());
            
            driveFromChassisSpeeds(c);
        }
        
        Pose2d pose = getRobotDisplayPose();
        ChargedUp.field.setRobotPose(pose);

        hasDrivenThisUpdate = false;
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
            return Commands.parallel
            (
                enableFieldRelative(),
                Commands.run(() -> Drivetrain.this.set(omega_rad_per_second, vx_meter_per_second, -vy_meter_per_second), Drivetrain.this)
                    .deadlineWith(Commands.waitSeconds(time)),
                disableFieldRelative()
            );
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


        public Command follow(PathPlannerTrajectory trajectory)
        {
            return Commands.race(
                new PPSwerveControllerCommand(
                    trajectory, 
                    Drivetrain.this::getRobotPose,
                    KINEMATICS, 
                    xController, 
                    yController, 
                    thetaController,
                    Drivetrain.this::driveFromStates, 
                    Drivetrain.this
                ),
                Commands.run(() -> ChargedUp.field.getObject("trajectory").setTrajectory(trajectory))
            );
        }

        public SwerveAutoBuilder autoBuilder(HashMap<String, Command> markers)
        {
            return new SwerveAutoBuilder(
                Drivetrain.this::getRobotPose,
                Drivetrain.this::setRobotPose,
                KINEMATICS,
                PosPID.KConsts, 
                ThetaPID.KConsts,
                Drivetrain.this::driveFromStates,
                markers,
                true,
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
            return Commands.run(() -> Drivetrain.this.setTargetAngle(angle), Drivetrain.this)
                .until(Drivetrain.this::isThetaPIDFree);
        }

    }
}
