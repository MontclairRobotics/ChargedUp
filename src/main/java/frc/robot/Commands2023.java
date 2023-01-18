package frc.robot;

import static org.team555.frc.command.Commands.*;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
// import frc.robot.subsystems.Grabber;
// import frc.robot.subsystems.Shwooper;
import frc.robot.constants.Constants.Robot;

public class Commands2023 
{
    // put commands here

    
    public static Command doNothing() 
    {
        return instant(() -> {});
    }

    /*
     * takes a length in meters as a double and moves the arm to that length
     */
    public static Command armGoToLength(double length)
    {
        final var arm = ChargedUp.arm;
        return runUntil(arm::isInOutPIDFree, () -> arm.extendTo(length));
    }
    
    /** 
     * takes an angle as a double and moves the arm to that angle
     */
    public static Command armGoToAngle(double angle){
        return runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(angle));
    }
    
    /**
     * arm returns to origin position in fully retracted and lowered position
     */
    public static Command returnArm(){
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_RETURN_POSITION)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(0))
        );
    }
    /**
     * moves the arm to be above the mid peg so cones can drop and score
     */
    public static Command armToMidPeg(){
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_MID_PEG_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_MID_LENGTH))
        );
    }

    /**
     * moves the arm so the end of the arm is directly over the high peg and game pieces can be scored
     */
    public static Command armToHighPeg(){
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_HIGH_PEG_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_HIGH_LENGTH))
        );
    }

    /**
     * moves the arm so the end is over the middle shelf and game pieces can be scored on that level
     */
    public static Command armToMidShelf(){
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_HIGH_PEG_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_HIGH_LENGTH))
        );
    }

    /**
     * moves the arm so that the end is directly over the high shelf and game pieces can be scored there
     * @return
     */
    public static Command armToHighShelf(){
        return CommandGroupBase.parallel (
            runUntil(ChargedUp.arm::isUpDownPIDFree, () -> ChargedUp.arm.rotateTo(Robot.ARM_HIGH_SHELF_ANGLE)),
            runUntil(ChargedUp.arm::isInOutPIDFree, () -> ChargedUp.arm.extendTo(Robot.ARM_HIGH_LENGTH))
        );
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
