package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Commands;
import org.team555.math.MathUtils;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import frc.robot.ChargedUp;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.SwerveTrajectory;
import frc.robot.structure.factories.PoseFactory;
import frc.robot.structure.helpers.Logging;
import frc.robot.structure.swerve.SwerveModuleSpec;

import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import com.swervedrivespecialties.swervelib.SwerveModule;

import static frc.robot.constants.Constants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Drivetrain extends SubsystemBase
{
    private final SwerveModule[] modules;
    private final SwerveDriveOdometry odometry;

    public final PIDController xController;
    public final PIDController yController;
    public final PIDController thetaController;
    
    private Pose2d estimatedPose;
    private double currentRelativeXVel, currentRelativeYVel, currentOmega;
    private double currentSimulationX, currentSimulationY, currentSimulationTheta;

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
        currentRelativeXVel = 0;
        currentRelativeYVel = 0;
        currentOmega = 0;

        currentSimulationX = 0;
        currentSimulationY = 0;
        currentSimulationTheta = 0;

        // Build Modules //
        modules = new SwerveModule[Drive.MODULE_COUNT];

        int i = 0;
        for(SwerveModuleSpec spec : Drive.MODULES)
        {
            modules[i] = spec.createFalconDriveNeoTurn(
                Shuffleboard.getTab("Drivetrain")
                    .getLayout("Module " + MODULE_NAMES[i], BuiltInLayouts.kList)
                    .withSize(2, 5)
                    .withPosition(2*i, 0)
            );
            i++;
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
            Drive.POSITIONS,
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
     * Takes joystick inputs for turning and driving and converts them to velocities for the robot.
     * Should be called in order to manually control the robot.
     * Sets the speeds of the motors directly
     * @param turn the turn axis of the joystick
     * @param drive the driving axis of the joystick
     */
    public void driveInput(JoystickInput turn, JoystickInput drive)
    {
        ControlScheme.TURN_ADJUSTER.adjustX(turn);
        ControlScheme.DRIVE_ADJUSTER.adjustMagnitude(drive);

        drive(
            +turn.getX()  * Drive.MAX_TURN_SPEED_RAD_PER_S,
            -drive.getY() * Drive.MAX_SPEED_MPS,
            +drive.getX() * Drive.MAX_SPEED_MPS
        );
    }
    
    /**
     * Driving with the given velocities 
     * @param omega_rad_per_second rotational velocity in radians per second
     * @param vx_meter_per_second x direction velocity 
     * @param vy_meter_per_second y direction velocity
     */
    public void drive(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        // Rotate so that the front is the real front of the robot
        double newvx = Robot.NAVX_OFFSET.getCos() * vx_meter_per_second - Robot.NAVX_OFFSET.getSin() * vy_meter_per_second;
        double newvy = Robot.NAVX_OFFSET.getSin() * vx_meter_per_second + Robot.NAVX_OFFSET.getCos() * vy_meter_per_second;
        
        // TODO: why do we need to negate the y velocity here?
        // TODO: should this negation be reflected in "currentYVel"?

        // Get the states for the modules
        ChassisSpeeds chassisSpeeds = getChassisSpeeds(omega_rad_per_second, newvx, newvy);

        // Actually drive
        driveFromChassisSpeeds(chassisSpeeds);
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
        // TODO: why do we need to negate the y velocity here?
        // TODO: unflip omega and fix with input flipping
        adjusted_vy    = MathUtils.clamp(adjusted_vx,     -Drive.MAX_SPEED_MPS,            Drive.MAX_SPEED_MPS);
        adjusted_vx    = MathUtils.clamp(adjusted_vy,     -Drive.MAX_SPEED_MPS,            Drive.MAX_SPEED_MPS);
        adjusted_omega = -MathUtils.clamp(adjusted_omega, -Drive.MAX_TURN_SPEED_RAD_PER_S, Drive.MAX_TURN_SPEED_RAD_PER_S);
        adjusted_vx *= Drive.speeds[speedIndex][0];
        adjusted_vy *= Drive.speeds[speedIndex][0];
        adjusted_omega *= Drive.speeds[speedIndex][1];

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
        currentRelativeXVel = speeds.vxMetersPerSecond;
        currentRelativeYVel = speeds.vyMetersPerSecond;
        currentOmega = speeds.omegaRadiansPerSecond;

        if(RobotBase.isReal())
        {
            SwerveModuleState[] states = Drive.KINEMATICS.toSwerveModuleStates(speeds);
            driveFromStates(states);
        }
        else
        {
            /*
            var s = new Translation2d(currentRelativeXVel, currentRelativeYVel);
            s = s.rotateBy(new Rotation2d(-currentSimulationTheta));

            currentSimulationX += s.getX() * TimedRobot.kDefaultPeriod;
            currentSimulationY += s.getY() * TimedRobot.kDefaultPeriod;
            currentSimulationTheta += currentOmega * TimedRobot.kDefaultPeriod;
            */
        }
    }

    private void driveFromStates(SwerveModuleState[] states)
    {
        // Only run this code if in real mode
        if(RobotBase.isReal())
        {
            SwerveDriveKinematics.desaturateWheelSpeeds(states, Drive.MAX_SPEED_MPS);

            for(int i = 0; i < Drive.MODULE_COUNT; i++)
            {
                modules[i].set(
                    states[i].speedMetersPerSecond / Drive.MAX_SPEED_MPS * Drive.MAX_VOLTAGE_V,
                    states[i].angle.getRadians()
                );
            }
            
            odometry.update(
                getRobotRotation(), 
                getModulePositions()
            );
        }
    }

    public SwerveModulePosition[] getModulePositions()
    {
        return Arrays.stream(modules)
            .map(x -> x.getPosition())
            .toArray(SwerveModulePosition[]::new);
    }

    @Override 
    public void periodic() 
    {
        ChargedUp.field.setRobotPose(getRobotPose());
    }

    public void setRobotPose(Pose2d pose)
    {
        odometry.resetPosition(
            getRobotRotation(),
            getModulePositions(),
            pose
        );

        currentSimulationX = pose.getX();
        currentSimulationY = pose.getY();
        currentSimulationTheta = pose.getRotation().getRadians();
    }

    public Rotation2d getRobotRotation()
    {
        if(RobotBase.isReal())
        {
            return ChargedUp.gyroscope.getRotation2d();
        }
        else 
        {
            return new Rotation2d(currentSimulationTheta);
        }
    }

    public Pose2d getRobotPose()
    {
        if(RobotBase.isReal())
        {
            return odometry.getPoseMeters();
        }
        else 
        {
            return new Pose2d(currentSimulationX, currentSimulationY, getRobotRotation());
        }
    }

    public void increaseMaxSpeed()
    {
        speedIndex = (speedIndex == Drive.speeds.length-1) ? speedIndex : speedIndex + 1;
    }
    public void decreaseMaxSpeed() 
    {
        speedIndex = (speedIndex == 0) ? 0 : speedIndex - 1;
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
            return Commands.runOnce(() -> Drivetrain.this.drive(omega_rad_per_second, vx_meter_per_second, -vy_meter_per_second), Drivetrain.this);
        }
        public Command driveForTime(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.parallel
            (
                enableFieldRelative(),
                Commands.run(() -> Drivetrain.this.drive(omega_rad_per_second, vx_meter_per_second, -vy_meter_per_second), Drivetrain.this)
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
    }
}
