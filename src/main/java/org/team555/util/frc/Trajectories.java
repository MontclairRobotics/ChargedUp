package org.team555.util.frc;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.server.PathPlannerServer;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ProxyCommand;

import org.team555.ChargedUp;
/**
 * Class which holds the trajectories which may be used by the robot
 */
public class Trajectories 
{
    public static final String TEST_PREFIX = "test.";

    /**
     * Get the directory where PathPlanner stores its paths.
     */
    public static Path pathPlannerDir()
    {
        return Filesystem.getDeployDirectory().toPath().resolve("pathplanner");
    }

    /**
     * Get whether or not the given path exists.
     */
    public static boolean exists(String name)
    {
        Path pathFile = pathPlannerDir().resolve(name+".path");
        return pathFile.toFile().exists();
    }

    /**
     * Get an enumeration of all the test trajectories in this project.
     * @return The enumeration as a set
     */
    public static Map<String, CommandBase> getAllTests()
    {
        Path ppDir = pathPlannerDir();

        Map<String, CommandBase> result = new HashMap<>();

        for(File pathFile : ppDir.toFile().listFiles())
        {
            Path path = pathFile.toPath();
            String filename = path.getFileName().toString();
            String name = filename.substring(0, filename.lastIndexOf('.'));

            if(name.startsWith(TEST_PREFIX))
            {
                result.put(name, new ProxyCommand(() -> ChargedUp.drivetrain.commands.testTrajectory(name)).withName("Test Trajectory " + name));
            }
        }

        return result;
    }

    /**
     * Returns the trajectory with the given name 
     * and with given maximum velocity and acceleration
     * @param name the name of the trajectory
     * @param constraints the constraints of the path
     * @return the trajectory
     */
    public static PathPlannerTrajectory get(String name, PathConstraints constraints)
    {
        return PathPlannerTrajectory.transformTrajectoryForAlliance(
            PathPlanner.loadPath(name, constraints), 
            DriverStation.getAlliance()
        );
    }
}
