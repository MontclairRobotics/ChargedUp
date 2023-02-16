package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.ChargedUp;
import frc.robot.framework.Math555;
import frc.robot.framework.commandrobot.CommandRobot;
import frc.robot.framework.commandrobot.ManagerSubsystemBase;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.PIDMechanism;
import frc.robot.structure.Tracking;
import frc.robot.structure.helpers.Logging;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import com.revrobotics.CANSparkMax;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SwerveModule;

import static frc.robot.constants.Constants.*;

import java.util.Arrays;
import java.util.HashMap;

public class Drivetrain extends ManagerSubsystemBase
{
    private final SwerveModule[] modules;
    private final SwerveDriveOdometry odometry;

    public final PIDController xController;
    public final PIDController yController;
    public final PIDController thetaController;

    public final PIDMechanism xPID;
    public final PIDMechanism yPID;
    public final PIDMechanism thetaPID;

    private Tracking objectTracked; 
    
    private Pose2d estimatedPose;
    private Transform2d currentTransform;

    private boolean useFieldRelative = true;


    //sets the speedIndex to speeds length
    private static int speedIndex = Drive.speeds.length-1;

    private static final String[] MODULE_NAMES = {
        "FL",
        "FR",
        "BL",
        "BR"
    };

    public Drivetrain()
    {
        estimatedPose = new Pose2d(0, 0, new Rotation2d());
        currentTransform = new Transform2d();

        // Build Modules //
        modules = new SwerveModule[Drive.MODULE_COUNT];

        // TODO: UNCOMMENT ALL OF THIS

        for (int i = 0; i < Drive.MODULES.length; i++)
        // for(SwerveModuleSpec spec : Drive.MODULES)
        {
            modules[i] = Drive.MODULES[i].build(
                Shuffleboard.getTab("Drivetrain")
                    .getLayout("Module " + MODULE_NAMES[i], BuiltInLayouts.kList)
                    .withSize(2, 5)
                    .withPosition(2*i, 0)
            );

            assert Drive.STEER_TYPE == MotorType.NEO 
                 : "This code assumes that the steer type of the robot is a neo.";

            CANSparkMax mot = (CANSparkMax) modules[i].getSteerMotor();
            mot.burnFlash();
        }

        // Build Shuffleboard //
        Shuffleboard.getTab("Main")
            .addBoolean("Field Relative", () -> useFieldRelative)
            .withWidget(BuiltInWidgets.kBooleanBox)
            .withSize(2, 1)
            .withPosition(0, 0);

        Shuffleboard.getTab("Main")
            .addNumber("Gyroscope", () -> {
                double y = ChargedUp.gyroscope.getYaw();
                return y > 0 ? y : 360+y; 
            })
            .withWidget(BuiltInWidgets.kGyro)
            .withSize(2, 2)
            .withPosition(2, 0);
        
        Shuffleboard.getTab("Main")
            .addNumber("Max linear speed: ", () -> Drive.speeds[speedIndex][0])
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2, 2)
            .withPosition(4, 0);
        
