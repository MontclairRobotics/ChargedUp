package frc.robot.structure;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;

public class SwerveTrajectory
{
    public final Trajectory innerTrajectory;
    public final Rotation2d targetRotation;

    public SwerveTrajectory(Trajectory trajectory, Rotation2d targetRotation)
    {
        this.innerTrajectory = trajectory;
        this.targetRotation = targetRotation;
    }

    public SwerveTrajectory(Trajectory trajectory)
    {
        this(trajectory, new Rotation2d());
    }
}