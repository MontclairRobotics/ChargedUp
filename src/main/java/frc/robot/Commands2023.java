package frc.robot;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ProxyCommand;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.Drive;
import frc.robot.Constants.Field;
import frc.robot.Constants.Robot;
import frc.robot.animation.DefaultAnimation;
import frc.robot.animation.QuickSlowFlash;
import frc.robot.components.managers.Auto;
import frc.robot.components.subsystems.Drivetrain;
import frc.robot.components.subsystems.Drivetrain.DriveCommands;
import frc.robot.structure.DetectionType;
import frc.robot.structure.GamePiece;
import frc.robot.structure.ScoreHeight;
import frc.robot.structure.ScoringType;
import frc.robot.util.HashMaps;
import frc.robot.util.Unimplemented;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.Trajectories;

import static frc.robot.ChargedUp.elevator;

import java.util.Arrays;

public class Commands2023 
{   

    public static Command log(String str) 
    {
        return Commands.runOnce(() -> Logging.info(str));
    }

    /////////////////////// LED COMMANDS /////////////////////////


    /**
     * Displays solid purple on the LEDs
     * To stop displaying this, either call activateYellow() or activateAlliance() which will set to alliance color
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activatePurple() 
    {
        return Commands.runOnce(() -> DefaultAnimation.setViolet());
    }

    /**
     * Displays solid yellow on the LEDs
     * To stop displaying this, either call activateYellow() or activateAlliance() which will set to alliance color
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activateYellow() 
    {
        return Commands.runOnce(() -> DefaultAnimation.setYellow());
    }

    /**
     * Displays the alliance color in solid on the LEDs. Will run until another LED is called.
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activateAlliance() 
    {
        return Commands.runOnce(() -> DefaultAnimation.setDefault());
    }

    /**
     * Flashes twice quickly and twice slowly in yellow: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cone into play
     */
    public static Command quickSlowFlashYellow() 
    {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kYellow)));
    }

    /**
     * Flashes twice quickly and twice slowly in purple: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cube into play
     */
    public static Command quickSlowFlashPurple()
    {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kPurple)));
    }
   

    /////////////////// STINGER COMMMANDS //////////////////////

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

    //////////////////////////////// ELEVATOR COMMANDS ////////////////////////////////

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

    ///////////////// ELEVATOR + STINGER COMMANDS /////////////////////////////

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
            run(() -> ChargedUp.elevator.setHigh())
                .until(ChargedUp.elevator.PID::free),
            run(() -> ChargedUp.stinger.toHigh())
                .until(ChargedUp.stinger.PID::free)
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
                .until(ChargedUp.stinger.PID::free),
            run(() -> ChargedUp.elevator.setMid())
                .until(ChargedUp.elevator.PID::free)
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
                .until(ChargedUp.stinger.PID::free),
            run(() -> ChargedUp.elevator.setLow())
                .until(ChargedUp.elevator.PID::free)
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

    

    /////////////////////// GRABBER COMMANDS ////////////////////////////////////////////
    
    /**
     * Closes the Grabber
     */ 
    public static Command closeGrabber() 
    {
        return Commands.runOnce(ChargedUp.grabber::grab);

     }
     /**
      * Opens the grabber
      */
    public static Command openGrabber() 
    {
        return Commands.runOnce(ChargedUp.grabber::release);
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

    /////////////////////////////// SHWOOPER COMMMANDS ///////////////////////////
    
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
        return Commands.runOnce(ChargedUp.shwooper::toggleShwooper);
    }
    /**
     * retracts shwooper
     */ 
    public static Command retractSchwooper()
    {
        return Commands.runOnce(ChargedUp.shwooper::retractShwooper);
    }
    /** 
     * extends shwooper
     */  
    public static Command extendSchwooper()
    {
        return Commands.runOnce(ChargedUp.shwooper::extendShwooper);
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

    ////////////////////// AUTO COMMANDS //////////////////////////
    
    // picks up objects and raises the elevator to the middle after picking the object up
    public static Command pickup() 
    {
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
     * Moves stinger/elevator to the specfied score place and opens the grabber
     * 
     * retracts the stinger and bring the elevator to mid
     */
    public static Command scoreFromHeightAndType(ScoreHeight height, ScoringType type)
    {
        CommandBase c = Commands.sequence(
            log("[SCORE] Beginning score sequence . . ."),

            //move sideways to the target
            Commands2023.moveToObjectSideways(type.getType()),

            //Prepare position
            log("[SCORE] Positioning elevator and stinger . . ."),
            height.getPositioner(), 

            //Drop grabber
            log("[SCORE] Dropping . . ."),
            openGrabber(), 

            //Return to position 
            log("[SCORE] Returning elevator and stinger to internal state . . ."),
            Commands.sequence(
                retractStinger(), 
                elevatorToMid()
            ),
            
            log("[SCORE] Done!")
        );
        
        return c;
    }

    public static Command scoreMidPeg()
    {return scoreFromHeightAndType(ScoreHeight.MID, ScoringType.PEG);}
    public static Command scoreMidShelf()
    {return scoreFromHeightAndType(ScoreHeight.MID, ScoringType.SHELF);}
    
    public static Command scoreHighPeg()
    {return scoreFromHeightAndType(ScoreHeight.HIGH, ScoringType.PEG);}
    public static Command scoreHighShelf()
    {return scoreFromHeightAndType(ScoreHeight.HIGH, ScoringType.SHELF);}
    
    public static Command scoreMid()
    {return new ProxyCommand(() -> scoreFromHeightAndType(ScoreHeight.MID,  ScoringType.from(ChargedUp.grabber.getHeldObject())));}
    public static Command scoreHigh()
    {return new ProxyCommand(() -> scoreFromHeightAndType(ScoreHeight.HIGH, ScoringType.from(ChargedUp.grabber.getHeldObject())));}

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
        return Commands.sequence(
            Commands.runOnce(ChargedUp.drivetrain::disableFieldRelative),
            Commands.run(() -> 
            {
                ChargedUp.drivetrain.disableFieldRelative();

                double angle = ChargedUp.drivetrain.getChargeStationAngle();
                double speed = Drive.MAX_SPEED_MPS / 14 * angle / Field.CHARGE_ANGLE_RANGE_DEG;

                if (angle <= Field.CHARGE_ANGLE_DEADBAND && angle >= -Field.CHARGE_ANGLE_DEADBAND) 
                {
                    speed = 0;
                }
                speed = Robot.CHARGER_STATION_INCLINE_INVERT ? -speed : speed;

                ChargedUp.drivetrain.set(0, speed, 0);
            })
        ).handleInterrupt(ChargedUp.drivetrain::enableFieldRelative);
    }

    public static Command ifHasTarget(Command cmd)
    {
        return cmd.until(() -> !ChargedUp.vision.hasObject()).unless(() -> !ChargedUp.vision.hasObject());
    }

    /**
     * Turns to the object.
     * @return
     */
    public static Command turnToObject() 
    {
        return ifHasTarget(
            Commands.run(() -> ChargedUp.drivetrain.setTargetAngle(ChargedUp.drivetrain.getObjectAngle()), ChargedUp.drivetrain)
                .until(() -> ChargedUp.drivetrain.isThetaPIDFree())
        );
    }

    /**
     * Goes towards the object on the x-axis.
     * @return
     */
    public static Command moveToObjectSideways()
    {
        return ifHasTarget(
            Commands.run(() -> ChargedUp.drivetrain.xPID.setTarget(ChargedUp.drivetrain.getRobotPose().getX() + ChargedUp.drivetrain.getObjectAngle()), ChargedUp.drivetrain)
            .until(() -> !ChargedUp.drivetrain.xPID.active())
        );
    }

    /**
     * Turns to the object.
     * @return
     */
    public static Command turnToObject(DetectionType type)
    {
        return Commands.sequence(
            Commands.runOnce(() -> ChargedUp.vision.setTargetType(type)),
            turnToObject(),
            Commands.runOnce(() -> ChargedUp.vision.setTargetType(DetectionType.APRIL_TAG))
        );
    }

    /**
     * Goes towards the object on the x-axis.
     * @return
     */
    public static Command moveToObjectSideways(DetectionType type)
    {
        return Commands.sequence(
            Commands.runOnce(() -> ChargedUp.vision.setTargetType(type)),
            moveToObjectSideways(),
            Commands.runOnce(() -> ChargedUp.vision.setTargetType(DetectionType.APRIL_TAG))
        );
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
                case "3": return scoreHigh();

                case "B": return balance();

                default: 
                {
                    Logging.error("Invalid path point! Please be better.");
                    return null;
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
                    Constants.Auto.MAX_VEL, 
                    Constants.Auto.MAX_ACC, 
                    HashMaps.of(
                        "Elevator Mid",  elevatorToMid(),
                        "Elevator High", elevatorToHigh(),
                        "Extend Intake", extendSchwooper()
                    )
                );
            }
            catch (Exception e)
            {
                Logging.errorNoTrace("Error finding path transition '" + str + "'");
                return null;
            }
        }
        // Error
        else 
        {
            Logging.errorNoTrace("More than two or zero characters in received string!");
            return null;
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

            if(commandList[i] == null)
            {
                return null;
            }
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
        String[] parts = Auto.parse(p);

        if(parts == null) 
        {
            return null;
        }

        return buildAuto(parts);
    }
}