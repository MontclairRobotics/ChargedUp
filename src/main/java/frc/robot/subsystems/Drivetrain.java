package frc.robot.subsystems;

import org.team555.frc.command.Commands;
import org.team555.math.MathUtils;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.ChargedUp;
import frc.robot.Input;
import com.swervedrivespecialties.swervelib.SwerveModule;

import static frc.robot.constants.Constants.*;

public class Drivetrain extends SubsystemBase
{
    public final SwerveModule[] modules;
    public final SwerveDriveKinematics kinematics;
    public final SwerveDriveOdometry odometry;

    private SwerveModuleState[] states;

    private boolean useFieldRelative = true;

    private static final String[] moduleNames = {
        "FL",
        "FR",
        "BL",
        "BR"
    };

    public Drivetrain()
    {
        // Build Modules //
        modules = new SwerveModule[Drive.MODULE_COUNT];

        int i = 0;
        for(var spec : Drive.MODULES)
        {
            modules[i] = spec.createNeo(
                Shuffleboard.getTab("Drivetrain").getLayout("Module " + moduleNames[i], BuiltInLayouts.kList)
            );
            i++;
        }

        // Build Kinematics //
        kinematics = new SwerveDriveKinematics(
            new Translation2d( Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2), //FL
            new Translation2d( Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2), //FR
            new Translation2d(-Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2), //BL
            new Translation2d(-Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2)  //BR
        );

        // Build Odometry //
        odometry = new SwerveDriveOdometry(
            kinematics, 
            ChargedUp.gyroscope.getRotation2d(), 
            new Pose2d(5,5,Rotation2d.fromDegrees(0))
        );
    }

    public void enableFieldRelative()  {useFieldRelative = true;}
    public void disableFieldRelative() {useFieldRelative = false;}

    public void driveInput(double turn_axis, double x_axis, double y_axis)
    {
        drive(
            Input.adjust(turn_axis, ControlScheme.DEADBAND, ControlScheme.INPUT_POWER) * Drive.MAX_TURN_SPEED_RAD_PER_S,
            Input.adjust(x_axis,    ControlScheme.DEADBAND, ControlScheme.INPUT_POWER) * Drive.MAX_SPEED_MPS,
            Input.adjust(y_axis,    ControlScheme.DEADBAND, ControlScheme.INPUT_POWER) * Drive.MAX_SPEED_MPS
        );
    }
    
    public void drive(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        states = kinematics.toSwerveModuleStates(getChassisSpeeds(omega_rad_per_second, vx_meter_per_second, vy_meter_per_second));
    }

    private ChassisSpeeds getChassisSpeeds(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        vx_meter_per_second  = MathUtils.clamp(vx_meter_per_second,  -Drive.MAX_SPEED_MPS, Drive.MAX_SPEED_MPS);
        vy_meter_per_second  = MathUtils.clamp(vy_meter_per_second,  -Drive.MAX_SPEED_MPS, Drive.MAX_SPEED_MPS);
        omega_rad_per_second = MathUtils.clamp(omega_rad_per_second, -Drive.MAX_TURN_SPEED_RAD_PER_S, Drive.MAX_TURN_SPEED_RAD_PER_S);

        if(useFieldRelative)
        {
            return ChassisSpeeds.fromFieldRelativeSpeeds(
                vx_meter_per_second,
                vy_meter_per_second,
                omega_rad_per_second, 
                ChargedUp.gyroscope.getRotation2d()
            );
        }
        else
        {
            return new ChassisSpeeds(vx_meter_per_second, vy_meter_per_second, omega_rad_per_second);
        }
    }

    @Override
    public void periodic() 
    {
        if(states == null) return;

        var nstates = states.clone();
        SwerveDriveKinematics.desaturateWheelSpeeds(nstates, Drive.MAX_SPEED_MPS);

        for(int i = 0; i < Drive.MODULE_COUNT; i++)
        {
            modules[i].set(
                nstates[i].speedMetersPerSecond / Drive.MAX_SPEED_MPS * Drive.MAX_VOLTAGE_V,
                nstates[i].angle.getRadians()
            );
        }
    }

    public final DriveCommands commands = this.new DriveCommands();
    public class DriveCommands 
    {
        private DriveCommands() {}

        public Command drive(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.instant(() -> Drivetrain.this.drive(omega_rad_per_second, vx_meter_per_second, vy_meter_per_second), Drivetrain.this);
        }
        public Command driveForTime(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
        {
            return Commands.runForTime(time, () -> Drivetrain.this.drive(omega_rad_per_second, vx_meter_per_second, vy_meter_per_second), Drivetrain.this);
        }
        public Command enableFieldRelative()
        {
            return Commands.instant(Drivetrain.this::enableFieldRelative, Drivetrain.this);
        }
        public Command disableFieldRelative()
        {
            return Commands.instant(Drivetrain.this::disableFieldRelative, Drivetrain.this);
        }
    }
}
