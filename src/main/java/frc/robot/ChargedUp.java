// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.components.managers.Auto;
import frc.robot.components.managers.ColorSensor;
import frc.robot.components.managers.LED;
import frc.robot.components.managers.Limelight;
import frc.robot.components.managers.Photon;
import frc.robot.components.subsystems.Drivetrain;
import frc.robot.components.subsystems.Elevator;
import frc.robot.components.subsystems.Grabber;
import frc.robot.components.subsystems.Shwooper;
import frc.robot.components.subsystems.Stinger;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.animation.CircusAnimation;
import frc.robot.structure.animation.DefaultAnimation;
import frc.robot.structure.animation.FadeTransition;
import frc.robot.structure.animation.MagicAnimation;
import frc.robot.structure.animation.QuickSlowFlash;
import frc.robot.structure.animation.RainbowAnimation;
import frc.robot.structure.animation.SolidAnimation;
import frc.robot.structure.animation.WipeTransition;
import frc.robot.util.frc.GameController;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.GameController.Axis;
import frc.robot.util.frc.GameController.Button;
import frc.robot.util.frc.GameController.DPad;
import frc.robot.util.frc.commandrobot.RobotContainer;

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

    // COMPONENTS //
    public static final AHRS gyroscope = new AHRS();
    public static final LED  led       = new LED();

    // public static final Limelight   limelight   = new Limelight();
    public static final Photon      photon      = new Photon();
    public static final ColorSensor colorSensor = new ColorSensor();

    public static final Drivetrain drivetrain = new Drivetrain(photon);
    public static final Elevator   elevator   = new Elevator();
    public static final Shwooper   shwooper   = new Shwooper();
    public static final Grabber    grabber    = new Grabber();
    public static final Stinger    stinger    = new Stinger();

    // INITIALIZER //
    @Override 
    public void initialize() 
    { 
        led.setTransition(FadeTransition::new);

        Shuffleboard
            .getTab("Main")
            .add("Field", field);

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

        driverController.getButton(Button.A_CROSS)
            .onTrue(Commands.runOnce( () -> DefaultAnimation.setViolet()));
        driverController.getButton(Button.B_CIRCLE)
            .onTrue(Commands.runOnce( () -> led.add(MagicAnimation.fire(4))));
        // driverController.getButton(Button.A_CROSS)
        //     .onTrue(Commands.runOnce( () -> led.add(new CircusAnimation(7))));
        driverController.getButton(Button.Y_TRIANGLE)
            .onTrue(Commands.runOnce( () -> led.add(MagicAnimation.galaxy(5))));
        driverController.getButton(Button.X_SQUARE)
            .onTrue(Commands.runOnce( () -> led.add(new QuickSlowFlash(Color.kYellow))));
        
        // driverController.getButton(Button.Y_TRIANGLE)
        //     .toggleOnTrue(Commands2023.balance());
    
        // Buttons for Field Relative and Speed
        // driverController.getButton(Button.A_CROSS)
        //     .onTrue(drivetrain.commands.enableFieldRelative());
        // driverController.getButton(Button.X_SQUARE)
        //     .onTrue(drivetrain.commands.disableFieldRelative());
        // driverController.getButton(Button.RIGHT_BUMPER)
        //     .onTrue(drivetrain.commands.increaseSpeed());
        // driverController.getButton(Button.LEFT_BUMPER)
        //     .onTrue(drivetrain.commands.decreaseSpeed());
        
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

        // SETUP LOGGING //
        Shuffleboard.getTab("Main")
            .addString("Logs", Logging::allLogs)
            .withWidget(BuiltInWidgets.kTextView);

    }

    
    // AUTO //
    public static final Auto auto = new Auto();

    @Override
    public Command getAuto() 
    {
        return auto.get();
    }
}