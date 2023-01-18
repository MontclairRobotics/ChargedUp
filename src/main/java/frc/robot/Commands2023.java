package frc.robot;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.Constants.*;
// import frc.robot.subsystems.Grabber;
// import frc.robot.subsystems.Shwooper;
import frc.robot.subsystems.Stinger;

public class Commands2023 
{
    // put commands here

    private static Stinger stinger = new Stinger();

    /**
     * fully retracts the stinger 
     */
    public static Command retractStinger()
    {
        return Commands.instant(() -> {
            stinger.fullyRetract();
        });
    }
    /**
     * extends the stinger to the length of the midlle pole
     * @return
     */
    public static Command stingerToMid()
    {
        return Commands.instant(() -> {
            stinger.extendToLength(Robot.STINGER_MID_LENGTH);
        });
    }
    /**
     * extends the stinger to the high pole
     * @return
     */
    public static Command stingerToHigh()
    {
        return Commands.instant(() -> {
            stinger.extendToLength(Robot.STINGER_HIGH_LENGTH);
        });
    }

    
    public static Command doNothing() 
    {
        return Commands.instant(() -> {});
    }

    //Commands to operate the grabber
    // public static Command openGrabber() {
    //     return Commands.instant(() -> {
    //         Grabber.grab();
    //     });
        
    // }

    // public static Command closeGrabber() {
    //     return Commands.instant(() -> {
    //         Grabber.release(); 
    //     });
    // }
    // //commands to operate the shwooper
    // public static Command extendShwooper() {
    //     return Commands.instant(() -> {
    //         Shwooper.extend();
    //     });
    // }

    // public static Command retractShwooper() {
    //     return Commands.instant(() -> {
    //         Shwooper.retract();
    //     });
    // }
}
