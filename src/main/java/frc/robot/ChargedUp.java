// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.animation.DefaultAnimation;
import frc.robot.animation.FadeTransition;
import frc.robot.animation.MagicAnimation;
import frc.robot.animation.QuickSlowFlash;
import frc.robot.components.managers.Auto;
import frc.robot.components.managers.ColorSensor;
import frc.robot.components.managers.LED;
import frc.robot.components.subsystems.Drivetrain;
import frc.robot.components.subsystems.Elevator;
import frc.robot.components.subsystems.Grabber;
import frc.robot.components.subsystems.Shwooper;
import frc.robot.components.subsystems.Stinger;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.DetectionType;
import frc.robot.util.frc.GameController;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.GameController.Axis;
import frc.robot.util.frc.GameController.Button;
import frc.robot.util.frc.GameController.DPad;
import frc.robot.util.frc.commandrobot.RobotContainer;
import frc.robot.vision.LimelightSystem;
import frc.robot.vision.PhotonSystem;
import frc.robot.vision.VisionSystem;

import static frc.robot.Constants.*;

import com.kauailabs.navx.frc.AHRS;

public class ChargedUp extends RobotContainer 
{
    public static final Field2d field = new Field2d();

    // CONTROLLERS //
    public static final GameController driverController = GameController.from(
        ControlScheme.DRIVER_CONTROLLER_TYPE,
        ControlScheme.DRIVER_CONTROLLER_PORT);
    public static final GameController operatorController = GameController.from(
        ControlScheme.OPERATOR_CONTROLLER_TYPE,
        ControlScheme.OPERATOR_CONTROLLER_PORT);

    // SHUFFLEBOARD //
    private static final ShuffleboardTab debugTab = Shuffleboard.getTab("Debug");
    public static ShuffleboardTab getDebugTab() {return debugTab;}
    private static final ShuffleboardTab mainTab = Shuffleboard.getTab("Main");
    public static ShuffleboardTab getMainTab() {return mainTab;}

    // COMPONENTS //
    public static final AHRS gyroscope = new AHRS();
    public static final LED  led       = new LED();

    // public static final Limelight   limelight   = new ();
    public static final VisionSystem vision      = new PhotonSystem();
    public static final ColorSensor  colorSensor = new ColorSensor();

    public static final Drivetrain drivetrain = new Drivetrain();
    public static final Elevator   elevator   = new Elevator();
    public static final Shwooper   shwooper   = new Shwooper();
    public static final Grabber    grabber    = new Grabber();
    public static final Stinger    stinger    = new Stinger();

    // INITIALIZER //
    @Override 
    public void initialize() 
    { 
        led.setTransition(FadeTransition::new);

        setupDebugTab();
        setupMainTab();

        vision.setTargetType(DetectionType.APRIL_TAG);

        // HANDLE DRIVING //
        drivetrain.setDefaultCommand(Commands.run(() -> 
            {
                if(!DriverStation.isTeleop())
                {
                    drivetrain.set(0,0,0);
                    return;
                }

                drivetrain.setInput(
                    JoystickInput.getRight(driverController, false, false),
                    JoystickInput.getLeft (driverController, false, false)
                );
            },
            drivetrain
        ));

        // driverController.getButton(Button.A_CROSS)
        //     .onTrue(Commands.runOnce(() -> DefaultAnimation.setViolet()));
        // driverController.getButton(Button.B_CIRCLE)
        //     .onTrue(Commands.runOnce(() -> led.add(MagicAnimation.fire(4))));
        // driverController.getButton(Button.A_CROSS)
        //     .onTrue(Commands.runOnce( () -> led.add(new CircusAnimation(7))));
        // driverController.getButton(Button.Y_TRIANGLE)
        //     .onTrue(Commands.runOnce(() -> led.add(MagicAnimation.galaxy(5))));
        // driverController.getButton(Button.X_SQUARE)
        //     .onTrue(Commands.runOnce(() -> led.add(new QuickSlowFlash(Color.kYellow))));
    
        // Buttons for Field Relative and Speed
        driverController.getButton(Button.A_CROSS)
            .onTrue(drivetrain.commands.enableFieldRelative());
        driverController.getButton(Button.X_SQUARE)
            .onTrue(drivetrain.commands.disableFieldRelative());
        driverController.getButton(Button.Y_TRIANGLE)
            .toggleOnTrue(Commands2023.balance());
        driverController.getButton(Button.RIGHT_BUMPER)
            .onTrue(drivetrain.commands.increaseSpeed());
        driverController.getButton(Button.LEFT_BUMPER)
            .onTrue(drivetrain.commands.decreaseSpeed());
        
        // Button to Zero NavX
        driverController.getButton(Button.START_TOUCHPAD)
            .onTrue(Commands.runOnce(() -> {
                if(DriverStation.isEnabled()) 
                {
                    Logging.warning("Attempted to zeroed NavX while enabled; refusing input.");
                    return;
                }

                gyroscope.zeroYaw();
                Logging.info("Zeroed NavX!");
            }));

        // OPERATOR CONTROLS //

        // D-Pad Controls
        operatorController.getDPad(DPad.UP)
            .onTrue(Commands2023.elevatorStingerToHigh());
        operatorController.getDPad(DPad.LEFT)
            .onTrue(Commands2023.elevatorStingerToMid());
        operatorController.getDPad(DPad.DOWN)
            .onTrue(Commands2023.elevatorStingerToLow());

        // Stinger
        stinger.setDefaultCommand(Commands.run(() -> {
            JoystickInput right = JoystickInput.getRight(
                operatorController, 
                false, 
                false);
            Robot.Stinger.JOY_ADJUSTER.adjustX(right);
            stinger.setSpeed(right.getX());
        }, stinger));

        // Grabber
        operatorController.getButton(Button.A_CROSS)
            .onTrue(Commands2023.toggleGrabber());

        // Schwooper 
        
        // suck button
        operatorController.getAxis(Axis.LEFT_TRIGGER)
            .whenGreaterThan(0.5)
            .onTrue (Commands2023.shwooperSuck())
            .onFalse(Commands2023.stopShwooper());
        // button to spit/stop schwooper
        operatorController.getAxis(Axis.RIGHT_TRIGGER)
            .whenGreaterThan(0.5)
            .onTrue (Commands2023.shwooperSpit())
            .onFalse(Commands2023.stopShwooper());
        /**
         * toggles shwooper
         */
        operatorController.getButton(Button.X_SQUARE)
            .toggleOnTrue(Commands2023.toggleShwooper());

        //Elevator 
        elevator.setDefaultCommand(Commands.run(() -> 
        {
            JoystickInput left = JoystickInput.getLeft(
                operatorController, false, false);
            
            Robot.Elevator.JOY_ADJUSTER.adjustY(left);

            elevator.setSpeed(left.getY());
        }, elevator));

        driverController.getDPad(DPad.UP).onTrue(drivetrain.commands.goToAngle(Math.PI/2));
        driverController.getDPad(DPad.RIGHT).onTrue(drivetrain.commands.goToAngle(0));
        driverController.getDPad(DPad.DOWN).onTrue(drivetrain.commands.goToAngle((3*Math.PI)/2));
        driverController.getDPad(DPad.LEFT).onTrue(drivetrain.commands.goToAngle(Math.PI));

        //Auto Score
        operatorController.getButton(Button.Y_TRIANGLE)
            .onTrue(Commands2023.scoreHigh());

        operatorController.getButton(Button.B_CIRCLE)
            .onTrue(Commands2023.scoreMid());
        
        //LEDs
        operatorController.getButton(Button.RIGHT_BUMPER)
            .onTrue(Commands2023.quickSlowFlashYellow());
        operatorController.getButton(Button.LEFT_BUMPER)
            .onTrue(Commands2023.quickSlowFlashPurple());
    }

    
    // AUTO //
    public static final Auto auto = new Auto();

