package frc.robot;

import static org.team555.frc.command.Commands.*;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.structure.GamePiece;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.constants.Constants.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Stinger;

import static frc.robot.ChargedUp.elevator;
import static frc.robot.ChargedUp.stinger;

public class Commands2023 
{   
    // put commands here

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
   
    // STINGER COMMMANDS

    /**
     * fully retracts the stinger 
     * @return Command
     */
    public static Command retractStinger()
    {
        return Commands.runUntil(ChargedUp.stinger::isPIDFree, () -> ChargedUp.stinger.fullyRetract());
    }
    /**
     * extends the stinger to the length of the middle pole
     * @return Command
     */
    public static Command stingerToMid()
    {
        return Commands.runUntil(ChargedUp.stinger::isPIDFree, () -> ChargedUp.stinger.toMid());
    }
    /**
     * extends the stinger to the high pole
     * @return Command
     */
    public static Command stingerToHigh()
    {
        return Commands.runUntil(ChargedUp.stinger::isPIDFree, () -> ChargedUp.stinger.toHigh());
    }

    // ELEVATOR COMMANDS

    /**
     * Sets the elevator its lowest height
     * @return Command
     */
    public static Command elevatorToLow()
    {
        return Commands.runUntil(elevator::isPIDFree, elevator::setLow, elevator);
    }

    /**
     * Sets the elevator to the height of the middle section
     * @return the command
     */
    public static Command elevatorToMid()
    {
        return Commands.runUntil(elevator::isPIDFree, () -> elevator.setMid(), elevator);
    }

    /**
     * Sets the elevator to the height of the high section
     * @return the command
     */
    public static Command elevatorToHigh()
    {
        return Commands.runUntil(elevator::isPIDFree, () -> elevator.setHigh(), elevator);
    }

    // ELEVATOR + STINGER COMMANDS 

    /**
     * Moves the Elevator and Stinger to High position simultaneously
     * <p>
     * Elevator goes to {@link Robot#ELEVATOR_HIGH_HEIGHT High Height Constant}
     * <p>
     * Stinger goes to {@link Robot#STINGER_HIGH_LENGTH High Length Constant}
     * @return Command
     */
    public static Command elevatorStingerToHigh()
    {
        return CommandGroupBase.parallel(
            runUntil(ChargedUp.elevator::isPIDFree, () -> ChargedUp.elevator.setHigh()),
            runUntil(ChargedUp.stinger::isPIDFree, () -> ChargedUp.stinger.toHigh())
        ).deadlineWith(block(ChargedUp.stinger, ChargedUp.elevator));
    }
    /**
     * Moves the Elevator and Stinger to MID position simultaneously
     * <p>
     * Elevator goes to {@link Robot#ELEVATOR_MID_HEIGHT High Height Constant}
     * <p>
     * Stinger goes to {@link Robot#STINGER_MID_LENGTH High Length Constant}
     * @return Command
     */
    public static Command elevatorStingerToMid()
    {
        return CommandGroupBase.parallel(
            runUntil(ChargedUp.elevator::isPIDFree, () -> ChargedUp.elevator.setMid()),
            runUntil(ChargedUp.stinger::isPIDFree, () -> ChargedUp.stinger.toMid())
        ).deadlineWith(block(ChargedUp.stinger, ChargedUp.elevator));
    }
    /**
     * Moves the Elevator and Stinger to LOW position simultaneously
     * <p>
     * Elevator goes to 0
     * <p>
     * Stinger goes to 0
     * @return Command
     */
    public static Command elevatorStingerToLow()
    {
        return CommandGroupBase.parallel(
            runUntil(ChargedUp.elevator::isPIDFree, () -> ChargedUp.elevator.setLow()),
            runUntil(ChargedUp.stinger::isPIDFree, () -> ChargedUp.stinger.fullyRetract())
        ).deadlineWith(block(ChargedUp.stinger, ChargedUp.elevator));
    }

    /**
     * Cancel the PID of the elevator and the stinger, so no longer targeting a value
     */
    public static Command stopPIDing()
    {
        return Commands.instant(()->{
            ChargedUp.stinger.stopPIDing();
            ChargedUp.stinger.stopPIDing();
        },
        ChargedUp.stinger,ChargedUp.elevator);
    }

    

    // GRABBER COMMANDS
    
    //public static Command openGrabber() {
    //     return Commands.instant(() -> {
    //         ChargedUp.grabber.grab();
    //     });

    // }

    // public static Command closeGrabber() {
    //     return Commands.instant(() -> {
    //         ChargedUp.grabber.release();
    //     });
    // }

    /**
     * Toggle the Grabber
     * <p>
     * - if it is grabbed something, then release
     * <p>
     * - if it is released, grab
     * @return Command
     */
    public static Command toggleGrabber(){
        return Commands.instant(() ->{
            ChargedUp.grabber.toggle();
        });
    }

    // SHWOOPER COMMMANDS

    // public static Command extendShwooper() {
    //     return Commands.instant(() -> {
    //         ChargedUp.shwooper.extend();
    //     });
    // }

    // public static Command retractShwooper() {
    //     return Commands.instant(() -> {
    //         ChargedUp.shwooper.retract();
    //     });
    // }

    /*public static Command setLEDColor(Color color) {
        return Commands.instant(() -> {
            ChargedUp.led.setColor(color);
        });
    }
    public static Command setObjectHolding(GamePiece object) 
    {
        return Commands.instant(() -> {
            ChargedUp.led.setHolding(object);
        });
    }*/
    
    /**
     * toggle the Shwooper
     * <p>
     * - if it is extended, <b>retract</b>
     * <p>
     * - if it is retracted, <b>extend</b>
     * @return Command
     */
    public static Command toggleShwooper() {
        return Commands.instant( () -> {
            ChargedUp.shwooper.toggle();
        });
    }

    /**
     * intake suck
     * @return Command
     */
    public static Command shwooperSuck() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.suck();
        });
    }
 
    /**
     * intake spit
     * @return Command
     */
    public static Command shwooperSpit() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.spit();
        });
    }

    /**
     * Stop intake
     * @return Command
     */
    public static Command stopShwooper() {
        return Commands.instant(() -> {
            ChargedUp.shwooper.stop();
        });
    }
}

