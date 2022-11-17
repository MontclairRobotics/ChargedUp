package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
import org.team555.units.Quantity;

import static org.team555.units.StandardUnits.*;

import java.util.Arrays;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.Drivebase;
import frc.robot.constants.WheelBases;
import frc.robot.structure.SwerveModule;

public class Drivetrain extends ManagerSubsystemBase
{
    private SwerveDriveKinematics sdk;

    private SwerveModule[] modules;

    public Drivetrain()
    {
        sdk = new SwerveDriveKinematics(
            new Translation2d( // FRONT LEFT
                WheelBases.FrontLeft_X.value(meter),
                WheelBases.FrontLeft_Y.value(meter)
            ),
            new Translation2d( // FRONT RIGHT
                WheelBases.FrontRight_X.value(meter),
                WheelBases.FrontRight_Y.value(meter)
            ),
            new Translation2d( // BACK LEFT
                WheelBases.BackLeft_X.value(meter),
                WheelBases.BackLeft_Y.value(meter)
            ),
            new Translation2d( // BACK RIGHT
                WheelBases.BackRight_X.value(meter),
                WheelBases.BackRight_Y.value(meter)
            )
        );

        modules = new SwerveModule[Drivebase.PORTS.length];

        for(int i = 0; i < modules.length; i++)
        {
            modules[i] = new SwerveModule(
                Drivebase.PORTS[i][0],
                Drivebase.TYPES[i][0],
                Drivebase.PORTS[i][1],
                Drivebase.TYPES[i][1]
            );
        }
    }

    @Override
    public void reset() 
    {
        
    }

    public void driveFromInput(double turn, double xdrive, double ydrive, double fieldAngle)
    {
        drive(
            Drivebase.TURN_SPEED.mul(turn),
            Drivebase.DRIVE_SPEED.mul(xdrive),
            Drivebase.DRIVE_SPEED.mul(ydrive),
            fieldAngle
        );
    }

    public void drive(Quantity omega, Quantity vx, Quantity vy, double fieldAngle)
    {
        var cspeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            vx.value(meter.per(second)), vy.value(meter.per(second)), 
            omega.value(radian.per(second)), 
            new Rotation2d(fieldAngle)
        );

        var states = sdk.toSwerveModuleStates(cspeeds);

        for(int i = 0; i < modules.length; i++)
        {
            modules[i].setState(states[i]);
        }
    }
}
