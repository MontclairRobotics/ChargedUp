package frc.robot.structure;

import java.util.HashMap;
import java.util.Map;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.SwerveAutoBuilder;

import org.team555.frc.command.AutoCommands;
import edu.wpi.first.wpilibj2.command.Commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.ChargedUp;
import frc.robot.structure.factories.PoseFactory;
import frc.robot.structure.helpers.Logging;
/**
 * Class which holds the trajectories which may be used by the robot
 */
public class Trajectories 
{
    /**
     * Returns the trajectory with the given name 
     * and with given maximum velocity and acceleration
     * @param name the name of the trajectory
     * @param maxVel the maximum velocity
     * @param maxAccel the maximum acceleration 
     * @return the trajectory
     */
    public static PathPlannerTrajectory get(String name, double maxVel, double maxAccel)
    {
        return PathPlanner.loadPath(name, maxVel, maxAccel);
    }

    /**
     * Returns a command that follows the trajectory with the given name
     * and with given maximum velocity and acceleration
     * @param name the name of the trajectory
     * @param maxVel the maximum velocity
     * @param maxAccel the maximum acceleration 
     * @return the command that follows the trajectory
     */
    public static Command follow(String name, double maxVel, double maxAccel)
    {
        return ChargedUp.drivetrain.commands.follow(get(name, maxVel, maxAccel));
    }

    /**
     * Creates an auto with a trajectory provided by 
     * {@link #get(String, double, double) get(name, maxVel, maxAccel)}
     * and with a hashmap containing the markers along the trajectory and their respective commands
     * @param name the name of the trajectory
     * @param maxVel the maximum velocity
     * @param maxAccel the maximum acceleration 
     * @param commands the hashmap containing the markers and their commands
     * @return an auto
     */
    public static Command auto(String name, double maxVel, double maxAccel, HashMap<String, Command> commands)
    {
        return ChargedUp.drivetrain.commands.auto(get(name, maxVel, maxAccel), commands);
    }
}
