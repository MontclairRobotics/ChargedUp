package org.team555;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ProxyCommand;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

import org.team555.animation2.QuickSlowFlash;
import org.team555.components.managers.Auto;
import org.team555.components.subsystems.Drivetrain;
import org.team555.constants.*;
import org.team555.math.Math555;
import org.team555.structure.DetectionType;
import org.team555.structure.GamePiece;
import org.team555.structure.ScoreHeight;
import org.team555.structure.ScoringType;
import org.team555.util.HashMaps;
import org.team555.util.frc.EdgeDetectFilter;
import org.team555.util.frc.Logging;
import org.team555.util.frc.Trajectories;
import org.team555.util.frc.EdgeDetectFilter.EdgeType;
import org.team555.vision.VisionSystem;

import static org.team555.ChargedUp.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import com.pathplanner.lib.PathPlannerTrajectory;

public class Commands555 
{   
    public static CommandBase log(String str) 
    {
        return Commands.runOnce(() -> Logging.info(str));
    }
    public static CommandBase log(Supplier<String> str) 
    {
        return Commands.runOnce(() -> Logging.info(str.get()));
    }

    /////////////////////// LED COMMANDS /////////////////////////
    public static CommandBase celebrate() 
    {
        return Commands.runOnce(led::celebrate)
            .withName("Celebrate");
    }

