package frc.robot.structure.factories;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.constraint.TrajectoryConstraint;
import frc.robot.constants.Constants.Drive;
import frc.robot.structure.SwerveTrajectory;

public class SwerveTrajectoryFactory 
{
    /**
     * Generates a trajectory from the end velocity, a target rotation, and given waypoints.
     * This method uses clamped cubic splines -- a method in which the initial pose, final pose, 
     * and interior waypoints are provided. The headings are automatically determined at the 
     * interior points to ensure continuous curvature.
     * 
     * @param endVel velocity it should have at the endpoint
     * @param targetRotation rotation it should have at the endpoint
     * @param waypoints array of Pose2ds which hold the points it should hit along its course
     * <p><i>NOTE: that the first waypoint is the initial pose, and the last is the final pose</i>
     * @return SwerveTrajectory object
     */
    public static SwerveTrajectory getTrajectory(double endVel, Rotation2d targetRotation, Pose2d... waypoints)
    {
        var config = new TrajectoryConfig(Drive.MAX_SPEED_MPS, Drive.MAX_ACCEL_MPS2)
            .setKinematics(Drive.KINEMATICS)
            .setEndVelocity(endVel);

        return new SwerveTrajectory(
            TrajectoryGenerator.generateTrajectory(
                List.of(waypoints),
                config
            ), 
            targetRotation
        );
    }

    /**
     * Generates a trajectory from a target rotation, and given waypoints.
     * This method uses clamped cubic splines -- a method in which the initial pose, final pose, 
     * and interior waypoints are provided. The headings are automatically determined at the 
     * interior points to ensure continuous curvature.
     * <p>
     * The ending velocity is set to 0
     * <p>
     * @param targetRotation rotation it should have at the endpoint
     * @param waypoints array of Pose2ds which hold the points it should hit along its course
     * <p><i>NOTE: that the first waypoint is the initial pose, and the last is the final pose</i>
     * @return SwerveTrajectory object
     */
    public static SwerveTrajectory getTrajectory(Rotation2d targetRotation, Pose2d... waypoints)
    {
        return getTrajectory(0, targetRotation, waypoints);
    }
}
