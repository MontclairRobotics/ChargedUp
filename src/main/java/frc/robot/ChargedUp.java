// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.team555.frc.command.AutoCommands;
import edu.wpi.first.wpilibj2.command.Commands;
import org.team555.frc.command.commandrobot.RobotContainer;
import org.team555.frc.controllers.GameController;
import org.team555.frc.controllers.GameController.Axis;
import org.team555.frc.controllers.GameController.Button;
import org.team555.frc.controllers.GameController.DPad;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryParameterizer;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.CompressorConfigType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.subsystems.LED;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Shwooper;
import frc.robot.subsystems.Stinger;
import frc.robot.subsystems.Grabber;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.Trajectories;
import frc.robot.structure.animation.RainbowAnimation;
import frc.robot.structure.factories.HashMaps;
import frc.robot.structure.factories.PoseFactory;
import frc.robot.structure.helpers.Logging;
// import frc.robot.subsystems.AngularVelocityManager;
import frc.robot.subsystems.Arm;

import static frc.robot.constants.Constants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.kauailabs.navx.frc.AHRS;

public class ChargedUp extends RobotContainer 
{
    public static final Field2d field = new Field2d();
    // public static final Compressor pneu = new Compressor(Pneu.COMPRESSOR_PORT, Pneu.MODULE_TYPE);

    // CONTROLLERS //
    public static final GameController driverController = GameController.from(
        ControlScheme.DRIVER_CONTROLLER_TYPE,
        ControlScheme.DRIVER_CONTROLLER_PORT);
    public static final GameController operatorController = GameController.from(
        ControlScheme.OPERATOR_CONTROLLER_TYPE,
        ControlScheme.OPERATOR_CONTROLLER_PORT);

    // COMPONENTS //
    public static final AHRS gyroscope = new AHRS();

    // SUBSYSTEMS //
    public static final LED        led        = new LED();
    public static final Drivetrain drivetrain = new Drivetrain();
    public static final Elevator   elevator   = new Elevator();
    public static final Arm        arm        = new Arm();
    public static final Shwooper   shwooper   = new Shwooper();
    public static final Grabber    grabber    = new Grabber();
    public static final Stinger    stinger    = new Stinger();

    // MANAGERS //
    // public static final AngularVelocityManager angularVelocityManager = new AngularVelocityManager();

    // INITIALIZER //
    @Override public void initialize() 
    {
        driverController.getButton(Button.X_SQUARE)
            .toggleOnTrue(Commands.runOnce(() -> {
                led.add(new RainbowAnimation(2));
                Logging.info("bruh");
            }));
        //Shuffleboard.getTab("Main")
        //  .add("Field", field)
        //    .withSize(4, 2)
        //    .withPosition(0, 2);
      
        // HANDLE DRIVING //
        drivetrain.setDefaultCommand(Commands.run(() ->
            {
                if(!DriverStation.isTeleop())
                {
                    drivetrain.drive(0,0,0);
                    return;
                }

                drivetrain.driveInput(
                    JoystickInput.getRight(driverController, true,  false),
                    JoystickInput.getLeft (driverController, false, false)
                );
            },
            drivetrain
        ));
            /** 
             * Buttons for Field Relative and Speed
             */
        driverController.getButton(Button.A_CROSS)
            .toggleOnTrue(drivetrain.commands.enableFieldRelative());
        driverController.getButton(Button.X_SQUARE)
            .toggleOnTrue(drivetrain.commands.disableFieldRelative());
        driverController.getButton(Button.RIGHT_BUMPER)
            .toggleOnTrue(drivetrain.commands.increaseSpeed());
        driverController.getButton(Button.LEFT_BUMPER)
            .toggleOnTrue(drivetrain.commands.decreaseSpeed());
            /**
             * Button to Zero NavX
             */ 
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
            Robot.Stinger.STINGER_JOYSTICK_ADJUSTER.adjustX(right);
            stinger.setSpeed(right.getX());
        }));

        // Grabber
        operatorController.getButton(Button.A_CROSS)
            .toggleOnTrue(Commands2023.toggleGrabber());

        // Schwooper 
        /**
         * button to suck
         */ 
        operatorController.getAxis(Axis.LEFT_TRIGGER)
            .whenGreaterThan(0.5)
            .onTrue (Commands2023.shwooperSuck())
            .onFalse(Commands2023.stopShwooper());
        /**
         * button to blow
         */ 
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
        //operatorController.getButton(Button.X_SQUARE)
        //    .whenActive(() -> elevator.elevate()) 
        //    .whenInactive(() -> elevator.stop());

        //operatorController.getButton(Button.A_CROSS)
        //    .whenActive(() -> elevator.delevate()) 
        //    .whenInactive(() -> elevator.stop());

        //using dylan's code base: v complicated
        elevator.setDefaultCommand(Commands.run(() -> {
            JoystickInput left = JoystickInput.getLeft(
                operatorController, 
                false, 
                false);
            Robot.Elevator.ELEVATOR_JOY_ADJUSTER.adjustY(left);
            elevator.setSpeed(left.getY());
        }));

        // HANDLE AUTO //
        //AutoCommands.add("Main", () -> CommandGroupBase.sequence(
        //    Commands.print("Starting main auto"),
        //    drivetrain.commands.driveForTime(2, 1, 0, 1),
        //    Commands.print("Ending the main auto"),
        //    drivetrain.commands.driveInstant(0, 0, 0)
        //));
        initAuto();

        // Shuffleboard.getTab("Main")
        //    .add("Auto Commands", AutoCommands.chooser())
        //    .withSize(2, 1)
        //    .withPosition(5, 0);
    }
    // Test line
    private void initAuto()
    {
        Command cmd = drivetrain.commands.auto(
            Trajectories.get(
                "Test Line", 
                Drive.MAX_SPEED_MPS / 2, 
                Drive.MAX_ACCEL_MPS2
            ),
            HashMaps.of()
        );
        
        // AutoCommands.add("Test Line", () -> cmd);.
        AutoCommands.add("Main", () -> Commands.runOnce( () -> {}));
        AutoCommands.setDefaultCommand("Main");
    }
}