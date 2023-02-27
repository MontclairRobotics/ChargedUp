package frc.robot.vision;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.structure.DetectionType;
import frc.robot.util.frc.commandrobot.Manager;

public interface VisionSystem extends Manager
{
    /**
     * updates the estimated pose of the robot
     * @param prev the previous estimated position of the robot
     */
    void updateEstimatedPose(Pose2d prev);

    /**
     * @return the estimated pose of the robot
     */
    Pose2d getEstimatedPose();

    /**
     * @return the estimated time the frame used to derive the robot pose was taken
     */
    double getTimestampSeconds();

    /**
     * Sets the Estimated Pose
     */
    void resetPose(Pose2d pose);

    /**
     * @return if it sees a target :)
     */
    boolean hasObject();

    /**
     * @return the {@link frc.robot.structure.DetectionType Detection Type} that vision system has found
     * returns {@link frc.robot.structure.DetectionType#NONE NONE} if not found
     */
    DetectionType getCurrentType();

    /**
     * @return the {@link frc.robot.structure.DetectionType Detection Type} that vision system is searching for
     */
    DetectionType getTargetType();

    /**
     * Set pipeline so that it targets this kind of Field element {@link frc.robot.structure.DetectionType DetectionType}
     */
    void setTargetType(DetectionType type);

    /**
     * @return the <b>horizontal</b> offset of the target from the center in degrees (angle range may change if using photon but who cares)
     */
    double getObjectAX();
    /**
     * @return the <b>vertical</b> offset of the target from the center in degrees (angle range may change if using photon but who cares)
     */
    double getObjectAY();

    /**
     * @return the int representing the pipline currently being used
     */
    double getPipeline();

    /**
     * Changes the current pipeline based on the value
     * @param value number which corresponds to the pipeline
     */
    void setPipeline(double value);

    /**
     * Gets the URL for the camera streams
     */
    String getCameraStreamURL();
}
