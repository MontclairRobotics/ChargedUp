package frc.robot;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ProxyCommand;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.animation.DefaultAnimation;
import frc.robot.animation.QuickSlowFlash;
import frc.robot.components.managers.Auto;
import frc.robot.components.subsystems.Drivetrain;
import frc.robot.constants.*;
import frc.robot.structure.DetectionType;
import frc.robot.structure.GamePiece;
import frc.robot.structure.ScoreHeight;
import frc.robot.structure.ScoringType;
import frc.robot.util.HashMaps;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.Trajectories;
import frc.robot.vision.VisionSystem;

import static frc.robot.ChargedUp.*;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.pathplanner.lib.PathPlannerTrajectory;

public class Commands2023 
{   
    public static Command log(String str) 
    {
        return Commands.runOnce(() -> Logging.info(str));
    }
    public static Command log(Supplier<String> str) 
    {
        return Commands.runOnce(() -> Logging.info(str.get()));
    }

    /////////////////////// LED COMMANDS /////////////////////////


    /**
     * Displays solid purple on the LEDs
     * To stop displaying this, either call activateYellow() or activateAlliance() which will set to alliance color
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activatePurple() 
    {
        return Commands.runOnce(() -> DefaultAnimation.setViolet())
            .withName("Activate Purple LED");
    }

    /**
     * Displays solid yellow on the LEDs
     * To stop displaying this, either call activateYellow() or activateAlliance() which will set to alliance color
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activateYellow() 
    {
        return Commands.runOnce(() -> DefaultAnimation.setYellow())
            .withName("Activate Yellow LED");
    }

    /**
     * Displays the alliance color in solid on the LEDs. Will run until another LED is called.
     * Since this changes the default color, if you add a command to the que it will override this
     */
    public static Command activateAlliance() 
    {
        return Commands.runOnce(() -> DefaultAnimation.setDefault())
            .withName("Activate Alliance LED");
    }

