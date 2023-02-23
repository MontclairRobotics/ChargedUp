package frc.robot.structure;

import edu.wpi.first.math.geometry.Pose2d;

public interface VisionProvider 
{
    void updateEstimatedPose(Pose2d prev);

    Pose2d getEstimatedPose();
    double getTimestampSeconds();

    double getObjectAX();
    double getObjectAY();

    double getPipeline();
    void setPipeline(double value);
}
