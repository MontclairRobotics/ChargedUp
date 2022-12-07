package frc.robot.structure.factories;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import frc.robot.constants.Constants.Drive;

public class TrajectoryFactory 
{
    public static Trajectory getTrajectory(double startVel, double endVel, Pose2d... waypoints)
    {
        var waypointsList = List.of(waypoints);

        var config = new TrajectoryConfig(Drive.MAX_SPEED_MPS, Drive.MAX_ACCEL_MPS2)
            .setKinematics(Drive.KINEMATICS)
            .setStartVelocity(startVel)
            .setEndVelocity(endVel);

        return TrajectoryGenerator.generateTrajectory(waypointsList, config);
    }

    public static Trajectory getTrajectory(Pose2d... waypoints)
    {
        return getTrajectory(0, 0, waypoints);
    }
}
