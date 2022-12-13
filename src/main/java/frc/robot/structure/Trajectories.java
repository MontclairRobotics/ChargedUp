package frc.robot.structure;

import java.util.HashMap;
import java.util.Map;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;

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
    public static PathPlannerTrajectory get(String name, double maxVel, double maxAccel)
    {
        return PathPlanner.loadPath(name, maxVel, maxAccel);
    }

    public static Command follow(String name, double maxVel, double maxAccel)
    {
        return ChargedUp.drivetrain.commands.trajectory(get(name, maxVel, maxAccel));
    }
    
    public static String makeFollowAuto(String name, double maxVel, double maxAccel)
    {
        var aname = "Trajectory " + name;
        AutoCommands.add(aname, () -> follow(name, maxVel, maxAccel));
        return aname;
    }
}
