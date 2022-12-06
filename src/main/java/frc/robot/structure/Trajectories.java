package frc.robot.structure;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import frc.robot.constants.Constants.Drive;

public class Trajectories 
{
    public static Trajectory getTrajectory(Pose2d... waypoints)
    {
        var waypointsList = List.of(waypoints);
        return TrajectoryGenerator.generateTrajectory(waypointsList, new TrajectoryConfig(Drive.MAX_SPEED_MPS, Drive.MAX_ACCEL_MPS2));
    }
}