    /**
     * Flashes twice quickly and twice slowly in yellow: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cone into play
     */
    public static Command quickSlowFlashYellow() 
    {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kYellow)))
            .withName("Signal Cone");
    }

    /**
     * Flashes twice quickly and twice slowly in purple: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cube into play
     */
    public static Command quickSlowFlashPurple()
    {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kPurple)))
            .withName("Signal Cube");
    }
    
    public static Command toggleGrabberHasCone() 
    {
        return Commands.runOnce(ChargedUp.grabber::toggleHoldingCone)
            .withName("Toggle Grabber has Cone");
    }
    public static Command setGrabberHasCone()
    {
        return Commands.runOnce(() -> grabber.setHoldingCone(true))
            .withName("Set Grabber has Cone");
    }
    public static Command setGrabberHasCube()
    {
        return Commands.runOnce(() -> grabber.setHoldingCone(false))
            .withName("Set Grabber has Cube");
    }
   

    /////////////////// STINGER COMMMANDS //////////////////////

    /**
     * fully retracts the stinger 
     * @return Command
     */
    public static Command retractStinger()
    {
        return Commands.runOnce(stinger::targetIn)
            .andThen(Commands.waitSeconds(StingerConstants.PNEU_TIME))
            .withName("Retract Stinger");
    }
    /**
     * extends the stinger to the length of the middle pole
     * @return Command
     */
    public static Command extendStinger()
    {
        return Commands.runOnce(stinger::targetOut) 
            .andThen(Commands.waitSeconds(StingerConstants.PNEU_TIME))
            .withName("Stinger Out");
    }

    /**
     * toggle the stinger
     * @return Command
     */
    public static Command toggleStinger()
    {
        return Commands.either(
            retractStinger(), 
            extendStinger(), 
            stinger::isOut
        ).withName("Toggle Stinger");
    }

    //////////////////////////////// ELEVATOR COMMANDS ////////////////////////////////

    /**
     * Sets the elevator its lowest height
     * @return Command
     */
    public static Command elevatorToLow()
    {
        return elevator.PID.goToSetpoint(ElevatorConstants.MIN_HEIGHT, elevator);
    }

    /**
     * Sets the elevator to the height of the middle cone peg
     * @return the command
     */
    public static Command elevatorToConeMid()
    {
        return elevator.PID.goToSetpoint(ElevatorConstants.MID_HEIGHT_CONE, elevator);
    }

    /**
     * Sets the elevator to the height of the middle cube shelf
     * @return Command
     */
    public static Command elevatorToCubeMid()
    {
        return elevator.PID.goToSetpoint(ElevatorConstants.MID_HEIGHT_CUBE, elevator);
    }

    /**
     * Sets the elevator to the height of the high section
     * @return the command
     */
    // public static Command elevatorToHigh()
    // {
    //     return elevator.PID.goToSetpoint(ElevatorConstants.HIGH_HEIGHT, elevator);
    // }

    ///////////////// ELEVATOR + STINGER COMMANDS /////////////////////////////
    /**
     * Moves the Elevator and Stinger to MID position in sequence
     * <p>
     * Elevator goes to {@link Robot#MID_HEIGHT_CONE High Height Constant}
     * <p>
     * Stinger goes to {@link Robot#MID_LENGTH_MUL Mid Length Constant}
     * @return Command
     */
    public static Command elevatorStingerToMid()
    {
        CommandBase c = sequence(
            elevatorToConeMid(),
            extendStinger()
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
    public static Command elevatorStingerReturn()
    {
        CommandBase c = sequence(
            retractStinger(),
            elevatorToLow()
        );
        c.addRequirements(elevator, stinger);
        return c;
    }

    public static Command elevatorStingerToLow()
    {
        CommandBase c = sequence(
            elevatorToLow(),
            stinger.outLow()
        );
        c.addRequirements(stinger, elevator);
        return c;
    }
    

    /////////////////////// GRABBER COMMANDS ////////////////////////////////////////////
    
    /**
     * Closes the Grabber
     */ 
    public static Command closeGrabber() 
    {
        return Commands.runOnce(ChargedUp.grabber::grab)
            .withName("Close Grabber");

     }
     /**
      * Opens the grabber
      */
    public static Command openGrabber() 
    {
        return Commands.runOnce(ChargedUp.grabber::release)
            .withName("Open Grabber");
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
        return Commands.runOnce(ChargedUp.grabber::toggle)
            .withName("Toggle Grabber");
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
        return Commands.runOnce(ChargedUp.shwooper::toggleShwooper) 
            .withName("Toggle Schwooper");
    }
    /**
     * retracts shwooper
     */ 
    public static Command retractSchwooper()
    {
        return Commands.runOnce(ChargedUp.shwooper::retractShwooper)
            .withName("Retract Schwooper");
    }
    /** 
     * extends shwooper
     */  
    public static Command extendSchwooper()
    {
        return Commands.runOnce(ChargedUp.shwooper::extendShwooper)
            .withName("Extend Schwooper");
    }

    /**
     * intake suck
     * @return Command
     */
    public static Command shwooperSuck() 
    {
        return Commands.runOnce(ChargedUp.shwooper::suck)
            .withName("Intake Suck");
    }
 
    /**
     * intake spit
     * @return Command
     */
    public static Command shwooperSpit() 
    {
        return Commands.runOnce(ChargedUp.shwooper::spit)
            .withName("Intake Spit");
    }

    /**
     * Stop intake
     * @return Command
     */
    public static Command stopShwooper() 
    {
        return Commands.runOnce(ChargedUp.shwooper::stop)
            .withName("Intake Stop");
    }

    ////////////////////// AUTO COMMANDS //////////////////////////
    
    // picks up objects and raises the elevator to the middle after picking the object up
    public static Command pickup() 
    {
        return Commands.sequence(
            // Ensure grabber released
            openGrabber(),

            // Lower grabber in place
            elevatorStingerReturn(),

            // Suck it
            shwooperSuck(),
            waitSeconds(ShwooperConstants.SUCK_TIME_FOR_PICKUP_AUTO),
            stopShwooper(),

            // Grab it
            closeGrabber(),

            // Prepare to leave
            // elevatorToMid(),
            retractSchwooper()
        ).withName("Pickup");
    }

    /**
     * Moves stinger/elevator to the specfied score place and opens the grabber
     * 
     * retracts the stinger and bring the elevator to mid
     */
    public static Command scoreFromHeightAndType(ScoreHeight height, ScoringType type)
    {
        return Commands.sequence(
            log("[SCORE] Beginning score sequence . . ."),
            closeGrabber(),

            //move sideways to the target
            moveToObjectSideways(type::getDetectionType),

            //Prepare position
            log("[SCORE] Positioning elevator and stinger . . ."),  
            height.getPositioner(), 

            //Drop grabber
            log("[SCORE] Dropping . . ."),
            waitSeconds(0.9),
            openGrabber(), 
            waitSeconds(0.7),

            //Return to position 
            log("[SCORE] Returning elevator and stinger to internal state . . ."),
            elevatorStingerReturn(),
            
            log("[SCORE] Done!")
        ).withName("Score " + height.toString().toLowerCase() + " " + type.toString().toLowerCase());
    }

    public static Command scoreLowPeg()
    {return scoreFromHeightAndType(ScoreHeight.LOW, ScoringType.PEG);}
    public static Command scoreLowShelf()
    {return scoreFromHeightAndType(ScoreHeight.LOW, ScoringType.SHELF);}

    public static Command scoreMidPeg()
    {return scoreFromHeightAndType(ScoreHeight.MID_CONE, ScoringType.PEG);}
    public static Command scoreMidShelf()
    {return scoreFromHeightAndType(ScoreHeight.MID_CUBE, ScoringType.SHELF);}
    
    // public static Command scoreHighPeg()g
    // {return scoreFromHeightAndType(ScoreHeight.HIGH_CONE, ScoringType.PEG);}
    // public static Command scoreHighShelf()
    // {return scoreFromHeightAndType(ScoreHeight.HIGH_CUBE, ScoringType.SHELF);}
    
    public static Command scoreMid()
    {return new ProxyCommand(() -> scoreFromHeightAndType(ChargedUp.grabber.getHeldObject() == GamePiece.CUBE? ScoreHeight.MID_CUBE : ScoreHeight.MID_CONE,  ScoringType.from(ChargedUp.grabber.getHeldObject())));}
    // public static Command scoreHigh()
    // {return new ProxyCommand(() -> scoreFromHeightAndType(ScoreHeight.HIGH, ScoringType.from(ChargedUp.grabber.getHeldObject())));}
    public static Command scoreLow()
    {return new ProxyCommand(() -> scoreFromHeightAndType(ScoreHeight.LOW, ScoringType.from(ChargedUp.grabber.getHeldObject())));}

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
        // TODO: redo this to stop when tipping

        return Commands.runOnce(drivetrain::disableFieldRelative)
            .andThen(Commands.run(() -> 
            {
                double angle = drivetrain.getChargeStationAngle();
                double speed = DriveConstants.MAX_SPEED_MPS * DriveConstants.CHARGER_STATION_MUL.get() * angle / Constants.Field.CHARGE_ANGLE_RANGE_DEG;

                if (angle <= Constants.Field.CHARGE_ANGLE_DEADBAND && angle >= -Constants.Field.CHARGE_ANGLE_DEADBAND) 
                {
                    speed = 0;
                }
                speed = DriveConstants.CHARGER_STATION_INCLINE_INVERT ? -speed : speed;

                drivetrain.set(0, speed, 0);
            }))
            .finallyDo(interupted -> drivetrain.enableFieldRelative())
            .withName("Balance");
    }

    /**
     * Decorate a command so that it fails immediately if 
     */
    public static Command ifHasTarget(Command cmd)
    {
        return cmd.until(() -> !vision.hasObject()).unless(() -> !vision.hasObject());
    }

    /**
     * Turns to the object.
     * @return
     */
    public static Command turnToCurrentObject() 
    {
        return ifHasTarget(
            drivetrain.thetaPID.goToSetpoint(drivetrain::getObjectAngle, drivetrain)
        ).withName("Turn to Current Object");
    }

    /**
     * Goes towards the object on the x-axis.
     * @return
     */
    public static Command moveToCurrentObjectSideways()
    {
        return ifHasTarget(
            drivetrain.yPID.goToSetpoint(drivetrain::getObjectHorizontalPosition, drivetrain)
        ).withName("Move to Current Object Sideways");
    }

    /**
     * Turns to the object.
     * @return
     */
    public static Command turnToObject(Supplier<DetectionType> type)
    {
        return Commands.sequence(
            Commands.runOnce(() -> vision.setTargetType(type.get())),
            turnToCurrentObject(),
            Commands.runOnce(() -> vision.setTargetType(VisionSystem.DEFAULT_DETECTION))
        );
    }

    /**
     * Goes towards the object on the x-axis.
     * @return
     */
    public static Command moveToObjectSideways(Supplier<DetectionType> type)
    {
        return Commands.sequence(
            Commands.runOnce(() -> ChargedUp.vision.setTargetType(type.get())),
            moveToCurrentObjectSideways(),
            Commands.runOnce(() -> ChargedUp.vision.setTargetType(VisionSystem.DEFAULT_DETECTION))
        );
    }

    
    /**
     * Get the action command which corresponds to the given auto sequence string segment. 
     * This method can either take in transitions or actions.
     * 
     * @return The command, or none() with a log if an error occurs
     */
    public static Command fromStringToCommand(String str, ArrayList<Trajectory> trajectories)
    {
        // Single actions
        if(str.length() == 1)
        {
            switch(str)
            {
                case "A": 
                case "C": return pickup();

                case "1": return scoreMidPeg();
                case "3": return scoreMidPeg();
                case "2": return scoreMidPeg();
                case "4": return scoreMidShelf();
                case "5": return scoreMidShelf();

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
                PathPlannerTrajectory nextTrajectory = Trajectories.get(str, Constants.Auto.MAX_VEL, Constants.Auto.MAX_ACC);

                trajectories.add(nextTrajectory);

                return ChargedUp.drivetrain.commands.auto(nextTrajectory, HashMaps.of(
                    "Elevator Mid Peg", elevatorToConeMid(),
                    "Elevator Mid Shelf", elevatorToCubeMid(),
                    "Intake On", shwooperSuck(),
                    "Retract", elevatorStingerReturn(),
                    "Intake Off", Commands.sequence(stopShwooper(), closeGrabber())
                ));
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
        ArrayList<Trajectory> allTrajectories = new ArrayList<>();
        
        FieldObject2d trajectoryObject = ChargedUp.field.getObject("Trajectories");

        for (int i = 0; i < list.length; i++)
        {
            commandList[i] = fromStringToCommand(list[i], allTrajectories);

            if(commandList[i] == null)
            {
                trajectoryObject.setPose(-10, -10, Rotation2d.fromDegrees(0));
                return null;
            }
        }

        // Calculate the sum trajectory
        Trajectory sumTrajectory = null;
        if(allTrajectories.size() > 0)
        {
            sumTrajectory = allTrajectories.get(0);

            for(int i = 1; i < allTrajectories.size(); i++)
            {
                sumTrajectory = sumTrajectory.concatenate(allTrajectories.get(i));
            }

            if (DriverStation.getAlliance() == Alliance.Red)
            {
                sumTrajectory = sumTrajectory.relativeTo(new Pose2d(16.5, 8, Rotation2d.fromDegrees(180)));
            }
        }
        

        // Display the sum trajectory
        if(sumTrajectory == null)
        {
            trajectoryObject.setPose(-10, -10, Rotation2d.fromDegrees(0));
        } 
        else
        {
            trajectoryObject.setTrajectory(sumTrajectory);
        }
        
        // Return the sum command
        return Commands.sequence(
            // elevatorInitialize(), 
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

    public static Command backupAuto()
    {
        return Commands.sequence
        (
            Commands.runOnce(() -> 
            {
                ChargedUp.gyroscope.setNorth(); 
                ChargedUp.gyroscope.setAddition(Rotation2d.fromDegrees(180));
            }),

            log("STARTING THE AUTO!!"),
            scoreMid(),
            log("SCORED!!!!!"),
            
            Commands.sequence(
                log("Starting auton drive . . ."),
                drivetrain.commands.driveForTime(2.5, 0, -1.25, 0),
                log("DROVE IT"),
                balance(),
                log("balance!!")
            )
            .unless(ChargedUp::skipDriveAuto)
        );
    }
}