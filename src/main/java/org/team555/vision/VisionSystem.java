package org.team555.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.function.Supplier;

import org.team555.structure.DetectionType;
import org.team555.util.frc.commandrobot.ManagerBase;

public abstract class VisionSystem extends ManagerBase
{
    protected static DetectionType getDefaultInternal() 
    {
        return DriverStation.isAutonomous() 
            ? DetectionType.APRIL_TAG 
            : DetectionType.NONE;
    }

    private DetectionType desiredDriveTarget = DetectionType.CUBE;

    public void setDesiredDriveTarget(DetectionType type)
    {
        desiredDriveTarget = type;
    }
    public DetectionType getDesiredDriveTarget()
    {
        return desiredDriveTarget;
    }
    public String getDesiredDriveTargetAsString()
    {
        if (desiredDriveTarget == null) return "none";
        return desiredDriveTarget.toString();
    }


    public void cycleDesiredDriveTarget()
    {
             if(desiredDriveTarget == DetectionType.CONE) desiredDriveTarget = DetectionType.CUBE;
        else if(desiredDriveTarget == DetectionType.CUBE) desiredDriveTarget = DetectionType.TAPE;
        else if(desiredDriveTarget == DetectionType.TAPE) desiredDriveTarget = DetectionType.CONE;
    }

    /**
     * updates the estimated pose of the robot
     * @param prev the previous estimated position of the robot
     */
    public abstract void updateEstimatedPose(Pose2d prev);

    /**
     * @return the estimated pose of the robot
     */
    public abstract Pose2d getEstimatedPose();

    /**
     * @return the estimated time the frame used to derive the robot pose was taken
     */
    public abstract double getTimestampSeconds();

    /**
     * Sets the Estimated Pose
     */
    public abstract void resetPose(Pose2d pose);

    /**
     * @return if it sees a target :)
     */
    public abstract boolean hasObject();

    /**
     * @return the {@link org.team555.structure.DetectionType Detection Type} that vision system has found
     * returns {@link org.team555.structure.DetectionType#NONE NONE} if not found
     */
    public abstract DetectionType getCurrentType();

    /**
     * @return the {@link org.team555.structure.DetectionType Detection Type} that vision system is searching for
     */
    public abstract DetectionType getTargetType();

    /**
     * Set pipeline so that it targets this kind of Field element {@link org.team555.structure.DetectionType DetectionType}
     */
    public abstract void setTargetType(DetectionType type);

    /**
     * @return the <b>horizontal</b> offset of the target from the center in degrees (angle range may change if using photon but who cares)
     */
    public abstract double getObjectAX();
    /**
     * @return the <b>vertical</b> offset of the target from the center in degrees (angle range may change if using photon but who cares)
     */
    public abstract double getObjectAY();

    /**
     * @return the int representing the pipline currently being used
     */
    public abstract double getPipeline();

    /**
     * Changes the current pipeline based on the value
     * @param value number which corresponds to the pipeline
     */
    public abstract void setPipeline(double value);

    /**
     * Check whether the vision system's current pipeline is applicable for detecting the inputted {@link DetectionType detection type}
     * @param type
     * @return
     */
    public abstract boolean currentPipelineMatchesDetection(Supplier<DetectionType> type);

    /**
     * Gets the URL for the camera streams
     */
    public abstract String getCameraStreamURL();
}
