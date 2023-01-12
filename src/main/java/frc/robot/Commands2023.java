package frc.robot;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Grabber;

public class Commands2023 
{
    // put commands here

    
    public static Command doNothing() 
    {
        return Commands.instant(() -> {});
    }

    //Commands to operate the grabber
    public static Command openGrabber() {
        return Commands.instant(() -> {
            Grabber.grab();
        });
        
    }

    public static Command closeGrabber() {
        return Commands.instant(() -> {
            Grabber.release();
        });
    }
}
