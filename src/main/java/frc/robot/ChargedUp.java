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
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.constants.Drivebase;
import frc.robot.constants.SwerveConstants555;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Navx;
import frc.swervelib.SwerveSubsystem;

import static frc.robot.constants.Controllers.*;

public class ChargedUp extends RobotContainer 
{
    public static final GameController driverController = GameController.from(DRIVER_CONTROLLER_TYPE,
            DRIVER_CONTROLLER_PORT);
    public static final GameController operatorController = GameController.from(OPERATOR_CONTROLLER_TYPE,
            OPERATOR_CONTROLLER_PORT);

            
    public static final Navx gyro = new Navx();
    public static final Drivetrain drivetrain = new Drivetrain();

    @Override
    public void initialize() 
    {
        // HANDLE FIELDS //
        SmartDashboard.putData("field", drivetrain.dt.getField());

        // HANDLE DRIVING //
        drivetrain.setDefaultCommand(Commands.run(() ->
            {
                if(!DriverStation.isTeleop()) return;

                drivetrain.driveFromInput(
                    driverController.getAxisValue(Axis.LEFT_X),
                    driverController.getAxisValue(Axis.RIGHT_X),
                    driverController.getAxisValue(Axis.RIGHT_Y)
                );
            },
            drivetrain
        ));

        // HANDLE AUTO //
        AutoCommands.add("Main", () -> CommandGroupBase.sequence(
            Commands.print("Starting main auto"),
            drivetrain.commands.driveForTime(3, Math.PI, 3, 0),
            Commands.print("Ending the main auto"),
            drivetrain.commands.drive(0, 0, 0)
        ));
    }
}