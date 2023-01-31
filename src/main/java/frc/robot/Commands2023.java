package frc.robot;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.wpilibj2.command.Commands;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.structure.GamePiece;
import frc.robot.structure.Unimplemented;
import frc.robot.constants.Constants.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Grabber;
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
        return run(() -> ChargedUp.arm.extendTo(length), ChargedUp.arm)
            .until(ChargedUp.arm::isUpDownPIDFree);
    }
    
    /** 
     * takes an angle as a double and moves the arm to that angle
     */
    public static Command armGoToAngle(double angle)
    {
        return run(() -> ChargedUp.arm.rotateTo(angle), ChargedUp.arm)
            .until(ChargedUp.arm::isUpDownPIDFree);
    }
    /**
     * arm returns to origin position in fully retracted and lowered position
     */
    public static Command returnArm()
    {
        CommandBase x = Commands.parallel 
        (
            armGoToAngle(Robot.ARM_RETURN_POSITION),
            armGoToLength(0)
        );

        x.addRequirements(ChargedUp.arm);

        return x;
    }
    /**
     * moves the arm to be above the mid peg so cones can drop and score
     */
    public static Command armToMidPeg()
    {
        CommandBase x = parallel 
        (
            armGoToAngle(Robot.ARM_MID_PEG_ANGLE),
            armGoToLength(Robot.ARM_MID_LENGTH)
        );

        x.addRequirements(ChargedUp.arm);

        return x;
    }

    /**
     * moves the arm so the end of the arm is directly over the high peg and game pieces can be scored
     */
    public static Command armToHighPeg()
    {
        CommandBase x = parallel
        (
            armGoToAngle(Robot.ARM_HIGH_PEG_ANGLE),
            armGoToLength(Robot.ARM_HIGH_LENGTH)
        );

        x.addRequirements(ChargedUp.arm);

        return x;
    }

    /**
     * moves the arm so the end is over the middle shelf and game pieces can be scored on that level
     */
    public static Command armToMidShelf()
    {
        CommandBase x = parallel
        (
            armGoToAngle(Robot.ARM_MID_SHELF_ANGLE),
            armGoToLength(Robot.ARM_MID_LENGTH)
        );

        x.addRequirements(ChargedUp.arm);

        return x;
    }

    /**
     * moves the arm so that the end is directly over the high shelf and game pieces can be scored there
     * @return
     */
    public static Command armToHighShelf()
    {
        CommandBase x = parallel
        (
            armGoToAngle(Robot.ARM_HIGH_SHELF_ANGLE),
            armGoToLength(Robot.ARM_HIGH_LENGTH)
        );

        x.addRequirements(ChargedUp.arm);

        return x;
    }
   
    // STINGER COMMMANDS

    /**
     * fully retracts the stinger 
     * @return Command
     */
    public static Command retractStinger()
    {
        return run(() -> ChargedUp.stinger.fullyRetract(), ChargedUp.stinger)
            .until(ChargedUp.stinger::isPIDFree);
    }
    /**
     * extends the stinger to the length of the middle pole
     * @return Command
     */
    public static Command stingerToMid()
    {
        return run(() -> ChargedUp.stinger.toMid(), ChargedUp.stinger)
            .until(ChargedUp.stinger::isPIDFree);
    }
    /**
     * extends the stinger to the high pole
     * @return Command
     */
    public static Command stingerToHigh()
    {
        return run(() -> ChargedUp.stinger.toHigh(), ChargedUp.stinger)
            .until(ChargedUp.stinger::isPIDFree);
    }

    // ELEVATOR COMMANDS

    /**
     * Sets the elevator its lowest height
     * @return Command
     */
    public static Command elevatorToLow()
    {
        return run(elevator::setLow, elevator)
            .until(elevator::isPIDFree);
    }

    /**
     * Sets the elevator to the height of the middle section
     * @return the command
     */
    public static Command elevatorToMid()
    {
        return run(elevator::setMid, elevator)
            .until(elevator::isPIDFree);
    }

    /**
     * Sets the elevator to the height of the high section
     * @return the command
     */
    public static Command elevatorToHigh()
    {
        return run(elevator::setHigh, elevator)
            .until(elevator::isPIDFree);
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
        CommandBase c = parallel(
            run(() -> ChargedUp.elevator.setHigh())
                .until(ChargedUp.elevator::isPIDFree),
            run(() -> ChargedUp.stinger.toHigh())
                .until(ChargedUp.stinger::isPIDFree)
        );
        c.addRequirements(ChargedUp.elevator, ChargedUp.stinger);
        return c;
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
        CommandBase c = parallel(
            run(() -> ChargedUp.elevator.setMid())
                .until(ChargedUp.elevator::isPIDFree),
            run(() -> ChargedUp.stinger.toMid())
                .until(ChargedUp.stinger::isPIDFree)
        );

        c.addRequirements(ChargedUp.elevator, ChargedUp.stinger);
        return c;
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
        CommandBase c = parallel(
            run(() -> ChargedUp.elevator.setLow())
                .until(ChargedUp.elevator::isPIDFree),
            run(() -> ChargedUp.stinger.fullyRetract())
                .until(ChargedUp.stinger::isPIDFree)
        );
        c.addRequirements(ChargedUp.elevator, ChargedUp.stinger);
        return c;
    }

    /**
     * Cancel the PID of the elevator and the stinger, so no longer targeting a value
     */
    public static Command stopPIDing()
    {
        return Commands.runOnce(() ->
            {
                ChargedUp.stinger.stopPIDing();
                ChargedUp.elevator.stopPIDing();
            },
            ChargedUp.stinger,
            ChargedUp.elevator
        );
    }

    

    // GRABBER COMMANDS
    
    public static Command openGrabber() {
        return Commands.runOnce(() -> {
            ChargedUp.grabber.grab();
        });

     }

    public static Command closeGrabber() {
        return Commands.runOnce(() -> {
            ChargedUp.grabber.release();
        });
    }

    /**
     * Toggle the Grabber
     * <p>
     * - if it is grabbed something, then release
     * <p>
     * - if it is released, grab
     * @return Command
     */
    public static Command toggleGrabber()
    {
        return Commands.runOnce(ChargedUp.grabber::toggle);
    }
    
    public static Command grabGrabber()
    {
        return Commands.runOnce(ChargedUp.grabber::grab);
    }
    
    public static Command releaseGrabber()
    {
        return Commands.runOnce(ChargedUp.grabber::release);
    }

    // SHWOOPER COMMMANDS

    // public static Command extendShwooper() {
    //     return Commands.runOnce(() -> {
    //         ChargedUp.shwooper.extend();
    //     });
    // }

    // public static Command retractShwooper() {
    //     return Commands.runOnce(() -> {
    //         ChargedUp.shwooper.retract();
    //     });
    // }

    /*public static Command setLEDColor(Color color) {
        return Commands.runOnce(() -> {
            ChargedUp.led.setColor(color);
        });
    }
    public static Command setObjectHolding(GamePiece object) 
    {
        return Commands.runOnce(() -> {
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
    public static Command toggleShwooper() 
    {
        return Commands.runOnce(ChargedUp.shwooper::toggle);
    }

    public static Command retractSchwooper()
    {
        return Commands.runOnce(ChargedUp.shwooper::retract);
    }
    
    public static Command extendSchwooper()
    {
        return Commands.runOnce(ChargedUp.shwooper::extend);
    }

    /**
     * intake suck
     * @return Command
     */
    public static Command shwooperSuck() 
    {
        return Commands.runOnce(ChargedUp.shwooper::suck);
    }
 
    /**
     * intake spit
     * @return Command
     */
    public static Command shwooperSpit() 
    {
        return Commands.runOnce(ChargedUp.shwooper::spit);
    }

    /**
     * Stop intake
     * @return Command
     */
    public static Command stopShwooper() 
    {
        return Commands.runOnce(ChargedUp.shwooper::stop);
    }
    

    public static Command pickup() 
    {
<<<<<<< HEAD
        return Commands.runOnce(() -> {
            ChargedUp.Stinger.fullyRetract();
            ChargedUp.Elevator.setLow();
            ChargedUp.Grabber.grab();
        });
=======
        // SEQUENCE //
            // PARALLEL //
                //grabber toggle
                //retract stinger
            // END PARALLEL //
            //elevator low
            // FOR (Robot.SUCK_TIME) SECONDS //
                //suck
            // END FOR SECONDS //
            //stop sucking
            //elevator mid 
        // END SEQUENCE 

        return Commands.sequence(
            // Ensure grabber released
            releaseGrabber(),

            // Lower grabber in place
            Commands.parallel(
                retractStinger(),
                elevatorToLow()
            ),

            // Suck it
            shwooperSuck(),
            waitSeconds(Robot.INTAKE_SUCK_TIME),
            stopShwooper(),

            // Grab it
            grabGrabber(),

            // Prepare to leave
            elevatorToMid(),
            retractSchwooper()
        );
    }

    public static Command score()
    {
        CommandBase c = Commands.sequence(
            // Prepare position
            elevatorStingerToHigh(), 

            // Drop grabber
            openGrabber(), 

            // Return to position
            Commands.parallel(
                retractStinger(), 
                elevatorToMid()
            )
        );
        //c.addRequirements(ChargedUp.elevator, ChargedUp.stinger, ChargedUp.grabber);
        return c;
    }

    public static Command fromStringToCommand(String str)
    {
        return Commands.none();
    }
}