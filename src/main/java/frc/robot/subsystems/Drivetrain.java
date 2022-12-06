package frc.robot.subsystems;

import org.team555.frc.command.Commands;
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
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.ChargedUp;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.Logging;
import frc.robot.structure.Trajectories;

import com.swervedrivespecialties.swervelib.SwerveModule;

import static frc.robot.constants.Constants.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Drivetrain extends SubsystemBase
{
    public final SwerveModule[] modules;
    public final SwerveDriveKinematics kinematics;
    public final SwerveDriveOdometry odometry;
    public final HolonomicDriveController driveController;

    public final PIDController xController;
    public final PIDController yController;
    public final ProfiledPIDController thetaController;

    private Trajectory.State currentState;

    private ChassisSpeeds chassisSpeeds;

    private double currentXVel, currentYVel;

    private boolean useFieldRelative = true;

    private static final String[] MODULE_NAMES = {
        "FL",
        "FR",
        "BL",
        "BR"
    };

    public Drivetrain()
    {
        currentXVel = 0;
        currentYVel = 0;

        // Build Modules //
        modules = new SwerveModule[Drive.MODULE_COUNT];

        int i = 0;
        for(var spec : Drive.MODULES)
        {
            modules[i] = spec.createNeo(
                Shuffleboard.getTab("Drivetrain")
                    .getLayout("Module " + MODULE_NAMES[i], BuiltInLayouts.kList)
                    .withSize(2, 5)
                    .withPosition(2*i, 0)
            );
            i++;
        }

        // Build Shuffleboard //
        Shuffleboard.getTab("Main")
            .addString("Log", Logging::logString)
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2, 3)
            .withPosition(0, 1);

        Shuffleboard.getTab("Main")
            .addBoolean("Field Relative", () -> useFieldRelative)
            .withWidget(BuiltInWidgets.kBooleanBox)
            .withSize(2, 1)
            .withPosition(0, 0);

        Shuffleboard.getTab("Main")
            .addNumber("Direction", () -> {
                var y = ChargedUp.gyroscope.getYaw();
                return y > 0 ? y : 360+y; 
            })
            .withWidget(BuiltInWidgets.kGyro)
            .withSize(2, 2)
            .withPosition(2, 0);

        // Build Kinematics //
        kinematics = new SwerveDriveKinematics(
            new Translation2d(-Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2), //FL
            new Translation2d( Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2), //FR
            new Translation2d(-Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2), //BL
            new Translation2d( Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2)  //BR
        );

        // Build Odometry //
        odometry = new SwerveDriveOdometry(
            kinematics, 
            ChargedUp.gyroscope.getRotation2d(), 
            new Pose2d(5,5,Rotation2d.fromDegrees(0))
        );

        // Build PID Controllers //
        xController = new PIDController(
            Drive.XPID.KP,
            Drive.XPID.KI, 
            Drive.XPID.KD
        );

        yController = new PIDController(
            Drive.YPID.KP,
            Drive.YPID.KI, 
            Drive.YPID.KD
        );

        thetaController = new ProfiledPIDController(
            Drive.ThetaPID.KP,
            Drive.ThetaPID.KI, 
            Drive.ThetaPID.KD, 
            new TrapezoidProfile.Constraints(
                Drive.MAX_SPEED_MPS, 
                Drive.MAX_TURN_ACCEL_RAD_PER_S2
            )
        );

        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        // Build Drive Controller //
        driveController = new HolonomicDriveController(xController, yController, thetaController);
    }

    public void enableFieldRelative()  
    {
        useFieldRelative = true;
        Logging.Info("Field relative enabled");
    }
    public void disableFieldRelative() 
    {
        useFieldRelative = false;
        Logging.Info("Field relative disabled");
    }

    public void driveInput(JoystickInput turn, JoystickInput drive)
    {
        ControlScheme.TURN_ADJUSTER.adjustX(turn);
        ControlScheme.DRIVE_ADJUSTER.adjustMagnitude(drive);

        drive(
            turn.getX()  * Drive.MAX_TURN_SPEED_RAD_PER_S,
            drive.getX() * Drive.MAX_SPEED_MPS,
            drive.getY() * Drive.MAX_SPEED_MPS
        );
    }
    
    public void drive(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        // Rotate so that the front is the real front of the robot
        var newvx = Robot.NAVX_OFFSET.getCos() * vx_meter_per_second - Robot.NAVX_OFFSET.getSin() * vy_meter_per_second;
        var newvy = Robot.NAVX_OFFSET.getSin() * vx_meter_per_second + Robot.NAVX_OFFSET.getCos() * vy_meter_per_second;
        
        // TODO: why do we need to negate the y velocity here?
        // TODO: should this negation be reflected in "currentYVel"?
        newvy = -newvy;

        // Get the states for the modules
        chassisSpeeds = getChassisSpeeds(omega_rad_per_second, newvx, newvy);

        // Set the current velcocities
        currentXVel = vx_meter_per_second;
        currentYVel = vy_meter_per_second;
    }

    private ChassisSpeeds getChassisSpeeds(double adjusted_omega, double adjusted_vx, double adjusted_vy)
    {
        // TODO: why do we need to negate the y velocity here?
        // TODO: unflip omega and fix with input flipping
        adjusted_vx    = MathUtils.clamp(adjusted_vx,    -Drive.MAX_SPEED_MPS,            Drive.MAX_SPEED_MPS);
        adjusted_vy    = MathUtils.clamp(adjusted_vy,    -Drive.MAX_SPEED_MPS,            Drive.MAX_SPEED_MPS);
        adjusted_omega = -MathUtils.clamp(adjusted_omega, -Drive.MAX_TURN_SPEED_RAD_PER_S, Drive.MAX_TURN_SPEED_RAD_PER_S);

        if(useFieldRelative)
        {
            return ChassisSpeeds.fromFieldRelativeSpeeds(
                adjusted_vx,
                adjusted_vy,
                adjusted_omega, 
                ChargedUp.gyroscope.getRotation2d()
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

    public void driveFromChassisSpeeds() 
    {
        if(chassisSpeeds == null) return;

        var states = kinematics.toSwerveModuleStates(chassisSpeeds);

        SwerveDriveKinematics.desaturateWheelSpeeds(states, Drive.MAX_SPEED_MPS);

        for(int i = 0; i < Drive.MODULE_COUNT; i++)
        {
            modules[i].set(
                states[i].speedMetersPerSecond / Drive.MAX_SPEED_MPS * Drive.MAX_VOLTAGE_V,
                states[i].angle.getRadians()
            );
        }
        
        odometry.update(ChargedUp.gyroscope.getRotation2d(), states);
    }

    public void autoPeriodic()
    {
        chassisSpeeds = driveController.calculate(
            odometry.getPoseMeters(), 
            currentState,
            ChargedUp.gyroscope.getRotation2d()
        );
        
        if (driveController.atReference())
        {
            currentState = null;
        }
    }

    @Override public void periodic()
    {
        if(currentState != null) autoPeriodic();
        driveFromChassisSpeeds();

        ChargedUp.field.setRobotPose(odometry.getPoseMeters());
    }

    public final DriveCommands commands = this.new DriveCommands();
    public class DriveCommands 
    {
        private DriveCommands() {}

        public Command drive(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.instant(() -> Drivetrain.this.drive(omega_rad_per_second, vx_meter_per_second, -vy_meter_per_second), Drivetrain.this);
        }
        public Command driveForTime(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            enableFieldRelative();
            return Commands.runForTime(time, () -> Drivetrain.this.drive(omega_rad_per_second, vx_meter_per_second, -vy_meter_per_second), Drivetrain.this);
        }
        public Command enableFieldRelative()
        {
            return Commands.instant(Drivetrain.this::enableFieldRelative, Drivetrain.this);
        }
        public Command disableFieldRelative()
        {
            return Commands.instant(Drivetrain.this::disableFieldRelative, Drivetrain.this);
        }
        public Command followTrajectory(Trajectory trajectory)
        {
            return new Command() 
            {
                @Override public Set<Subsystem> getRequirements() {return Set.of(Drivetrain.this);}

                public List<Trajectory.State> states = trajectory.getStates();
                public int state = 0;

                @Override
                public void execute() 
                {
                    if(state == states.size()) return;

                    Drivetrain.this.currentState = states.get(state);
                    Drivetrain.this.autoPeriodic();

                    if(Drivetrain.this.currentState == null)
                    {
                        state++;
                    }
                }

                @Override
                public void end(boolean interrupted) {
                    Drivetrain.this.currentState = null;
                }
                
                @Override
                public boolean isFinished() {
                    return state == states.size();
                }
            };
        }
    }
}
