package frc.robot;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.Auto;
import frc.robot.Constants.Drive;
import frc.robot.Constants.Field;
import frc.robot.Constants.Robot;
import frc.robot.structure.SequenceParser;
import frc.robot.structure.Trajectories;
import frc.robot.structure.animation.DefaultAnimation;
import frc.robot.structure.animation.QuickSlowFlash;
import frc.robot.structure.animation.SolidAnimation;
import frc.robot.structure.factories.HashMaps;
import frc.robot.structure.helpers.Logging;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Drivetrain.DriveCommands;

import static frc.robot.ChargedUp.elevator;

import java.util.Arrays;

public class Commands2023 
{   
    // put commands here

    // ARM COMMANDS

    // /*
    //  * takes a length in meters as a double and moves the arm to that length
    //  */
    // public static Command armGoToLength(double length)
    // {
    //     return run(() -> ChargedUp.arm.extendTo(length), ChargedUp.arm)
    //         .until(ChargedUp.arm::isUpDownPIDFree);
    // }
    
    // /** 
    //  * takes an angle as a double and moves the arm to that angle
    //  */
    // public static Command armGoToAngle(double angle)
    // {
    //     return run(() -> ChargedUp.arm.rotateTo(angle), ChargedUp.arm)
    //         .until(ChargedUp.arm::isUpDownPIDFree);
    // }
    // /**
    //  * arm returns to origin position in fully retracted and lowered position
    //  */
    // public static Command returnArm()
    // {
    //     CommandBase x = Commands.parallel 
    //     (
    //         armGoToAngle(Robot.Arm.RETURN_POSITION),
    //         armGoToLength(0)
    //     );

    //     x.addRequirements(ChargedUp.arm);

    //     return x;
    // }
    // /**
    //  * moves the arm to be above the mid peg so cones can drop and score
    //  */
    // public static Command armToMidPeg()
    // {
    //     CommandBase x = parallel 
    //     (
    //         armGoToAngle(Robot.Arm.MID_PEG_ANGLE),
    //         armGoToLength(Robot.Arm.MID_LENGTH)
    //     );

    //     x.addRequirements(ChargedUp.arm);

    //     return x;
    // }

    // /**
    //  * moves the arm so the end of the arm is directly over the high peg and game pieces can be scored
    //  */
    // public static Command armToHighPeg()
    // {
    //     CommandBase x = parallel
    //     (
    //         armGoToAngle(Robot.Arm.HIGH_PEG_ANGLE),
    //         armGoToLength(Robot.Arm.HIGH_LENGTH)
    //     );

    //     x.addRequirements(ChargedUp.arm);

    //     return x;
    // }

    // /**
    //  * moves the arm so the end is over the middle shelf and game pieces can be scored on that level
    //  */
    // public static Command armToMidShelf()
    // {
    //     CommandBase x = parallel
    //     (
    //         armGoToAngle(Robot.Arm.MID_SHELF_ANGLE),
    //         armGoToLength(Robot.Arm.MID_LENGTH)
    //     );

    //     x.addRequirements(ChargedUp.arm);

    //     return x;
    // }

    // /**
    //  * moves the arm so that the end is directly over the high shelf and game pieces can be scored there
    //  * @return
    //  */
    // public static Command armToHighShelf()
    // {
    //     CommandBase x = parallel
    //     (
    //         armGoToAngle(Robot.Arm.HIGH_SHELF_ANGLE),
    //         armGoToLength(Robot.Arm.HIGH_LENGTH)
    //     );

    //     x.addRequirements(ChargedUp.arm);

    //     return x;
    // }

