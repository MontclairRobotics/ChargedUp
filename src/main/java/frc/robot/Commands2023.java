package frc.robot;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.subsystems.Grabber;
// import frc.robot.subsystems.Shwooper;

public class Commands2023 {
    // put commands here

    public static Command doNothing() {
        return Commands.instant(() -> {
        });
    }

    // Commands to operate the grabber
    public static Command openGrabber() {
        return Commands.instant(() -> {
            ChargedUp.grabber.grab();
        });

    }

    public static Command closeGrabber() {
        return Commands.instant(() -> {
            ChargedUp.grabber.release();
        });
    }

    //Commands to operate shwooper
    public static Command extendShwooper() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.extend();
        });
    }

    public static Command retractShwooper() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.retract();
        });
    }

    public static Command shwooperSuck() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.suck();
        });
    }

    public static Command shwooperSpit() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.spit();
        });
    }

    public static Command stopShwooper() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.stop();
        });
    }
}
