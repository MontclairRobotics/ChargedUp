package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
import org.team555.units.Quantity;

import static org.team555.units.StandardUnits.*;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.WheelBases;

public class Drivetrain extends ManagerSubsystemBase
{
    private SwerveDriveKinematics sdk;

    private Spark[] rotators = {

    };
    private Spark[] drivors = {

    };

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
    }

    @Override
    public void reset() 
    {
        
    }

    public void drive(Quantity omega /* rotational velocity */, Quantity vx, Quantity vy, Quantity fieldAngle)
    {
        var cspeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            vx.value(meter.per(second)), vy.value(meter.per(second)), 
            omega.value(radian.per(second)), 
            new Rotation2d(fieldAngle.value(radian))
        );

        var states = sdk.toSwerveModuleStates(cspeeds);

        for(var state : states)
        {
            state.
        }
    }
}
