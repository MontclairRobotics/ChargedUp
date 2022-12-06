// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.team555.frc.command.AutoCommands;
import org.team555.frc.command.Commands;
import org.team555.frc.command.commandrobot.RobotContainer;
import org.team555.frc.controllers.GameController;
import org.team555.frc.controllers.GameController.Button;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.inputs.JoystickInput;
import frc.robot.structure.factories.PoseFactory;
import frc.robot.structure.factories.TrajectoryFactory;
import frc.robot.subsystems.AngularVelocityManager;

import static frc.robot.constants.Constants.*;

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

    // SUBSYSTEMS //
    public static final Drivetrain drivetrain = new Drivetrain();

    // MANAGERS //
    public static final AngularVelocityManager angularVelocityManager = new AngularVelocityManager();

    // INITIALIZER //
    @Override public void initialize() 
    {
        Shuffleboard.getTab("Field")
            .add(field)
            .withSize(4, 2)
            .withPosition(0, 0);

        gyroscope.zeroYaw();

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

        driverController.getButton(Button.A_CROSS)
            .toggleWhenActive(drivetrain.commands.enableFieldRelative());
        driverController.getButton(Button.X_SQUARE)
            .toggleWhenActive(drivetrain.commands.disableFieldRelative());

        // HANDLE AUTO //
        AutoCommands.add("Main", () -> CommandGroupBase.sequence(
            Commands.print("Starting main auto"),
            drivetrain.commands.driveForTime(2, 1, 0, 1),
            Commands.print("Ending the main auto"),
            drivetrain.commands.driveInstant(0, 0, 0)
        ));
        
        AutoCommands.add("Trajectory Test", () -> drivetrain.commands.followTrajectory(
            TrajectoryFactory.getTrajectory(
                PoseFactory.of(0, 0, 0),
                PoseFactory.of(2, 0, 0)
            )
        ));

        Shuffleboard.getTab("Main")
            .add("Auto Commands", AutoCommands.chooser())
            .withSize(2, 1)
            .withPosition(5, 0);
    }
}