        Shuffleboard.getTab("Main")
            .addNumber("Max angular speed: ", () -> Drive.speeds[speedIndex][1])
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2, 2)
            .withPosition(4, 2);

        // Build Odometry //
        odometry = new SwerveDriveOdometry(
            Drive.KINEMATICS, 
            getRobotRotation(), 
            getModulePositions(),
            new Pose2d()
        );

        // Build PID Controllers //
        xController = new PIDController(
            Drive.PosPID.KP,
            Drive.PosPID.KI, 
            Drive.PosPID.KD
        );

        yController = new PIDController(
            Drive.PosPID.KP,
            Drive.PosPID.KI, 
            Drive.PosPID.KD
        );

        thetaController = new PIDController(
            Drive.ThetaPID.KP,
            Drive.ThetaPID.KI, 
            Drive.ThetaPID.KD
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
            +turn.getX()  * Drive.MAX_TURN_SPEED_RAD_PER_S,
            +drive.getY() * Drive.MAX_SPEED_MPS,
            +drive.getX() * Drive.MAX_SPEED_MPS
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
        vx_meter_per_second  = +Math555.clamp(vx_meter_per_second,  -Drive.MAX_SPEED_MPS,            Drive.MAX_SPEED_MPS);
        vy_meter_per_second  = +Math555.clamp(vy_meter_per_second,  -Drive.MAX_SPEED_MPS,            Drive.MAX_SPEED_MPS);
        omega_rad_per_second = +Math555.clamp(omega_rad_per_second, -Drive.MAX_TURN_SPEED_RAD_PER_S, Drive.MAX_TURN_SPEED_RAD_PER_S);
        
        vx_meter_per_second  *= Drive.speeds[speedIndex][0];
        vy_meter_per_second  *= Drive.speeds[speedIndex][0];
        omega_rad_per_second *= Drive.speeds[speedIndex][1];

        // Logging.info("omega rad per sec = " + omega_rad_per_second);
        
        // Logging.info("Front Left: "  + Units.radiansToDegrees(modules[0].getSteerEncoder().getAbsoluteAngle()));
        // Logging.info("Front Right: " + Units.radiansToDegrees(modules[1].getSteerEncoder().getAbsoluteAngle()));
        // Logging.info("back Left: "   + Units.radiansToDegrees(modules[2].getSteerEncoder().getAbsoluteAngle()));
        // Logging.info("back right: "  + Units.radiansToDegrees(modules[3].getSteerEncoder().getAbsoluteAngle()));
        


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
     * Uses the chassis speeds to drive. 
     * Only works when the robot is real currently
     * @param speeds ChassisSpeeds object
     */
    public void driveFromChassisSpeeds(ChassisSpeeds speeds) 
    {
        SwerveModuleState[] states = Drive.KINEMATICS.toSwerveModuleStates(speeds);
        driveFromStates(states);
    }

    /**
     * Converts speed of each swerve module from meters/second to voltage and converts angle to radians. 
     * Then, updates odometry with new values. 
     * @param states array of {@link SwerveModuleState}, contains all four states
     */
    private void driveFromStates(SwerveModuleState[] states)
    {
        SwerveDriveKinematics.desaturateWheelSpeeds(states, Drive.MAX_SPEED_MPS);
        
        for(int i = 0; i < Drive.MODULE_COUNT; i++)
        {
            states[i] = SwerveModuleState.optimize(
                states[i], 
                new Rotation2d(modules[i].getSteerAngle())
            );

            modules[i].set(
                states[i].speedMetersPerSecond / Drive.MAX_SPEED_MPS * Drive.MAX_VOLTAGE_V,
                states[i].angle.getRadians()
            );
        }

        if(RobotBase.isSimulation())
        {
            Rotation2d robotAngle = getRobotRotation();

            Translation2d vel = null;
            Rotation2d omega  = null;

            for (int i = 0; i < states.length; i++) 
            {
                Translation2d pos2d = Drive.MOD_POSITIONS[i];
                Rotation2d modAngle = states[i].angle;

                Rotation2d relAngle = modAngle.minus(robotAngle);
                vel = new Translation2d(states[i].speedMetersPerSecond, relAngle);

                double omegaDbl = pos2d.getNorm() 
                                * vel.getNorm() 
                                * pos2d.minus(vel).getAngle().getSin();
                            
                omega = new Rotation2d(omegaDbl);
            }

            Transform2d transform = new Transform2d(vel, omega);
                // System.out.println(transform);  
            transform = transform.times(CommandRobot.deltaTime());

            estimatedPose = estimatedPose.plus(transform);
        }
        
        odometry.update(
            getRobotRotation(), 
            getModulePositions()
        );
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
    public void always() 
    {
        ChargedUp.field.setRobotPose(getRobotPose());

        xPID.setMeasurement(getRobotPose().getX());
        yPID.setMeasurement(getRobotPose().getY());
        thetaPID.setMeasurement(getRobotRotation().getDegrees());

        xPID.update();
        yPID.update();
        thetaPID.update();

        ChassisSpeeds c = getChassisSpeeds(thetaPID.getSpeed(), xPID.getSpeed(), yPID.getSpeed());
        
        driveFromChassisSpeeds(c);
    }
    
    public void setRobotPose(Pose2d pose)
    {
        odometry.resetPosition(
            getRobotRotation(),
            getModulePositions(),
            pose
        );
        
        estimatedPose = pose;
    }

    public Rotation2d getRobotRotation()
    {
        if(RobotBase.isReal())
        {
            return ChargedUp.gyroscope.getRotation2d();
        }
        else 
        {
            return estimatedPose.getRotation();
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
            return odometry.getPoseMeters();
        }
        else 
        {
            return estimatedPose;
        }
    }

    public void trackObject(Tracking object) 
    {
        ChargedUp.limelight.setPipeline(object.getPipelineNum());
        objectTracked = object;
    }

    public void stopTracking() 
    {
        objectTracked = Tracking.NONE;
    }
    
    public void increaseMaxSpeed()
    {
        speedIndex = (speedIndex == Drive.speeds.length-1) ? speedIndex : speedIndex + 1;
    }
    public void decreaseMaxSpeed() 
    {
        speedIndex = (speedIndex == 0) ? 0 : speedIndex - 1;
    }

    public boolean isThetaPIDFree()
    {
        return !thetaPID.active();
    }


    public double getObjectAngle() 
    {
        if (objectTracked == Tracking.NONE) return 0;
        return getRobotRotation().getDegrees() + ChargedUp.limelight.getX(); 
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

        // VISION COMMANDS
        public Command turnToTarget() 
        {
            return Commands.run(() -> ChargedUp.drivetrain.setTargetAngle(getObjectAngle()))
                .until(() -> isThetaPIDFree());
        }

        /**
         * Goes towards the object on the x-axis.
         * @return
         */
        public Command moveToObjectSideways()
        {
            return Commands.run(() -> ChargedUp.drivetrain.xPID.setTarget(getRobotPose().getX() + 0.05 * ChargedUp.drivetrain.getObjectAngle()))
                .until(() -> !xPID.active());
        }

        public Command follow(PathPlannerTrajectory trajectory)
        {
            return new PPSwerveControllerCommand(
                trajectory, 
                Drivetrain.this::getRobotPose,
                Drive.KINEMATICS, 
                xController, 
                yController, 
                thetaController,
                Drivetrain.this::driveFromStates, 
                Drivetrain.this
            );
        }

        public SwerveAutoBuilder autoBuilder(HashMap<String, Command> markers)
        {
            return new SwerveAutoBuilder(
                Drivetrain.this::getRobotPose,
                Drivetrain.this::setRobotPose,
                Drive.KINEMATICS,
                Drive.PosPID.KConsts, 
                Drive.ThetaPID.KConsts,
                Drivetrain.this::driveFromStates,
                markers
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
