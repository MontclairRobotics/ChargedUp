package frc.robot.constants;

import edu.wpi.first.math.util.Units;

public class LimelightConstants
{
    public static final int LED_MODE_CURRENT = 0;
    public static final int LED_MODE_FORCE_OFF = 1;
    public static final int LED_MODE_FORCE_BLINK = 2;
    public static final int LED_MODE_FORCE_ON = 3;
    public static final int CAM_MODE_VISION_PROCESSOR = 0;
    public static final int CAM_MODE_DRIVER_CAMERA = 1;
    public static final int STREAM_STANDARD = 0;
    public static final int STREAM_PIP_MAIN = 1;
    public static final int STREAM_PIP_SECONDARY = 2;
    public static final int SNAPSHOT_MODE_RESET = 0;
    public static final int SNAPSHOT_TAKE_EXACTLY_ONE = 1;
    public static final double[] CROP_VALUES = {-1, 1, -1, 1};

    //Camera pose offsets (x, y, z)
    public static final double FORWARD_OFFSET = Units.inchesToMeters( 11.625);
    public static final double SIDE_OFFSET = Units.inchesToMeters(3.57);
    public static final double UP_OFFSET = Units.inchesToMeters(28 + (1.0/8));

    public static final String LIMELIGHT_URL = "http://10.5.55.11:5800";
}
