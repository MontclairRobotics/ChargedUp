package frc.robot;

import static org.team555.frc.command.Commands.*;

import org.team555.frc.command.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
// import frc.robot.subsystems.Grabber;
// import frc.robot.subsystems.Shwooper;
import frc.robot.constants.Constants.Robot;
import frc.robot.subsystems.Stinger;

public class Commands2023 {
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
