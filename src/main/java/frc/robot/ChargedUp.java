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
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryParameterizer;
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

import java.util.HashMap;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

public class ChargedUp extends RobotContainer 
{
    public static final Field2d field = new Field2d();
    public static final Map<String, Trajectory> trajectories = new HashMap<String, Trajectory>();

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
        Shuffleboard.getTab("Main")
            .add("Field", field)
            .withSize(4, 2)
            .withPosition(0, 2);

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

        initTrajectories();

        for(var e : trajectories.entrySet())
        {
            AutoCommands.add(
                "(Trajectory) " + e.getKey(), 
                () -> drivetrain.commands.trajectory(e.getValue())
            );
        }

        Shuffleboard.getTab("Main")
            .add("Auto Commands", AutoCommands.chooser())
            .withSize(2, 1)
            .withPosition(5, 0);
    }

    private void addTrajectory(String name, Trajectory trajectory)
    {
        trajectories.put(name, trajectory);
    }

    private void initTrajectories()
    {
        addTrajectory("Test Line", TrajectoryFactory.getTrajectory(
            PoseFactory.meter(0, 0, 0),
            PoseFactory.meter(1, 0, 0)
        ));

        addTrajectory("Test Diagonal", TrajectoryFactory.getTrajectory(
            PoseFactory.meter(0, 0, 0),
            PoseFactory.meter(2, 2, 0)
        ));

        addTrajectory("Test Line+Turn Long", TrajectoryFactory.getTrajectory(
            PoseFactory.meter(0, 0, 0),
            PoseFactory.meter(5, 5, 180)
        ));

        addTrajectory("Test Line+Turn", TrajectoryFactory.getTrajectory(
            PoseFactory.meter(0, 0, 0),
            PoseFactory.meter(1, 1, 180)
        ));

        addTrajectory("Test Line+Turn & Return", TrajectoryFactory.getTrajectory(
            PoseFactory.meter(0, 0, 0),
            PoseFactory.meter(1, 1, 180),
            PoseFactory.meter(0, 0, 360)
        ));
    }
}