    /**
     * Displays solid purple on the LEDs
     * To stop displaying this, either call activateYellow() or activateAlliance() which will set to alliance color
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activatePurple() {
        return Commands.runOnce(() -> DefaultAnimation.setPurple());
    }

    /**
     * Displays solid yellow on the LEDs
     * To stop displaying this, either call activateYellow() or activateAlliance() which will set to alliance color
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activateYellow() {
        return Commands.runOnce(() -> DefaultAnimation.setYellow());
    }

    /**
     * Displays the alliance color in solid on the LEDs. Will run until another LED is called.
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activateAlliance() {
        return Commands.runOnce(() -> DefaultAnimation.setDefault());
    }

    /**
     * Flashes twice quickly and twice slowly in yellow: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cone into play
     */
    public static Command quickSlowFlashYellow() {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kYellow)));
    }

    /**
     * Flashes twice quickly and twice slowly in purple: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cone into play
     */
    public static Command quickSlowFlashPurple() {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kPurple)));
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

    /**
     * Moves the elevator downwards until it reaches the start position
     * @return the command
     */
    public static Command elevatorInitialize() 
    {
        return Commands.sequence(
            run(elevator::delevate, elevator)
                .until(elevator::isAtStartPosition),

            run(elevator::resetElevatorEncoder, elevator) 
        );
    }

    // ELEVATOR + STINGER COMMANDS 

    /**
     * Moves the Elevator and Stinger to High position to sequence
     * <p>
     * Elevator goes to {@link Robot#HIGH_HEIGHT High Height Constant}
     * <p>
     * Stinger goes to {@link Robot#HIGH_LENGTH_MUL High Length Constant}
     * @return Command
     */
    public static Command elevatorStingerToHigh()
    {
        CommandBase c = sequence(
            run(() -> ChargedUp.stinger.toHigh())
                .until(ChargedUp.stinger::isPIDFree),
            run(() -> ChargedUp.elevator.setHigh())
                .until(ChargedUp.elevator::isPIDFree)
        );
        c.addRequirements(ChargedUp.elevator, ChargedUp.stinger);
        return c;
    }
    /**
     * Moves the Elevator and Stinger to MID position in sequence
     * <p>
     * Elevator goes to {@link Robot#MID_HEIGHT High Height Constant}
     * <p>
     * Stinger goes to {@link Robot#MID_LENGTH_MUL High Length Constant}
     * @return Command
     */
    public static Command elevatorStingerToMid()
    {
        CommandBase c = sequence(
            run(() -> ChargedUp.stinger.toMid())
                .until(ChargedUp.stinger::isPIDFree),
            run(() -> ChargedUp.elevator.setMid())
                .until(ChargedUp.elevator::isPIDFree)
        );

        c.addRequirements(ChargedUp.elevator, ChargedUp.stinger);
        return c;
    }

    /**
     * Moves the Elevator and Stinger to LOW position in sequence
     * <p>
     * Elevator goes to 0
     * <p>
     * Stinger goes to 0
     * @return Command
     */
    public static Command elevatorStingerToLow()
    {
        CommandBase c = sequence(
            run(() -> ChargedUp.stinger.fullyRetract())
                .until(ChargedUp.stinger::isPIDFree),
            run(() -> ChargedUp.elevator.setLow())
                .until(ChargedUp.elevator::isPIDFree)
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
    
    /**
     * Closes the Grabber
     */ 
    public static Command closeGrabber() {
        return Commands.runOnce(() -> {
            ChargedUp.grabber.grab();
        });

     }
     /**
      * Opens the grabber
      */
    public static Command openGrabber() {
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
    // grabs 
    public static Command grabGrabber()
    {
        return Commands.runOnce(ChargedUp.grabber::grab);
    }
    // releases the grabber
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

    // public static Command setLEDColor(Color color) {
    //     return Commands.runOnce(() -> {
    //         ChargedUp.led.setColor(color);
    //     });
    // }
    // public static Command setObjectHolding(GamePiece object) 
    // {
    //     return Commands.runOnce(() -> {
    //         ChargedUp.led.setHolding(object);
    //     });
    // }
    
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
    /**
     * retracts shwooper
     */ 
    public static Command retractSchwooper()
    {
        return Commands.runOnce(ChargedUp.shwooper::retract);
    }
    /** 
     * extends shwooper
     */  
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
    
    // picks up objects and raises the elevator to the middle after picking the object up
    public static Command pickup() 
    {
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
            waitSeconds(Robot.Shwooper.SUCK_TIME_FOR_PICKUP_AUTO),
            stopShwooper(),

            // Grab it
            grabGrabber(),

            // Prepare to leave
            elevatorToMid(),
            retractSchwooper()
        );
    }
    /**
     * Moves stinger to the highest pole and opens the grabber
     */
    public static Command score()
    {
        CommandBase c = Commands.sequence(
            Commands.runOnce(() -> Logging.info("score!!!")),
            //move sideways to the target
            ChargedUp.drivetrain.commands.moveToObjectSideways(),

            //Prepare position
            elevatorStingerToHigh(), 

            //Drop grabber
            openGrabber(), 

            //Return to position 
            Commands.parallel(
                retractStinger(), 
                elevatorToMid()
            )
        );
        //c.addRequirements(ChargedUp.elevator, ChargedUp.stinger, ChargedUp.grabber);
        return c;
    }

    /**
     * Automatically compensates for angle offset caused by oscillatory motion of the 
     * 'Charging Station'.
     * 
     * Uses {@link Drivetrain#getChargeStationAngle()} to get the charge station angle,
     * and inverts the direction of motion (relative to the angle) using 
     * {@link Robot#CHARGER_STATION_INCLINE_INVERT}.
     */
    public static Command balance()
    {
        return Commands.runEnd(() -> 
        {
            ChargedUp.drivetrain.disableFieldRelative();

            double angle = ChargedUp.drivetrain.getChargeStationAngle();
            double speed = Drive.MAX_SPEED_MPS / 14 * angle / Field.CHARGE_ANGLE_RANGE_DEG;

            if (angle <= Field.CHARGE_ANGLE_DEADBAND && angle >= -Field.CHARGE_ANGLE_DEADBAND) 
            {
                speed = 0;
            }
            speed = Robot.CHARGER_STATION_INCLINE_INVERT ? -speed : speed;
            
            Logging.info("angle = " + angle + "; speed = " + speed);

            ChargedUp.drivetrain.set(0, speed, 0);
        }, 
        () -> ChargedUp.drivetrain.enableFieldRelative());
    }

    /**
     * Get the action command which corresponds to the given auto sequence string segment. 
     * This method can either take in transitions or actions.
     * 
     * @return The command, or none() with a log if an error occurs
     */
    public static Command fromStringToCommand(String str)
    {
        // Single actions
        if(str.length() == 1)
        {
            switch(str)
            {
                case "A": 
                case "C": return pickup();

                case "1":
                case "2":
                case "3": return score();

                case "B": return balance();

                default: 
                {
                    Logging.error("Invalid path point! Please be better.");
                    return none();
                }
            }
        }
        // Transition
        else if(str.length() == 2)
        {
            try 
            {
                return Trajectories.auto(
                    str, 
                    Auto.MAX_VEL, 
                    Auto.MAX_ACC, 
                    HashMaps.of(
                        "Elevator Mid",  elevatorToMid(),
                        "Elevator High", elevatorToHigh(),
                        "Extend Intake", extendSchwooper()
                    )
                );
            }
            catch (Exception e)
            {
                Logging.error("Error finding path transition " + str + ": is it valid? Are you dumb? Who knows!\n" + e);
                return none();
            }
        }
        // Error
        else 
        {
            Logging.error("(> 2 || == 0) characters in received string! Please cry about it.");
            return none();
        }
    }

    /**
     * Create the autonomous command from the given parsed autonomous sequence string.
     * 
     * @return The command, with none() inserted into places in the sequence where an error occured.
     */
    public static Command buildAuto(String[] list)
    {
        Command[] commandList = new Command[list.length];

        for (int i = 0; i < list.length; i++)
        {
            commandList[i] = fromStringToCommand(list[i]);
        }
        
        return Commands.sequence(
            elevatorInitialize(), 
            Commands.sequence(commandList)
        );
    }
    
    /**
     * Create the autonomous command from the given parsed autonomous sequence string.
     * 
     * @return The command, with none() inserted into places in the sequence where an error occured,
     * or none() itself if parsing or lexing fails.
     */
    public static Command buildAuto(String p) 
    {
        String[] parts = SequenceParser.parse(p);

        if(parts == null) 
        {
            return none();
        }

        return buildAuto(parts);
    }
}