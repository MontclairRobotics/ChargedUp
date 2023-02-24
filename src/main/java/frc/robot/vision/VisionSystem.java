package frc.robot.vision;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.util.frc.commandrobot.Manager;

public interface VisionSystem extends Manager
{
    // boolean supportsAllInOneDetection();

    void updateEstimatedPose(Pose2d prev);

    Pose2d getEstimatedPose();
    double getTimestampSeconds();

    boolean hasObject();

    DetectionType getCurrentType();
    DetectionType getTargetType();
    void setTargetType(DetectionType type);

    double getObjectAX();
    double getObjectAY();

    double getPipeline();
    void setPipeline(double value);
}
