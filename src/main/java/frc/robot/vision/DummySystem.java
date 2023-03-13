package frc.robot.vision;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.structure.DetectionType;

public class DummySystem extends VisionSystem
{
    @Override
    public void always() {}

    @Override
    public void updateEstimatedPose(Pose2d prev) {}

    @Override
    public Pose2d getEstimatedPose() {
        return new Pose2d();
    }

    @Override
    public double getTimestampSeconds() {
        return -10;
    }

    @Override
    public void resetPose(Pose2d pose) {}

    @Override
    public boolean hasObject() {
        return false;
    }

    @Override
    public DetectionType getCurrentType() {
        return DEFAULT_DETECTION;
    }

    @Override
    public DetectionType getTargetType() {
        return DEFAULT_DETECTION;
    }

    @Override
    public void setTargetType(DetectionType type) {}

    @Override
    public double getObjectAX() {
        return 0;
    }

    @Override
    public double getObjectAY() {
        return 0;
    }

    @Override
    public double getPipeline() {
        return 0;
    }

    @Override
    public void setPipeline(double value) {}

    @Override
    public String getCameraStreamURL() {
        return "<dummy>";
    }
    
}
