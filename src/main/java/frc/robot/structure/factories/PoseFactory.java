package frc.robot.structure.factories;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;

public class PoseFactory 
{
    public static Pose2d meter(double x_meter, double y_meter, double angle_deg)
    {
        return new Pose2d(
            x_meter, y_meter, 
            Rotation2d.fromDegrees(angle_deg)
        );
    }

    public static Pose2d feet(double x_feet, double y_feet, double angle_deg)
    {
        return new Pose2d(
            Units.feetToMeters(x_feet), 
            Units.feetToMeters(y_feet), 
            Rotation2d.fromDegrees(angle_deg)
        );
    }
}
