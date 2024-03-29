package org.team555;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ProxyCommand;
import edu.wpi.first.wpilibj2.command.ProxyScheduleCommand;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
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
import org.team555.constants.DriveConstants;

import static org.team555.ChargedUp.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPoint;
import com.pathplanner.lib.PathPlannerTrajectory.EventMarker;
import com.pathplanner.lib.auto.SwerveAutoBuilder;

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
    
    public static CommandBase logged(Command cmd, String str) 
    {
        return cmd
            .beforeStarting(log("[" + str + "] starting . . ."))
            .finallyDo(interrupted -> Logging.info("[" + str + "] " + (interrupted ? "interrupted" : "ended") + " . . ."));
    }
    public static CommandBase logged(Command cmd) 
    {
        return logged(cmd, cmd.getName());
    }

    public static CommandBase timed(Command cmd)
    {
        double[] time = {0};
        return cmd
            .beforeStarting(() -> time[0] = Timer.getFPGATimestamp())
            .andThen(log(() -> "Finished " + cmd.getName() + " in " + (Timer.getFPGATimestamp() - time[0]) + " seconds!"));
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
            .unless(() -> !stinger.isOut())
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
            .unless(() -> stinger.isOut())
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

        return either( 
            run(() -> elevator.PID.setSpeed(1))
                .until(() -> elevator.getHeight() >= height)
                .andThen(() -> elevator.PID.setSpeed(0)),
            
            run(() -> elevator.PID.setSpeed(-1))
                .until(() -> elevator.getHeight() <= height)
                .andThen(() -> elevator.PID.setSpeed(0)),

            () -> elevator.getHeight() < height
        )
        .withName("Elevator to " + height + "m");

        // return elevator.PID.goToSetpoint(height, elevator) 
        //     .withName("Elevator to " + height + "m");
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
    public static CommandBase elevatorToConeHigh()
    {
        return elevatorTo(ElevatorConstants.HIGH_HEIGHT_CONE);
    }

    /**
     * Sets the elevator to the height of the middle cube shelf
     * @return Command
     */
    public static CommandBase elevatorToCubeHigh()
    {
        return elevatorTo(ElevatorConstants.HIGH_HEIGHT_CUBE);
    }

    public static CommandBase elevatorToMid()
    {
        return elevatorTo(ElevatorConstants.MID_HEIGHT);
    }

    /**
     * Sets the elevator high enough so that the human player can feed it a cone. Currently 5/6 max height.
     * @return Command
     */
    public static CommandBase elevatorToTop()
    {
        return elevatorTo(ElevatorConstants.MAX_HEIGHT);
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
        CommandBase c =
            retractStinger()
            .andThen(elevatorToLow())
            .withName("Return Elevator and Stinger");
        c.addRequirements(elevator, stinger);
        return c;
    }
    
    public static CommandBase elevatorStingerToConeHigh()
    {
        CommandBase c = sequence(
            elevatorToConeHigh(),
            extendStinger()
        );
        c.addRequirements(elevator, stinger);
        return c;
    }

    public static CommandBase elevatorStingerToCubeHigh()
    {
        CommandBase c = parallel(
            elevatorToCubeHigh(), //BANANAS SOUP SHIT
            Commands.sequence(
                waitUntil(() -> Math.abs(elevator.getHeight() - ElevatorConstants.HIGH_HEIGHT_CUBE) < 0.05),
                extendStinger()
            )
        );
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
     * intake spit
     * @return Command
     */
    public static CommandBase shwooperSpitFast() 
    {
        return Commands.runOnce(ChargedUp.shwooper::spitFast)
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
    public static CommandBase pickup(double driveTime) 
    {
        return logged(Commands.sequence(
            // Ensure grabber released
            openGrabber(),
            log("[PICK UP] grabber opened"),

            // Lower grabber in place
            elevatorStingerReturn(),
            log("[PICK UP] elevator stinger returned.."),

            // Suck it
            // moveToObjectSideways(() -> DetectionType.CONE),

            log("[PICK UP] object sideways"),
            shwooperSuck(),
            Commands.sequence(
                moveToObjectSideways(() -> DetectionType.CONE, DriveConstants.FORWARD_VELOCITY_DURING_PICKUP),
                drivetrain.commands.driveForTimeRelative(driveTime,0,1.5,0)
            )
                .until(shwooper::manipulatedObject)
                .withTimeout(2),
            waitSeconds(0.3),
            stopShwooper(),
            log("[PICK UP] shwooper stop")

            // Grab it
            // closeGrabber()

            // Prepare to leave
            // elevatorToMid(),
            // log("[PICK UP] pick up over")
        )).withName("Pickup");
    }

    /**
     * Get the command responsible for aligning the drivetrain to the given score type.
     */
    public static CommandBase positionDrivetrainForScore(Supplier<ScoringType> type)
    {
        return drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(180));
    }

    /**
     * Get the command responsible for aligning the drivetrain to the given score type.
     */
    public static CommandBase positionElevatorAssemblyForScore(ScoreHeight height, Supplier<ScoringType> type)
    {
        CommandBase cmd = Commands.either(
            elevatorStingerToConeHigh(),
            elevatorStingerToCubeHigh(),
            () -> type.get() == ScoringType.PEG
        );

        return cmd;
    }

    /**
     * Get the command responsible for scoring in the hybrid zone with a cube.
     */
    public static CommandBase scoreCubeLow(boolean isAuto)
    {
        return logged(Commands.sequence(
            //close grabber to position the cube closer to the intake
            openGrabber(),
            // waitSeconds(0.3),
            either(
                shwooperSpitFast(), 
                shwooperSpit(), 
                () -> isAuto
            ),
            openGrabber(),
            

            //wait until the cube has shot out
            waitUntil(shwooper::manipulatedObject)
                .andThen(led::celebrate)
                .withTimeout(1.2),
            waitSeconds(0.3),
            stopShwooper()


        ).withName("deIntake score"));
    }


    /**
     * Moves stinger/elevator to the specfied score place and opens the grabber
     * Retracts the stinger and bring the elevator to mid
     */
    public static CommandBase scoreFromHeightAndDynamicType(ScoreHeight height, Supplier<ScoringType> type, boolean skipObjectAlignment, boolean resetAfterScore)
    {
        return timed(Commands.sequence(
            log("[SCORE] Beginning score sequence . . ."),
            closeGrabber(),

            // Align to score
            log("[SCORE] Positioning . . ."), 
            Commands.sequence(
                //Align drivetrain to score
                positionDrivetrainForScore(type).unless(() -> skipObjectAlignment),

                //Prepare elevator assembly
                positionElevatorAssemblyForScore(height, type)
            ),

            //Drop grabber
            log("[SCORE] Dropping . . ."),
            waitSeconds(0.45),
            openGrabber(), 
            runOnce(led::celebrate),
            waitSeconds(0.1),
            waitSeconds(0.5).unless(() -> type.get() == ScoringType.PEG),

            //Return to position 
            log("[SCORE] Returning elevator and stinger to internal state . . ."),

            either(
                elevatorStingerReturn(),
                retractStinger(),
                () -> resetAfterScore || true //bc this should always be true and our previous code is stupid
            ),
            
            log("[SCORE] Done!")
        ));
    }

    /**
     * Equivalent to {@link #scoreFromHeightAndDynamicType(ScoreHeight, Supplier)} but for
     * known scoring types. Names command appropriately.
     */
    public static CommandBase scoreFromHeightAndType(ScoreHeight height, ScoringType type, boolean skipObjectAlignment, boolean resetAfterScore)
    {
        return scoreFromHeightAndDynamicType(height, () -> type, skipObjectAlignment, resetAfterScore)
            .withName("Score " + height.toString() + " " + type.toString());
    }

    public static CommandBase scoreLowPeg(boolean skipObjectAlignment, boolean resetAfterScore)
    {return scoreFromHeightAndType(ScoreHeight.LOW, ScoringType.PEG, skipObjectAlignment, resetAfterScore);}
    public static CommandBase scoreLowShelf(boolean skipObjectAlignment, boolean resetAfterScore)
    {return scoreFromHeightAndType(ScoreHeight.LOW, ScoringType.SHELF, skipObjectAlignment, resetAfterScore);}
    public static CommandBase scoreLow(boolean skipObjectAlignment, boolean resetAfterScore)
    {return scoreFromHeightAndDynamicType(ScoreHeight.LOW, () -> ScoringType.from(ChargedUp.grabber.getHeldObject()), skipObjectAlignment, resetAfterScore)
        .withName("Score Low (General)");}

    public static CommandBase scoreHighPeg(boolean skipObjectAlignment, boolean resetAfterScore)
    {return scoreFromHeightAndType(ScoreHeight.HIGH, ScoringType.PEG, skipObjectAlignment, resetAfterScore);}
    public static CommandBase scoreHighShelf(boolean skipObjectAlignment, boolean resetAfterScore)
    {return scoreFromHeightAndType(ScoreHeight.HIGH, ScoringType.SHELF, skipObjectAlignment, resetAfterScore);}
    public static CommandBase scoreHigh(boolean skipObjectAlignment, boolean resetAfterScore)
    {return scoreFromHeightAndDynamicType(ScoreHeight.HIGH, () -> ScoringType.from(ChargedUp.grabber.getHeldObject()), skipObjectAlignment, resetAfterScore)
        .withName("Score High (General)");}

    
    public static CommandBase balance() //the sneakers :(
    {
        return drivetrain.commands.disableFieldRelative().andThen(Commands.run(() -> {
            double velocity = DriveConstants.MAX_SPEED_MPS * DriveConstants.CHARGER_STATION_MUL.get();
            velocity = DriveConstants.CHARGER_STATION_INCLINE_INVERT ? -velocity : velocity;

            velocity *= Math.signum(gyroscope.getRoll());

            drivetrain.setChassisSpeeds(0, velocity, 0);
        }))
        .until(() -> 
            Math.abs(gyroscope.getRollRate()) > DriveConstants.CHARGER_STATION_TILT_SPEED_THRESHOLD.get()
            && Math.abs(gyroscope.getRoll()) < 12)
        .finallyDo(inter -> {
            Logging.info("ended balance: " + gyroscope.getRollRate());
            drivetrain.enableFieldRelative();
            drivetrain.enableXMode();
        });
    }
    
    public static Command balanceOriginal()
    {
        return Commands.run(() -> 
            {
                double angle = drivetrain.getChargeStationAngle();
                double speed = DriveConstants.MAX_SPEED_MPS * DriveConstants.CHARGER_STATION_MUL.get() * angle / Constants.Field.CHARGE_ANGLE_RANGE_DEG;
                if (angle <= Constants.Field.CHARGE_ANGLE_DEADBAND.get() && angle >= -Constants.Field.CHARGE_ANGLE_DEADBAND.get()) 
                {
                    speed = 0;
                }
                speed = DriveConstants.CHARGER_STATION_INCLINE_INVERT ? -speed : speed;
                
                drivetrain.setChassisSpeeds(drivetrain.getSpeedsFromMode(0, speed, 0));
            })
            .finallyDo(interupted -> 
            {
                drivetrain.enableXMode();
            })
            .withName("Balance");
    }
    /**

    /**
     * Automatically compensates for angle offset caused by oscillatory motion of the 
     * 'Charging Station'.
     * 
     * Uses {@link Drivetrain#getChargeStationAngle()} to get the charge station angle,
     * and inverts the direction of motion (relative to the angle) using 
     * {@link Robot#CHARGER_STATION_INCLINE_INVERT}.
     */
    public static CommandBase balanceOLD()
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
                boolean atRest = atRestDebouncer.calculate(!isTilting && Math.abs(tilt) <= Constants.Field.CHARGE_ANGLE_DEADBAND.get());
                if(atRest) led.celebrate();

                return atRest;
            }

            @Override
            public void end(boolean interrupted) 
            {
                drivetrain.enableFieldRelative();
                drivetrain.enableXMode();
                Logging.info("[Balance] over");
            }
        }
        .withName("Balance");
    }

    /**
     * Decorate a command so that it fails immediately if the vision system currently does not have an object
     * in its view.
     */
    public static CommandBase ifHasTarget(Command cmd)
    {
        return cmd.until(() -> !vision.hasObject()).unless(() -> !vision.hasObject());
    }
    public static CommandBase ifHasTargetDebounce(Command cmd)
    {
        Debouncer hasEnded = new Debouncer(0.1, DebounceType.kFalling);

        return Commands.sequence(
            Commands.runOnce(() -> hasEnded.calculate(vision.hasObject())),
            cmd.until(() -> hasEnded.calculate(vision.hasObject()))
        );
    }

    /**
     * Turns to the object.
     * @return
     */
    public static CommandBase turnToObject(Supplier<DetectionType> type) 
    {

        final double DEADBAND = 2;//degrees
        final double SPEED_MUL = DriveConstants.MAX_TURN_SPEED_RAD_PER_S * 0.15;
        Debouncer hasEnded = new Debouncer(0.1, DebounceType.kRising);

        CommandBase turn = ifHasTarget(
            Commands.runOnce(() -> hasEnded.calculate(false))
                .andThen(run(() -> drivetrain.setChassisSpeeds(-Math555.atLeast(SPEED_MUL * vision.getObjectAX() / 27.0, 0.275), 0, 0)))
                    .until(() -> hasEnded.calculate(Math.abs(vision.getObjectAX()) < DEADBAND))
        );
        
        // ifHasTargetDebounce(
        //     Commands.run(() -> drivetrain.setChassisSpeeds(-Math555.atLeast(SPEED_MUL * vision.getObjectAX() / 27.0, 0.275), 0, 0))
        //             .until(() -> Math.abs(vision.getObjectAX()) < DEADBAND)
        // );
        
        if(type == null) return turn;
        
        return Commands.sequence(
            log("[TURN TO OBJECT] starting"),
            waitForPipe(type),
            log("[TURN TO OBJECT] reached correct pipeline"),
            turn,
            Commands.runOnce(() -> vision.setTargetType(DetectionType.DEFAULT)), 
            log("[TURN TO OBJECT] did the turn :D")
        ).withName("Turn to Object");
    }

    /**
     * Goes towards the object on the x-axis.
     * 
     * @param type DetectionType to move to. {@code null} can be passed in if vision is already using the correct pipeline
     * @return
     */
    public static CommandBase moveToObjectSideways(Supplier<DetectionType> type, double forwardVelocity)
    {
        final double forwardVelocityClamped = Math555.clamp(forwardVelocity, 0, DriveConstants.MAX_SPEED_MPS);
        Debouncer hasEnded = new Debouncer(0.1, DebounceType.kRising);

        final double DEADBAND = 2;
        final double SPEED_MUL = -DriveConstants.MAX_SPEED_MPS * 0.4;

        CommandBase moveSideways = ifHasTarget(
            Commands.runOnce(() -> hasEnded.calculate(false))
                .andThen(run(() -> drivetrain.setChassisSpeeds(0, forwardVelocityClamped, Math555.atLeast(SPEED_MUL * vision.getObjectAX() / 27.0, 0.11)))
                    .until(() -> hasEnded.calculate(Math.abs(vision.getObjectAX()) < DEADBAND)))
        );


        if(type == null) return moveSideways;

        return Commands.sequence(
            log("[MOVE SIDEWAYS TO OBJECT] starting"),
            waitForPipe(type),
            log("[MOVE SIDEWAYS TO OBJECT] reached correct pipe"),
            moveSideways,
            Commands.runOnce(() -> vision.setTargetType(DetectionType.DEFAULT)),
            log("[MOVE SIDEWAYS TO OBJECT] finished the movement :D")
        ).withName("Move to Object Sideways");
    }

    /**
     * Goes towards the object on the y-axis.
     * <p>
     * <b> This should really only be used to align with tape when scoring </b>
     * 
     * @param type DetectionType to move to. {@code null} can be passed in if vision is already using the correct pipeline
     * @return
     */
    public static CommandBase moveToObjectForward()
    {
        final double SPEED_MUL = -DriveConstants.MAX_SPEED_MPS * 0.5;
        final double DEADBAND = 1;

        return logged(ifHasTarget(
            Commands.run(() -> drivetrain.setChassisSpeeds(0, Math555.atLeast(SPEED_MUL * vision.getObjectAY() / 27.0, 0.2), 0))
                .until(() -> Math.abs(vision.getObjectAY()) <= DEADBAND)
                .withTimeout(3)
                .andThen(drivetrain.commands.driveForTimeRelative(1.7/2, 0, 0.2*3, 0))
                .withName("MOVE FORWARD TO TAPE")
        ));
    }

    public static CommandBase alignWithAprilTagForScoreBackup()
    {
        return Commands.sequence(
            waitForPipe(() -> DetectionType.APRIL_TAG),
            Commands.sequence(
                moveToObjectSideways(null, 0),
                moveToObjectForward()
            ),
            runOnce(() -> vision.setTargetType(DetectionType.DEFAULT))
        );
    }

    /**
     * Create a command which aligns the drivetrain with the april tag in front of it in order to score.
     * @return
     */
    public static CommandBase alignWithAprilTagForScore()
    {   
        final double TOLERANCE = 0.1;
        final double SPEED_MAX = 1;
        
        Debouncer hasTargetDebounce = new Debouncer(0.1, DebounceType.kFalling);

        return logged(logged(waitForPipe(() -> DetectionType.APRIL_TAG).withName("Wait for april tag")).andThen
        (
            Commands.runOnce(() -> hasTargetDebounce.calculate(!vision.hasObject()))
                .andThen(
                    new CommandBase() 
                    {
                        Debouncer hasEndedDebounce = new Debouncer(0.1, DebounceType.kRising);
                        boolean hasEnded;

                        @Override
                        public void initialize() 
                        {
                            hasEndedDebounce.calculate(false);
                            hasEnded = false;
                        }

                        @Override
                        public boolean isFinished() 
                        {
                            return hasEnded;
                        }

                        @Override
                        public void execute()
                        {
                            Translation2d curpose = vision.getAprilTagRobotSpace();
                            Translation2d diff = DriveConstants.DESIRED_APRIL_TAG_SCORE_POSE.minus(curpose);
                            Logging.info("[ALIGN TO APRIL TAG] Difference X: " + diff.getX());
                            Logging.info("[ALIGN TO APRIL TAG] Difference Y: " + diff.getY());

                            hasEnded = hasEndedDebounce.calculate(diff.getNorm() < TOLERANCE);

                            double vx = Math555.atLeast(Math555.clamp(SPEED_MAX * diff.getX(), -SPEED_MAX, SPEED_MAX), 0.2);
                            double vy = Math555.atLeast(Math555.clamp(SPEED_MAX * -diff.getY(), -SPEED_MAX, SPEED_MAX), 0.3);

                            drivetrain.setChassisSpeeds(0, vx, vy);
                        }

                        @Override
                        public void end(boolean interrupt)
                        {
                            Commands.runOnce(() -> vision.setTargetType(DetectionType.DEFAULT));
                        }
                    }
                ).until(() -> hasTargetDebounce.calculate(!vision.hasObject()))
        ).withName("ALIGN TO APRIL TAG"));
    }

    /**
     * Creates a command which waits for the given detection type to be active on the limelight.
     */
    public static CommandBase waitForPipe(Supplier<DetectionType> type)
    {
        return Commands.runOnce(() -> vision.setTargetType(type.get()))
            .andThen(waitUntil(() -> vision.currentPipelineMatches(type.get())))
            .andThen(waitSeconds(0.2)) //TODO: this is very dumb
            .unless(RobotBase::isSimulation);
            // .unless(() -> vision.currentPipelineMatches(type.get())); 
    }

    /**
     * Check if the first score is mid for the given autonomous command
     */
    public static boolean firstScoreIsMid(String full)
    {
        return full.length() <= 3;
    }

    
    /**
     * Get the action command which corresponds to the given auto sequence string segment. 
     * This method can either take in transitions or actions.
     * 
     * @return The command, or none() with a log if an error occurs
     */
    public static CommandBase fromStringToCommand(String str, String full, boolean firstIsHigh, SwerveAutoBuilder autoBuilder, ArrayList<PathPlannerTrajectory> trajectories)
    {
        // Single actions
        if(str.length() == 1) //DEPLOY CODE PLSSSSS
        {
            switch(str)
            {
                case "A": 
                case "C": return drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(0)).withTimeout(0.3).andThen(pickup(0.75));

                case "D": {
                    double angle = DriverStation.getAlliance() == Alliance.Blue ? 270 : 90;
                    return drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(angle)).withTimeout(0.3).andThen(pickup(1.1));
                }
                case "E": {
                    double angle = DriverStation.getAlliance() == Alliance.Blue ? 90 : 270;
                    return drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(angle)).withTimeout(0.3).andThen(pickup(1.1));
                }
                case "1":
                case "2":
                case "3": 
                {
                    if(firstIsHigh)
                    {
                        boolean resetAfterScoring = (full.length() == 2 && full.charAt(1) == 'B');
                        return scoreHighShelf(true, resetAfterScoring);
                    }
                    else 
                    {
                        return scoreCubeLow(false);
                    } 
                }

                case "4":
                case "5": 
                {
                    // Parallelization may occur here
                    if(full.endsWith(str)) 
                    {
                        return scoreCubeLow(false);
                    }
                    else 
                    {
                        return Commands.none();
                    }
                }

                case "B": return drivetrain.commands.driveForTimeRelative(Constants.Auto.DRIVE_TIME_BEFORE_BALANCE.get(), 0, DriveConstants.MAX_SPEED_MPS, 0)
                    .until(() -> Math.abs(drivetrain.getChargeStationAngle()) > 10)
                    .andThen(waitSeconds(1))
                    .andThen(drivetrain.commands.driveForTimeRelative(Constants.Auto.DRIVE_TIME_AFTER_BALANCE_CLIP.get(), 0, DriveConstants.MAX_SPEED_MPS, 0))
                    .andThen(balanceOriginal());

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

            PathConstraints constraints = Constants.Auto.constraints();

            if(str.charAt(1) == 'B')
            {
                constraints = new PathConstraints(4, 2);
            }

            PathPlannerTrajectory nextTrajectory = Trajectories.get(str, constraints);

            trajectories.add(nextTrajectory);

            CommandBase cmd = ChargedUp.drivetrain.commands.trajectory(autoBuilder, nextTrajectory);

            // Check for first path and add a reset if it is
            if(trajectories.size() == 1)
            {
                cmd = autoBuilder.resetPose(nextTrajectory).andThen(cmd);
            }

            return cmd.withName(str);
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
    public static CommandBase buildAuto(String full, boolean firstIsHigh, String[] list)
    {
        Trajectories.clearAll();
        CommandBase[] commandList = new CommandBase[list.length];
        ArrayList<PathPlannerTrajectory> allTrajectories = new ArrayList<>();
        

        // Get the auto builder //
        HashMap<String, Command> markers = HashMaps.of(
            "Elevator Mid Peg"   , elevatorToConeHigh(),
            "Elevator Mid Shelf" , elevatorToCubeHigh(),
            "Score Low"          , scoreCubeLow(false), 
            "Intake On"          , shwooperSuck(),
            "Retract"            , elevatorStingerReturn(),
            "Intake Off"         , Commands.sequence(stopShwooper(), closeGrabber()),
            "Pickup Pipeline"    , waitForPipe(() -> DetectionType.CONE)
        );

        String debugAuto = "";
        SwerveAutoBuilder builder = drivetrain.commands.autoBuilder(markers);

        // Iterate all of the string segments
        for (int i = 0; i < list.length; i++)
        {
            commandList[i] = fromStringToCommand(list[i], full, firstIsHigh, builder, allTrajectories);

            // Error out here if necessary
            if(commandList[i] == null)
            {
                Logging.info("SOMEHTING NULLL JGINSJGSNGJIFNGJ");
                return null;
            }

            debugAuto = debugAuto + commandList[i].getName() + ", ";
        }

        // Calculate the sum trajectory
        if(allTrajectories.size() > 0)
        {
            Trajectories.displayAll(allTrajectories);
        }

        // Parallelize the first score
        if(!firstIsHigh && commandList.length > 1)
        {
            CommandBase first = commandList[0];
            commandList[0] = Commands.none();
            commandList[1] = commandList[1].alongWith(first);
        }
        
        // Return the sum command (with a navx set-180)
        Logging.info("[AUTO BUILD]" + debugAuto);

        return timed(Commands.runOnce(gyroscope::setSouth)
            .andThen(Commands.sequence(commandList))
            .withName("Auto " + full));
    }
    
    /**
     * Create the autonomous command from the given parsed autonomous sequence string.
     * 
     * @return The command, with none() inserted into places in the sequence where an error occured,
     * or none() itself if parsing or lexing fails.
     */
    public static CommandBase buildAuto(String full, boolean firstIsHigh) 
    {
        String[] parts = Auto.parse(full);

        if(parts == null) 
        {
            return null;
        }
        Logging.info(Arrays.toString(parts));

        return buildAuto(full, firstIsHigh, parts);
    }
}