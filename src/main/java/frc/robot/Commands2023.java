package frc.robot;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.subsystems.Grabber;
// import frc.robot.subsystems.Shwooper;
import frc.robot.subsystems.GamePiece;

public class Commands2023 
{
    // put commands here

    
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

    public static Command setLEDColor(Color color) {
        return Commands.instant(() -> {
            ChargedUp.led.setColor(color);
        });
    }
    public static Command setObjectHolding(GamePiece object) 
    {
        return Commands.instant(() -> {
            ChargedUp.led.setHolding(object);
        });
    }
}

