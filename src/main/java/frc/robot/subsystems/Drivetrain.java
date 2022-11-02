package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
import static org.team555.units.StandardUnits.*;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.WheelBases;

public class Drivetrain extends ManagerSubsystemBase
{
    SwerveDriveKinematics sdk;

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
}
