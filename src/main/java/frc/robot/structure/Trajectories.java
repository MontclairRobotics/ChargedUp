package frc.robot.structure;

import java.util.HashMap;
import java.util.Map;

import org.team555.frc.command.AutoCommands;
import org.team555.frc.command.Commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.ChargedUp;
import frc.robot.structure.factories.SwerveTrajectoryFactory;
import frc.robot.structure.helpers.Logging;

public class Trajectories 
{
    private static Map<String, SwerveTrajectory> values = new HashMap<String, SwerveTrajectory>();

    public static void add(String name, SwerveTrajectory trajectory)
    {
        values.put(name, trajectory);
    }

    public static void add(String name, Rotation2d targetRotation, Pose2d... waypoints)
    {
        add(name, SwerveTrajectoryFactory.getTrajectory(targetRotation, waypoints));
    }

    public static void add(String name, double endVel, Rotation2d targetRotation, Pose2d... waypoints)
    {
        add(name, SwerveTrajectoryFactory.getTrajectory(endVel, targetRotation, waypoints));
    }

    public static SwerveTrajectory get(String name)
    {
        return values.get(name);
    }

    public static Command relativeFollow(String name)
    {
        return CommandGroupBase.sequence
        (
            Commands.instant(() -> Logging.info(name)),
            ChargedUp.drivetrain.commands.relativeTrajectory(get(name))
        );
    }
    public static Command absoluteFollow(String name)
    {
        return ChargedUp.drivetrain.commands.absoluteTrajectory(get(name));
    }

    public static void makeRelativeAuto(String name)
    {
        AutoCommands.add("Trajectory::rel " + name, () -> relativeFollow(name));
    }
    public static void makeAbsoluteAuto(String name)
    {
        AutoCommands.add("Trajectory::abs " + name, () -> absoluteFollow(name));
    }
}
