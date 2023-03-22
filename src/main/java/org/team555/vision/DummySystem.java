package org.team555.vision;

import edu.wpi.first.math.geometry.Pose2d;

import java.util.function.Supplier;

import org.team555.structure.DetectionType;

public class DummySystem extends VisionSystem
{
    @Override
    public void always() {}

    @Override
    public void updateEstimatedPose(Pose2d prev) {}

    @Override
    public Pose2d getEstimatedPose() 
    {
        return new Pose2d();
    }

    @Override
    public double getTimestampSeconds() 
    {
        return -10;
    }

    @Override
    public void resetPose(Pose2d pose) {}

    @Override
    public boolean hasObject() 
    {
        return false;
    }

    @Override
    public DetectionType getCurrentType() 
    {
        return DetectionType.DEFAULT;
    }

    @Override
    public DetectionType getTargetType() 
    {
        return DetectionType.DEFAULT;
    }

    @Override
    public void setTargetType(DetectionType type) {}

    @Override
    public double getObjectAX()
    {
        return 0;
    }

    @Override
    public double getObjectAY() 
    {
        return 0;
    }

    @Override
    public double getPipeline() 
    {
        return 0;
    }

    @Override
    public void setPipeline(double value) {}

    @Override
    public String getCameraStreamURL() 
    {
        return "<dummy>";
    }

    @Override
    public boolean currentPipelineMatches(DetectionType type) 
    {
        return true;
    }

    @Override
    public Pose2d getAprilTagRobotSpace() {
        return new Pose2d();
    }
}
