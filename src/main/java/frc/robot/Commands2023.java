package frc.robot;

import static org.team555.frc.command.Commands.*;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.Constants.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Stinger;

public class Commands2023 
{
    private static Elevator elevator = new Elevator();
    private static Stinger stinger = new Stinger();
    
    // put commands here

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
     * extends the stinger to the length of the middle pole
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

    // ARM COMMANDS

    /*
     * takes a length in meters as a double and moves the arm to that length
     */
    public static Command armGoToLength(double length)
    {
        return runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(length), ChargedUp.arm);
    }
    
    /** 
     * takes an angle as a double and moves the arm to that angle
     */
    public static Command armGoToAngle(double angle)
    {
        return runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(angle), ChargedUp.arm);
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

    /**
     * arm returns to origin position in fully retracted and lowered position
     */
    public static Command returnArm()
    {
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_RETURN_POSITION)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(0))
        ).deadlineWith(block(ChargedUp.arm));
    }
    /**
     * moves the arm to be above the mid peg so cones can drop and score
     */
    public static Command armToMidPeg()
    {
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_MID_PEG_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_MID_LENGTH))
        ).deadlineWith(block(ChargedUp.arm));
    }

    /**
     * moves the arm so the end of the arm is directly over the high peg and game pieces can be scored
     */
    public static Command armToHighPeg()
    {
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_HIGH_PEG_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_HIGH_LENGTH))
        ).deadlineWith(block(ChargedUp.arm));
    }

    /**
     * moves the arm so the end is over the middle shelf and game pieces can be scored on that level
     */
    public static Command armToMidShelf()
    {
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_HIGH_PEG_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_HIGH_LENGTH))
        ).deadlineWith(block(ChargedUp.arm));
    }

    /**
     * moves the arm so that the end is directly over the high shelf and game pieces can be scored there
     * @return
     */
    public static Command armToHighShelf()
    {
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_HIGH_SHELF_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_HIGH_LENGTH))
        ).deadlineWith(block(ChargedUp.arm));
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
