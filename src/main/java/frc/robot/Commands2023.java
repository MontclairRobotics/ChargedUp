package frc.robot;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.Constants.Robot;
// import frc.robot.subsystems.Grabber;
// import frc.robot.subsystems.Shwooper;
import frc.robot.subsystems.Elevator;

public class Commands2023 
{
    private static Elevator elevator = new Elevator();

    // put commands here

    
    public static Command doNothing() 
    {
        return Commands.instant(() -> {});
    }

    /**
     * Sets the elevator its lowest height
     * @return the command
     */
    public static Command elevatorToLow()
    {
        return Commands.runUntil(elevator::isPIDFree, elevator::setLow, elevator);
    }
    //

    /**
     * Sets the elevator to the height of the middle section
     * @return the command
     */
    public static Command elevatorToMid()
    {
        return Commands.runUntil(elevator::isPIDFree, () -> elevator.setHeight(Robot.ELEVATOR_MID_HEIGHT), elevator);
    }

    /**
     * Sets the elevator to the height of the high section
     * @return the command
     */
    public static Command elevatorToHigh()
    {
        return Commands.runUntil(elevator::isPIDFree, () -> elevator.setHeight(Robot.ELEVATOR_MAX_HEIGHT), elevator);
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
