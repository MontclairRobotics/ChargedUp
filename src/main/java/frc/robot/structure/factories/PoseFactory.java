package frc.robot.structure.factories;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;

/**
 * A class which implements methods to more simply create Pose2D Objects
 * 
*/
public class PoseFactory 
{
    /**
     * creates a Pose2d with components X and Y with a 0 degree rotation
     * @param x_meter
     * @param y_meter
     * @return Pose2d object
     */
    public static Pose2d meter(double x_meter, double y_meter)
    {
        return new Pose2d(
            x_meter, y_meter, 
            Rotation2d.fromDegrees(0)
        );
    }

    /**
     * creates a Pose2d with components X and Y in meters, and rotation in degrees
     * @param x_meter
     * @param y_meter
     * @param degrees
     * @return Pose2d object
     */
    public static Pose2d meter(double x_meter, double y_meter, double degrees)
    {
        return new Pose2d(
            x_meter, y_meter, 
            Rotation2d.fromDegrees(degrees)
        );
    }

    /**
     * Creates a Pose2d with components X and Y in *feet* and rotation of 0
     * @param x_feet
     * @param y_feet
     * @return Pose2d object
     */
    public static Pose2d feet(double x_feet, double y_feet)
    {
        return new Pose2d(
            Units.feetToMeters(x_feet), 
            Units.feetToMeters(y_feet), 
            Rotation2d.fromDegrees(0)
        );
    }

    /**
     * creates a Pose2d with components X and Y in feet, and rotation in degrees
     * @param x_feet
     * @param y_feet
     * @param degrees
     * @return Pose2d object
     */
    public static Pose2d feet(double x_feet, double y_feet, double degrees)
    {
        return new Pose2d(
            Units.feetToMeters(x_feet), 
            Units.feetToMeters(y_feet), 
            Rotation2d.fromDegrees(degrees)
        );
    }
}
