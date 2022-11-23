// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.team555.frc.command.AutoCommands;
import org.team555.frc.command.Commands;
import org.team555.frc.command.commandrobot.RobotContainer;
import org.team555.frc.controllers.GameController;
import org.team555.frc.controllers.GameController.Axis;
import org.team555.frc.controllers.GameController.Button;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.AngularVelocityManager;

import static frc.robot.constants.Constants.*;

import com.kauailabs.navx.frc.AHRS;

public class ChargedUp extends RobotContainer 
{
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
        gyroscope.zeroYaw();

        // HANDLE DRIVING //
        drivetrain.setDefaultCommand(Commands.run(() ->
            {
                if(!DriverStation.isTeleop()) return;

                drivetrain.driveInput(
                    driverController.getAxisValue(Axis.LEFT_X),
                    driverController.getAxisValue(Axis.RIGHT_X),
                    driverController.getAxisValue(Axis.RIGHT_Y)
                );
            },
            drivetrain
        ));

        driverController.getButton(Button.A_CROSS) .toggleWhenActive(drivetrain.commands.enableFieldRelative());
        driverController.getButton(Button.X_SQUARE).toggleWhenActive(drivetrain.commands.disableFieldRelative());

        // HANDLE AUTO //
        AutoCommands.add("Main", () -> CommandGroupBase.sequence(
            Commands.print("Starting main auto"),
            drivetrain.commands.driveForTime(1, 3, 1, 0),
            Commands.print("Ending the main auto"),
            drivetrain.commands.drive(0, 0, 0)
        ));
    }
}