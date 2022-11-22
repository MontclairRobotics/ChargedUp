package frc.robot.subsystems;

import org.team555.frc.command.Commands;
import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
import org.team555.units.Quantity;

import static org.team555.units.StandardUnits.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.hal.simulation.SimulatorJNI;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.ChargedUp;
import frc.robot.constants.Drivebase;
import frc.robot.constants.RobotSpec;
import frc.robot.constants.SwerveConstants555;
import frc.robot.structure.Logging;
import frc.robot.structure.SwerveDriveModel555;
import frc.swervelib.GyroscopeHelper;
import frc.swervelib.Mk4iSwerveModuleHelper;
import frc.swervelib.SwerveConstants;
import frc.swervelib.SwerveDrivetrainModel;
import frc.swervelib.SwerveInput;
import frc.swervelib.SwerveModule;
import frc.swervelib.SwerveSubsystem;
import frc.swervelib.kauailabs.navXFactoryBuilder;

public class Drivetrain extends SwerveSubsystem
{
    public static SwerveDrivetrainModel buildModel()
    {
        SwerveDrivetrainModel drivetrain;
        ArrayList<SwerveModule> modules;
        
        SwerveConstants555.build();

        modules = new ArrayList<SwerveModule>();

        for(int i = 0; i < 4; i++)
        {
            modules.add(Drivebase.MODULE_SPECS[i].createNeo(
                Shuffleboard.getTab("Swerve")
                            .getLayout("Module " + i, BuiltInLayouts.kList)
            ));
        }
        
        drivetrain = new SwerveDrivetrainModel(
            modules, 
            new navXFactoryBuilder().build(ChargedUp.gyro.getAhrs())
        );

        return drivetrain;
    }

    public Drivetrain()
    {
        super(buildModel());
    }

    public void driveFromInput(double turn, double xdrive, double ydrive)
    {
        dt.setModuleStates(new SwerveInput(turn, xdrive, ydrive));
    }

    public void drive(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        dt.setModuleStates(ChassisSpeeds.fromFieldRelativeSpeeds(
            vx_meter_per_second, 
            vy_meter_per_second, 
            omega_rad_per_second, 
            ChargedUp.gyro.getAngle()
        ));
    }

    public Command driveCommand(double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        return Commands.instant(() -> drive(omega_rad_per_second, vx_meter_per_second, vy_meter_per_second), this);
    }
    public Command driveForTimeCommand(double time, double omega_rad_per_second, double vx_meter_per_second, double vy_meter_per_second)
    {
        return Commands.runForTime(time, () -> drive(omega_rad_per_second, vx_meter_per_second, vy_meter_per_second), this);
    }

    @Override
    public void simulationPeriodic()
    {
        dt.update(
            DriverStation.isDisabled(), 
            13
        );
    }
}
