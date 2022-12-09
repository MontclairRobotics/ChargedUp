package frc.robot.structure;

import java.util.HashMap;
import java.util.Map;

import com.pathplanner.lib.PathPlanner;

import org.team555.frc.command.AutoCommands;
import org.team555.frc.command.Commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.ChargedUp;
import frc.robot.structure.factories.PoseFactory;
import frc.robot.structure.factories.SwerveTrajectoryFactory;
import frc.robot.structure.helpers.Logging;
/**
 * Class which holds the trajectories which may be used by the robot
 */
public class Trajectories 
{
    private static Map<String, SwerveTrajectory> values = new HashMap<String, SwerveTrajectory>();

    /**
     * store the trajectory and its corresponding name
     * @param name
     * @param trajectory
     */
    public static void add(String name, SwerveTrajectory trajectory)
    {
        values.put(name, trajectory);
    }

    /**
     * store a <code>SwerveTrajectory</code> and its corresponding name
     * <p>
     * Uses {@link SwerveTrajectoryFactory} to Generate a trajectory from the end velocity, a target rotation, and given waypoints. 
     * This method uses clamped cubic splines -- a method in which the initial pose, final pose, and interior waypoints are provided.
     * The headings are automatically determined at the interior points to ensure continuous curvature.
     * <p>
     * The ending velocity is set to 0
     * 
     * @param name
     * @param targetRotation rotation the trajectory should have at endpoint 
     * @param waypoints array of Pose2ds which hold the points the trajectory should hit along its course
     */
    public static void add(String name, Rotation2d targetRotation, Pose2d... waypoints)
    {
        add(name, SwerveTrajectoryFactory.getTrajectory(targetRotation, waypoints));
    }

    /**
     * store a <code>SwerveTrajectory</code> and its corresponding name
     * <p>
     * Uses {@link SwerveTrajectoryFactory} to Generate a trajectory from the end velocity, a target rotation, and given waypoints. 
     * This method uses clamped cubic splines -- a method in which the initial pose, final pose, and interior waypoints are provided.
     * The headings are automatically determined at the interior points to ensure continuous curvature.
     * 
     * @param name
     * @param endVel velocity the trajectory should have at the endpoint
     * @param targetRotation rotation the trajectory should have at endpoint 
     * @param waypoints array of Pose2ds which hold the points the trajectory should hit along its course
     */
    public static void add(String name, double endVel, Rotation2d targetRotation, Pose2d... waypoints)
    {
        add(name, SwerveTrajectoryFactory.getTrajectory(endVel, targetRotation, waypoints));
    }

    public static void addPathWeaver(String name, String path)
    {
        Logging.fatal("Unimplemented method Trajectories.addPathWeaver(String name, String path)");
        add(name, new Rotation2d(0), PoseFactory.feet(0, 0));
    }

    public static SwerveTrajectory get(String name)
    {
        return values.get(name);
    }

    public static Command follow(String name)
    {
        return ChargedUp.drivetrain.commands.trajectory(get(name));
    }

    /**
     * add an AutoCommand entitled <code>name</code> and makes it so that the specified 
     * autocommand follows the trajectory associated with its name
     * 
     * @param name
     * @return The name of the added auto command.
     */
    public static String makeFollowAuto(String name)
    {
        var aname = "Trajectory " + name;
        AutoCommands.add(aname, () -> follow(name));
        return aname;
    }
}
