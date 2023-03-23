package org.team555.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.function.Supplier;

import org.team555.structure.DetectionType;
import org.team555.util.frc.commandrobot.ManagerBase;

public abstract class VisionSystem extends ManagerBase
{
    /**
     * Currently, this returns...
     * <ul>
     *    <li> AUTONOMOUS - APRIL TAG </li>
     *    <li> DISABLED   - TAPE </li>
     *    <li> ELSE       - NONE </li>
     * </ul>
     * 
     * @return
     */
    protected static DetectionType getDefaultInternal() 
    {
        if(DriverStation.isAutonomous())
            return DetectionType.APRIL_TAG;
        if(DriverStation.isDisabled())
            return DetectionType.TAPE;
        return DetectionType.NONE;
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

    public abstract Translation2d getAprilTagRobotSpace();

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
    public abstract boolean currentPipelineMatches(DetectionType type);

    /**
     * Gets the URL for the camera streams
     */
    public abstract String getCameraStreamURL();
}
