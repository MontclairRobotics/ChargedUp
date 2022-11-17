// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

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
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Navx;

import static frc.robot.constants.Controllers.*;

public class ChargedUp extends RobotContainer 
{
    public static final GameController driverController = GameController.from(DRIVER_CONTROLLER_TYPE,
            DRIVER_CONTROLLER_PORT);
    public static final GameController operatorController = GameController.from(OPERATOR_CONTROLLER_TYPE,
            OPERATOR_CONTROLLER_PORT);

    public static final Drivetrain drivetrain = new Drivetrain();
    public static final Navx navx = new Navx();

    @Override
    public void initialize() 
    {
        drivetrain.setDefaultCommand(Commands.run(() ->
            {
                if(!DriverStation.isTeleop())
                {
                    return;
                }

                drivetrain.driveFromInput(
                    driverController.getAxisValue(Axis.LEFT_X),
                    driverController.getAxisValue(Axis.RIGHT_X),
                    driverController.getAxisValue(Axis.RIGHT_Y), 
                    navx.getAngle()
                );
            }
        ));
    }
}