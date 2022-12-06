package frc.robot.structure.factories;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class PoseFactory 
{
    public static Pose2d of(double x_meter, double y_meter, double angle_deg)
    {
        return new Pose2d(x_meter, y_meter, Rotation2d.fromDegrees(angle_deg));
    }
}