    @Override
    public Command getAuto() 
    {
        return auto.get();
    }

    public void setupDebugTab()
    {
        debugTab.add("X-PID Controller", drivetrain.xController)
            .withPosition(0, 3)
            .withSize(2, 3);
        debugTab.add("Y-PID Controller", drivetrain.yController)
            .withPosition(0+2, 3)
            .withSize(2, 3);
        debugTab.add("θ-PID Controller", drivetrain.thetaController)
            .withPosition(0+2+2, 3)
            .withSize(2, 3);

        debugTab.addStringArray("All Logs", Logging::allLogsArr)
            .withPosition(0+2+2+2, 3)
            .withSize(4, 2);
    }

    // SHUFFLEBOARD //
    public void setupMainTab()
    {
        // SETUP FIELD //
        mainTab
            .add("Field", field)
            .withPosition(0, 0);
        
        // SETUP LOGGING //

        // IS USING FIELD RELATIVE //
        mainTab
            .addBoolean("Field Relative", drivetrain::usingFieldRelative)
            .withWidget(BuiltInWidgets.kBooleanBox)
            .withSize(2, 1)
            .withPosition(0, 0);

        // LOGGING LOG RECENT //
        mainTab
            .addString("Recent Log", Logging::mostRecentLog)
            .withSize(3, 1)
            .withPosition(0, 2);

        // CAMERAS //
        // mainTab
        //     .addCamera("Cameras", "Vision Camera", vision.getCameraStreamURL())
        //     .withSize(3, 2)
        //     .withPosition(0, 3);

        // GYROSCOPE VALUE //
        mainTab
            .addNumber("Gyroscope", () -> {
                double y = gyroscope.getYaw();
                return y > 0 ? y : 360+y; 
            })
            .withWidget(BuiltInWidgets.kGyro)
            .withSize(2, 2)
            .withPosition(2, 0);
        
        // MAX LINEAR SPEED //
        mainTab
            .addNumber("Max Linear Speed (m/s)", () -> drivetrain.getCurrentSpeedLimits()[0] * Drive.MAX_SPEED_MPS)
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2, 2)
            .withPosition(4, 0);
        // MAX ANGULAR SPEED //
        mainTab
            .addNumber("Max Angular Speed (rad/s)", () -> drivetrain.getCurrentSpeedLimits()[1] * Drive.MAX_TURN_SPEED_RAD_PER_S)
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(2, 2)
            .withPosition(4, 2);
        
        // HELD OBJECT //
        mainTab
            .addString("Held Object", grabber::getHeldObjectName)
            .withSize(1, 1)
            .withPosition(4, 4);

        //TODO: Fixed Cameras, Field View, Auto Input
    }
}