    /**
     * Flashes twice quickly and twice slowly in yellow: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cone into play
     */
    public static CommandBase signalCone() 
    {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kYellow)))
            .withName("Signal Cone");
    }

    /**
     * Flashes twice quickly and twice slowly in purple: * * -- --
     * Signals to the HUMAN PLAYER that they need to enter a cube into play
     */
    public static CommandBase signalCube()
    {
        return Commands.runOnce(() -> ChargedUp.led.add(new QuickSlowFlash(Color.kPurple)))
            .withName("Signal Cube");
    }
    
    public static CommandBase toggleGrabberHasCone() 
    {
        return Commands.runOnce(ChargedUp.grabber::toggleHoldingCone)
            .withName("Toggle Grabber has Cone");
    }
    public static CommandBase setGrabberHasCone()
    {
        return Commands.runOnce(() -> grabber.setHoldingCone(true))
            .withName("Set Grabber has Cone");
    }
    public static CommandBase setGrabberHasCube()
    {
        return Commands.runOnce(() -> grabber.setHoldingCone(false))
            .withName("Set Grabber has Cube");
    }
   

    /////////////////// STINGER COMMMANDS //////////////////////

    /**
     * fully retracts the stinger 
     * @return Command
     */
    public static CommandBase retractStinger()
    {
        return Commands.runOnce(stinger::targetIn)
            .andThen(Commands.waitSeconds(StingerConstants.PNEU_TIME))
            .withName("Retract Stinger");
    }
    /**
     * extends the stinger to the length of the middle pole
     * @return Command
     */
    public static CommandBase extendStinger()
    {
        return Commands.runOnce(stinger::targetOut) 
            .andThen(Commands.waitSeconds(StingerConstants.PNEU_TIME))
            .withName("Stinger Out");
    }

    /**
     * toggle the stinger
     * @return Command
     */
    public static CommandBase toggleStinger()
    {
        return Commands.either(
            retractStinger(), 
            extendStinger(), 
            stinger::isOut
        ).withName("Toggle Stinger");
    }

    //////////////////////////////// ELEVATOR COMMANDS ////////////////////////////////

    public static CommandBase elevatorTo(double height)
    {
        if(height <= ElevatorConstants.MIN_HEIGHT) 
            return Commands.run(() -> elevator.PID.setSpeed(-1))
                .until(elevator::isAtBottom)
                .andThen(() -> elevator.PID.setSpeed(0))
                .withName("Elevator to Bottom");
            
        if(height >= ElevatorConstants.MAX_HEIGHT) 
            return Commands.run(() -> elevator.PID.setSpeed(1))
                .until(elevator::isAtTop)
                .andThen(() -> elevator.PID.setSpeed(0))
                .withName("Elevator to Top");

        return elevator.PID.goToSetpoint(height, elevator) 
            .withName("Elevator to " + height + "m");
    }

    /**
     * Sets the elevator its lowest height
     * @return Command
     */
    public static CommandBase elevatorToLow()
    {
        return elevatorTo(ElevatorConstants.MIN_HEIGHT);
    }

    /**
     * Sets the elevator to the height of the middle cone peg
     * @return the command
     */
    public static CommandBase elevatorToConeMid()
    {
        return elevatorTo(ElevatorConstants.MID_HEIGHT_CONE);
    }

    /**
     * Sets the elevator to the height of the middle cube shelf
     * @return Command
     */
    public static CommandBase elevatorToCubeMid()
    {
        return elevatorTo(ElevatorConstants.MID_HEIGHT_CUBE);
    }

    /**
     * Sets the elevator high enough so that the human player can feed it a cone. Currently 5/6 max height.
     * @return Command
     */
    public static CommandBase elevatorHumanPlayerLevel()
    {
        return elevatorTo(ElevatorConstants.MAX_HEIGHT * 5 / 6);
    }

    /**
     * Sets the elevator to the height of the high section
     * @return the command
     */
    // public static Command elevatorToHigh()
    // {
    //     return elevator.PID.goToSetpoint(ElevatorConstants.HIGH_HEIGHT, elevator);
    // }

    ///////////////// COMBINATION COMMANDS /////////////////////////////

    /**
     * Moves the Elevator and Stinger to LOW position in sequence
     * <p>
     * Elevator goes to 0
     * <p>
     * Stinger goes to 0
     * @return Command
     */
    public static CommandBase elevatorStingerReturn()
    {
        CommandBase c = sequence(
            retractStinger(),
            elevatorToLow()
        ).withName("Return Elevator and Stinger");
        c.addRequirements(elevator, stinger);
        return c;
    }
    

    /////////////////////// GRABBER COMMANDS ////////////////////////////////////////////
    
    /**
     * Closes the Grabber
     */ 
    public static CommandBase closeGrabber() 
    {
        return Commands.runOnce(ChargedUp.grabber::grab)
            .withName("Close Grabber");
    }

    /**
     * Opens the grabber
     */
    public static CommandBase openGrabber() 
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
    public static CommandBase toggleGrabber()
    {
        return Commands.runOnce(ChargedUp.grabber::toggle)
            .withName("Toggle Grabber");
    }

    /////////////////////////////// SHWOOPER COMMMANDS ///////////////////////////
    /**
     * intake suck
     * @return Command
     */
    public static CommandBase shwooperSuck() 
    {
        return Commands.runOnce(ChargedUp.shwooper::suck)
            .withName("Intake Suck");
    }
 
    /**
     * intake spit
     * @return Command
     */
    public static CommandBase shwooperSpit() 
    {
        return Commands.runOnce(ChargedUp.shwooper::spit)
            .withName("Intake Spit");
    }

    /**
     * Stop intake
     * @return Command
     */
    public static CommandBase stopShwooper() 
    {
        return Commands.runOnce(ChargedUp.shwooper::stop)
            .withName("Intake Stop");
    }

    ////////////////////// AUTO COMMANDS //////////////////////////
    
    // picks up objects and raises the elevator to the middle after picking the object up
    public static CommandBase pickup() 
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
            closeGrabber()

            // Prepare to leave
            // elevatorToMid(),
        ).withName("Pickup");
    }

    public static CommandBase positionForScore(ScoreHeight height, Supplier<ScoringType> type)
    {
        CommandBase cmd = Commands.select(
            Map.of(
                ScoringType.PEG,   elevatorToConeMid().andThen(extendStinger()),
                ScoringType.SHELF, elevatorToCubeMid().andThen(extendStinger())
            ),
            () -> type.get()
        ); 
        
        if(height == ScoreHeight.LOW)
        {
            cmd = cmd.andThen(drivetrain.commands.goToPositionRelative(-2, 0));
        }

        return cmd;
    }

    public static CommandBase scoreCubeLow()
    {
        return Commands.sequence(
            //close grabber to position the cube closer to the intake
            closeGrabber(),
            waitSeconds(0.3),
            shwooperSpit(),
            waitSeconds(0.3),
            openGrabber(),

            runOnce(led::celebrate),

            //wait until the cube has shot out
            waitSeconds(1),
            stopShwooper()

        ).withName("deIntake score");
    }

    /**
     * Moves stinger/elevator to the specfied score place and opens the grabber
     * Retracts the stinger and bring the elevator to mid
     */
    public static CommandBase scoreFromHeightAndDynamicType(ScoreHeight height, Supplier<ScoringType> type)
    {
        return Commands.sequence(
            log("[SCORE] Beginning score sequence . . ."),
            closeGrabber(),

            //Align to score (only in teleop)
            Commands.sequence(
                //move sideways to the target
                moveToObjectSideways(() -> type.get().getDetectionType()),

                //move forward so that bumpers slam into scoring area (makes it perfect distance away to score)
                runOnce(() -> drivetrain.commands.driveForTime(0.5, 0, 0.5, 0).schedule()) //TODO: these numbers are completely made up
            ).unless(DriverStation::isAutonomous),

            //Prepare position
            log("[SCORE] Positioning elevator and stinger . . ."),  
            positionForScore(height, type), 

            //Drop grabber
            log("[SCORE] Dropping . . ."),
            waitSeconds(0.9),
            openGrabber(), 
            runOnce(led::celebrate),
            waitSeconds(0.7),

            //Return to position 
            log("[SCORE] Returning elevator and stinger to internal state . . ."),
            elevatorStingerReturn(),
            
            log("[SCORE] Done!")
        );
    }

    /**
     * Equivalent to {@link #scoreFromHeightAndDynamicType(ScoreHeight, Supplier)} but for
     * known scoring types. Names command appropriately.
     */
    public static CommandBase scoreFromHeightAndType(ScoreHeight height, ScoringType type)
    {
        return scoreFromHeightAndDynamicType(height, () -> type)
            .withName("Score " + height.toString() + " " + type.toString());
    }

    public static CommandBase scoreLowPeg()
    {return scoreFromHeightAndType(ScoreHeight.LOW, ScoringType.PEG);}
    public static CommandBase scoreLowShelf()
    {return scoreFromHeightAndType(ScoreHeight.LOW, ScoringType.SHELF);}
    public static CommandBase scoreLow()
    {return scoreFromHeightAndDynamicType(ScoreHeight.LOW, () -> ScoringType.from(ChargedUp.grabber.getHeldObject()))
        .withName("Score Low (General)");}

    public static CommandBase scoreMidPeg()
    {return scoreFromHeightAndType(ScoreHeight.MID, ScoringType.PEG);}
    public static CommandBase scoreMidShelf()
    {return scoreFromHeightAndType(ScoreHeight.MID, ScoringType.SHELF);}
    public static CommandBase scoreMid()
    {return scoreFromHeightAndDynamicType(ScoreHeight.MID, () -> ScoringType.from(ChargedUp.grabber.getHeldObject()))
        .withName("Score Mid (General)");}

    /**
     * Automatically compensates for angle offset caused by oscillatory motion of the 
     * 'Charging Station'.
     * 
     * Uses {@link Drivetrain#getChargeStationAngle()} to get the charge station angle,
     * and inverts the direction of motion (relative to the angle) using 
     * {@link Robot#CHARGER_STATION_INCLINE_INVERT}.
     */
    public static CommandBase balance()
    {
        return new CommandBase() 
        { 
            LinearFilter tiltChange = LinearFilter.backwardFiniteDifference(1, 5, TimedRobot.kDefaultPeriod);
            EdgeDetectFilter tiltEdge = new EdgeDetectFilter(EdgeType.RISING);

            Debouncer atRestDebouncer = new Debouncer(DriveConstants.CHARGER_STATION_AT_REST_DEBOUNCE_TIME.get(), DebounceType.kRising);
    
            int tiltCount = 0;

            double tilt;
            boolean isTilting;

            @Override
            public void initialize() 
            {
                tiltCount = 0;

                tiltChange.reset();
                tiltEdge.reset();

                atRestDebouncer.calculate(false);

                drivetrain.disableFieldRelative();
            }

            @Override
            public void execute()
            {
                // Get angle
                tilt = drivetrain.getChargeStationAngle();

                // Filter to get rate
                double tiltRate = tiltChange.calculate(tilt);

                // Check if tilting
                isTilting = Math.abs(tiltRate) > DriveConstants.CHARGER_STATION_TILT_SPEED_THRESHOLD.get();

                // Check if we've just started tilting
                boolean startedTilting = tiltEdge.calculate(isTilting);

                // Add to the tilt count if we've just started tilting
                if(startedTilting) tiltCount++;

                double velocity;

                // Drive with a speed of zero if we are tilting
                if(isTilting) velocity = 0;
                // Otherwise drive our max speed scaled by the amount of times we have tilted and the direction
                // of the current tilt.
                else 
                {
                    velocity = DriveConstants.MAX_SPEED_MPS * DriveConstants.CHARGER_STATION_MUL.get();
                    velocity = DriveConstants.CHARGER_STATION_INCLINE_INVERT ? -velocity : velocity;

                    velocity *= 1.0 / (tiltCount + 1);
                    velocity *= Math.signum(tilt);
                }

                drivetrain.setChassisSpeeds(0, velocity, 0);
            }

            @Override
            public boolean isFinished() 
            {
                boolean atRest = atRestDebouncer.calculate(!isTilting && Math.abs(tilt) <= Constants.Field.CHARGE_ANGLE_DEADBAND);
                if(atRest) led.celebrate();

                return atRest;
            }

            @Override
            public void end(boolean interrupted) 
            {
                drivetrain.enableFieldRelative();
            }
        }
        .withName("Balance");
    }

    /**
     * Decorate a command so that it fails immediately if 
     */
    public static CommandBase ifHasTarget(Command cmd)
    {
        return cmd.until(() -> !vision.hasObject()).unless(() -> !vision.hasObject());
    }

    /**
     * Turns to the object.
     * @return
     */
    public static CommandBase turnToCurrentObject() 
    {
        //DON'T USE PID for turn
        // return ifHasTarget(
        //     drivetrain.thetaPID.goToSetpoint(drivetrain::getObjectAngle, drivetrain)
        Debouncer hasEnded = new Debouncer(0.1, DebounceType.kRising);

        final double DEADBAND = 2;//degrees
        final double SPEED_MUL = DriveConstants.MAX_TURN_SPEED_RAD_PER_S * 0.75;

        return ifHasTarget(
            Commands.runOnce(() -> hasEnded.calculate(false))
                .andThen(run(() -> drivetrain.setChassisSpeeds(Math555.atLeast(SPEED_MUL * vision.getObjectAX() / 27.0, 0.4), 0, 0)))
                .until(() -> hasEnded.calculate(Math.abs(vision.getObjectAX()) < DEADBAND))
        ).withName("Turn to Current Object");
    }

    /**
     * Goes towards the object on the x-axis.
     * @return
     */
    public static CommandBase moveToCurrentObjectSideways()
    {
        Debouncer hasEnded = new Debouncer(0.1, DebounceType.kRising);

        final double DEADBAND = 2;
        final double SPEED_MUL = -DriveConstants.MAX_SPEED_MPS * 0.5;

        return ifHasTarget(
            Commands.runOnce(() -> hasEnded.calculate(false))
                .andThen(run(() -> drivetrain.setChassisSpeeds(0, 0, Math555.atLeast(SPEED_MUL * vision.getObjectAX() / 27.0, 0.2))))
                .until(() -> hasEnded.calculate(Math.abs(vision.getObjectAX()) < DEADBAND))
        ).withName("Move to Current Object Sideways");
    }

    /**
     * Turns to the object.
     * @return
     */
    public static CommandBase turnToObject(Supplier<DetectionType> type)
    {
        return Commands.sequence(
            waitForPipe(type),
            turnToCurrentObject(),
            Commands.runOnce(() -> vision.setTargetType(DetectionType.DEFAULT))
        );
    }

    /**
     * Goes towards the object on the x-axis.
     * @return
     */
    public static CommandBase moveToObjectSideways(Supplier<DetectionType> type)
    {
        return Commands.sequence(
            waitForPipe(type),
            moveToCurrentObjectSideways(),
            Commands.runOnce(() -> ChargedUp.vision.setTargetType(DetectionType.DEFAULT))
        );
    }

    public static CommandBase waitForPipe(Supplier<DetectionType> type)
    {
        return Commands.runOnce(() -> ChargedUp.vision.setTargetType(type.get()))
            .andThen(waitUntil(() -> vision.currentPipelineMatchesDetection(type)))
            .andThen(waitSeconds(0.1)); //TODO: this is very dumb
    }

    
    /**
     * Get the action command which corresponds to the given auto sequence string segment. 
     * This method can either take in transitions or actions.
     * 
     * @return The command, or none() with a log if an error occurs
     */
    public static CommandBase fromStringToCommand(String str, ArrayList<Trajectory> trajectories)
    {
        // Single actions
        if(str.length() == 1)
        {
            switch(str)
            {
                case "A": 
                case "C": return pickup();

                case "1": return scoreMidPeg(); //TODO: mnfjaidognjrae
                case "3": return scoreMidPeg(); //TODO: jnidagunra
                case "2": return scoreMidPeg(); //TODO: caitiue said so
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
            if(!Trajectories.exists(str))  
            {
                Logging.errorNoTrace("No transition '" + str + "' found!");
                return null;
            }

            PathPlannerTrajectory nextTrajectory = Trajectories.get(str, Constants.Auto.constraints());

            trajectories.add(nextTrajectory);

            return ChargedUp.drivetrain.commands.auto(nextTrajectory, HashMaps.of(
                "Elevator Mid Peg", elevatorToConeMid(),
                "Elevator Mid Shelf", elevatorToCubeMid(),
                "Intake On", shwooperSuck(),
                "Retract", elevatorStingerReturn(),
                "Intake Off", Commands.sequence(stopShwooper(), closeGrabber())
            ));
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
    public static CommandBase buildAuto(String[] list)
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
    public static CommandBase buildAuto(String p) 
    {
        String[] parts = Auto.parse(p);

        if(parts == null) 
        {
            return null;
        }

        return buildAuto(parts);
    }

    public static CommandBase backupAuto()
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