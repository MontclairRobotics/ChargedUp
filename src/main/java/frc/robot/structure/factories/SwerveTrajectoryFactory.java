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
    public static SwerveTrajectory getTrajectory(double endVel, Rotation2d targetRotation, Pose2d... waypoints)
    {
        var config = new TrajectoryConfig(Drive.MAX_SPEED_MPS, Drive.MAX_ACCEL_MPS2)
            .setKinematics(Drive.KINEMATICS)
            .setEndVelocity(endVel);

        var translations = new ArrayList<Translation2d>();
        for(int i = 1; i < waypoints.length - 1; i++)
        {
            translations.add(waypoints[i].getTranslation());
        }

        return new SwerveTrajectory(
            TrajectoryGenerator.generateTrajectory(
                waypoints[0], 
                translations,
                waypoints[waypoints.length-1],
                config
            ), 
            targetRotation
        );
    }

    public static SwerveTrajectory getTrajectory(Rotation2d targetRotation, Pose2d... waypoints)
    {
        return getTrajectory(0, targetRotation, waypoints);
    }
}
