package frc.robot.constants;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;

public class PhotonConstants
{
    private PhotonConstants() {}
    //TODO: Get actual camera specs

    // Constants such as camera and target height stored. Change per robot and goal!
    public static final double CAMERA_HEIGHT_METERS = Units.inchesToMeters(24);
    public static final double TARGET_HEIGHT_METERS = Units.feetToMeters(5);
    
    // Angle between horizontal and the camera.
    public static final double CAMERA_PITCH_RADIANS = Units.degreesToRadians(0);

    // How far from the target we want to be
    public static final double GOAL_RANGE_METERS = Units.feetToMeters(3);

    public static final String CAMERA_NAME = "photonvision";

    //Distance between center of robot and camera position
    public static final Transform3d ROBOT_TO_CAM = new Transform3d(); 

    //The URL that shuffleboard gets video from for PhotonVision
    public static final String PHOTON_URL = "http://10.55.5.11:1182/stream.mjpg";

}