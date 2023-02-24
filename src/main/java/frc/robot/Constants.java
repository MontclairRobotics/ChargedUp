package frc.robot;

import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.framework.GameController;
import frc.robot.inputs.JoystickAdjuster;
import frc.robot.structure.helpers.Units555;
import frc.robot.structure.swerve.SwerveModuleSpec;

public final class Constants 
{
    private Constants() {}

    public static class Pneu
    {
        public static final int COMPRESSOR_PORT = 63;
        public static final PneumaticsModuleType MODULE_TYPE = PneumaticsModuleType.REVPH;
        public static final int GRABBER_SOLENOID_PORT = 0;
        public static final int GRABBER_PSI_SOLENOID_PORT = 15;
    }

    public static class Drive
    {
        public static final MotorType DRIVE_TYPE = MotorType.FALCON;
        public static final MotorType STEER_TYPE = MotorType.NEO;

          ////////////////////////////////////////////
         // REBOOT THE ROBOT WHEN CHANGING OFFSETS //
        ////////////////////////////////////////////
        private static final SwerveModuleSpec FRONT_LEFT = 
            new SwerveModuleSpec(
                SdsModuleConfigurations.MK4I_L1, 
                DRIVE_TYPE, 10, false, 
                STEER_TYPE, 7, false,  
                12, 268.242188
            ); //fl
        private static final SwerveModuleSpec FRONT_RIGHT = 
            new SwerveModuleSpec(
                SdsModuleConfigurations.MK4I_L1, 
                DRIVE_TYPE, 1, false, 
                STEER_TYPE, 18, false, 
                13, 305.771484
            ); //fr
        private static final SwerveModuleSpec BACK_LEFT = 
            new SwerveModuleSpec(
                SdsModuleConfigurations.MK4I_L1, 
                DRIVE_TYPE, 3, false, 
                STEER_TYPE, 28, false, 
                11, 250.048828
            ); //bl
        private static final SwerveModuleSpec BACK_RIGHT =
            new SwerveModuleSpec(
                SdsModuleConfigurations.MK4I_L1, 
                DRIVE_TYPE, 41, false, 
                STEER_TYPE,  29, false, 
                4, 149.765625
            ); //br
        
        /**
         * Rotator port first, driver port second
         * 
         * FL - 0
         * FR - 1
         * BL - 2
         * BR - 3
         * 
         * TODO: why do we need to subtract 90deg here?
         */
        public static final SwerveModuleSpec[] MODULES = 
        {
            FRONT_LEFT, 
            FRONT_RIGHT,
            BACK_LEFT,
            BACK_RIGHT
        };

        public static class PosPID
        {
            public static final double KP = 0.1;
            public static final double KI = 0;
            public static final double KD = 0;

            public static final PIDConstants KConsts = new PIDConstants(KP, KI, KD);
        }
        public static class ThetaPID
        {
            public static final double KP = 0.1;
            public static final double KI = 0;
            public static final double KD = 0;
            
            public static final PIDConstants KConsts = new PIDConstants(KP, KI, KD);
        }


        public static final int MODULE_COUNT = MODULES.length;
        
        public static final double MAX_VOLTAGE_V = 12.0;

        public static final double MAX_SPEED_MPS             = Units.feetToMeters(11);
        public static final double MAX_ACCEL_MPS2            = Units555.miphpsToMpsps(10);
        public static final double MAX_TURN_SPEED_RAD_PER_S  = Math.PI * 2;
        public static final double MAX_TURN_ACCEL_RAD_PER_S2 = Units.degreesToRadians(360);

        public static final double WHEEL_BASE_W_M = Units.inchesToMeters(27); //TODO: CONFIRM WITH JOSH
        public static final double WHEEL_BASE_H_M = Units.inchesToMeters(30);

        private static Translation2d FLPosition = new Translation2d( Drive.WHEEL_BASE_H_M/2,  Drive.WHEEL_BASE_W_M/2); //FL
        private static Translation2d FRPosition = new Translation2d( Drive.WHEEL_BASE_H_M/2, -Drive.WHEEL_BASE_W_M/2); //FR
        private static Translation2d BLPosition = new Translation2d(-Drive.WHEEL_BASE_H_M/2,  Drive.WHEEL_BASE_W_M/2); //BL
        private static Translation2d BRPosition = new Translation2d(-Drive.WHEEL_BASE_H_M/2, -Drive.WHEEL_BASE_W_M/2); //BR
        public static final Translation2d[] MOD_POSITIONS = {
            FLPosition,
            FRPosition,
            BLPosition,
            BRPosition
        };
        public static final SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            FLPosition, //FL
            FRPosition, //FR
            BLPosition, //BL
            BRPosition  //BR
        );

        public static final double[][] speeds  = {{0.25, 0.25}, {0.5, 0.5}, {0.75, 0.75}, {1.0, 1.0}};  
        // 1st element is drive speed, 2nd is angular speed
    }
    public static class Auto 
    {
        public static final double MAX_VEL = 10;
        public static final double MAX_ACC = 4; // these numbers have origin inside my head
    }
    public static class Robot 
    {
        public static final boolean CHARGER_STATION_INCLINE_INVERT = false;
        
        public static final double GRABBER_HEIGHT_NO_OBJECT = Units.feetToMeters(19.0/12);
        public static final double GRABBER_OBJECT_DANGLE = Units.feetToMeters(5.0/12);

        public static final double GRABBER_HEIGHT_WITH_OBJECT = GRABBER_HEIGHT_NO_OBJECT + GRABBER_OBJECT_DANGLE;

        public static class Elevator 
        {
            // GEAR RATIO  :=  40 in : 1 out
            // PULLY SIZE  :=  4 in.

            public static final double GEAR_RATIO_OUT_OVER_IN = 1.0 / 40.0;
            public static final double SPROCKET_DIAMETER = Units.inchesToMeters(8);

            public static final double ENCODER_CONVERSION_FACTOR = GEAR_RATIO_OUT_OVER_IN * SPROCKET_DIAMETER * Math.PI;

            public static final int MOTOR_PORT = 5;
            public static final boolean INVERTED = false;

            //TODO: Get actual digital inputs
            public static final int TOP_LIMIT_SWITCH = 0;
            public static final int BOTTOM_LIMIT_SWITCH = 1;
            public static final int START_LIMIT_SWITCH = 2;

            public static final double SPEED = 0.1;  
            //TODO: Tweak these values
            
            public static final double ELEVATOR_MIN_TO_FLOOR = Units.feetToMeters(6.0/12);

            public static final double MAX_HEIGHT = Units.feetToMeters(78.0/12);
            
            public static final double HIGH_HEIGHT = Units.feetToMeters(3.0 + 10.0/12) - ELEVATOR_MIN_TO_FLOOR + GRABBER_HEIGHT_NO_OBJECT;
            public static final double MID_HEIGHT  = Units.feetToMeters(2.0 + 10.0/12) - ELEVATOR_MIN_TO_FLOOR + GRABBER_HEIGHT_NO_OBJECT;

            public static final double BUFFER_SPACE_TO_INTAKE = Units.feetToMeters(12.0/12);
            public static final double MIN_HEIGHT = 0;

            public static final double DEADBAND = 0.05;
            public static final JoystickAdjuster JOY_ADJUSTER = new JoystickAdjuster(DEADBAND, 2.2);   

            public static final double 
                KP = 1,
                KI = 1,
                KD = 1
            ;

            public static final PIDController updown() 
            {
                return new PIDController(KP, KI, KD);
            }
        }

        public static class Shwooper 
        {
            public static final int LEFT_MOTOR_PORT = 91;
            public static final int RIGHT_MOTOR_PORT = 90;
            public static final int CENTER_MOTOR_PORT = 92;

            public static final int PNEU_PORT = 2;

            public static final boolean SOLENOID_DEFAULT_STATE = false;

            public static final double SPEED = 0.5;

            public static final boolean LEFT_INVERSION = false;
            public static final boolean RIGHT_INVERSION = false;
            public static final boolean CENTER_INVERSION = false;

            public static final double SUCK_TIME_FOR_PICKUP_AUTO = 1;
        }  

        public static class ColorSensing
        {
            public static final Color CONE_COLOR = Color.kYellow;
            public static final Color CUBE_COLOR = Color.kPurple;

            public static final double COLOR_CONFIDENCE = 0.7;
        }

        public static class Grabber 
        {
            public static final I2C.Port COLOR_SENSOR_PORT = I2C.Port.kMXP;

            public static final boolean SOLENOID_DEFAULT_STATE = false;
			public static final boolean PSI_SOLENOID_DEFAULT_STATE = false;
        }

        public static class LED 
        {
            public static final int PWM_PORT = 9;
        }


        public static class Limelight 
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
        }

        public static class PhotonVision 
        {
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
        }

        public static class Stinger 
        {
            public static final int MOTOR_PORT = 17;

            //TODO: Get actual digital inputs
            public static final int OUTER_LIMIT_SWITCH = 3;
            public static final int INNER_LIMIT_SWITCH = 4;

            public static final double
                IN_OUT_KP = 1,
                IN_OUT_KI = 0,
                IN_OUT_KD = 0
            ;
            
            public static final PIDController inout()
            {
                return new PIDController(IN_OUT_KP, IN_OUT_KI, IN_OUT_KD);
            }

            public static final double MID_LENGTH_MUL = 0.65;
            public static final double HIGH_LENGTH_MUL = 0.95;

            public static final double MIN_LENGTH = Units.feetToMeters(10.0/12); // 10 inches
            public static final double MAX_LENGTH = Units.feetToMeters(60.0/12); // 60 inches

            public static final double EXT_LENGTH = MAX_LENGTH - MIN_LENGTH;

            public static final double SPEED = 1;
            public static final double DEADBAND = 0.05;

            public static final JoystickAdjuster JOY_ADJUSTER = new JoystickAdjuster(DEADBAND, 2);



            public static final double SEGMENT_COUNT = 8.5;
            public static final double SEGMENT_LENGTH = Units.inchesToMeters(9.139);

            public static final double SEGMENT_COUNT_SQ = SEGMENT_COUNT*SEGMENT_COUNT;
            public static final double SEGMENT_LENGTH_SQ = SEGMENT_LENGTH*SEGMENT_LENGTH;

            public static final double LEAD_SCREW_FACTOR = 0.5;

            public static double leadDistToStngDist(final double leadDist)
            {
                /**
                 * S := 8.5    //The number of segments
                 * W := 9.139  //The length of one segment
                 * T := input  //The distance the lead screw has travelled
                 * 
                 * output := S * sqrt(W^2 + T^2)
                 */

                return SEGMENT_COUNT * Math.sqrt(SEGMENT_LENGTH_SQ - Math.pow(leadDist + MIN_LENGTH, 2));
            }

            public static double stngDistToLeadDist(final double elevDist) 
            {
                /**
                 * S := 8.5    //The number of segments
                 * W := 9.139  //The length of one segment
                 * T := input  //The distance the elevator has travelled
                 * 
                 * output := sqrt(W^2 - (T / W)^2)
                 */

                return Math.sqrt(SEGMENT_LENGTH_SQ - Math.pow(elevDist - MIN_LENGTH, 2) / SEGMENT_COUNT_SQ);
            }
        } 
    }
    
    public static class ControlScheme
    {
        public static final GameController.Type OPERATOR_CONTROLLER_TYPE = GameController.Type.PS4;
        public static final GameController.Type DRIVER_CONTROLLER_TYPE   = GameController.Type.PS4;

        public static final int OPERATOR_CONTROLLER_PORT = 0;
        public static final int DRIVER_CONTROLLER_PORT   = 1;

        public static final double DRIVE_DEADBAND = 0.05;
        
        public static final JoystickAdjuster DRIVE_ADJUSTER = new JoystickAdjuster(DRIVE_DEADBAND, 2.2);
        public static final JoystickAdjuster TURN_ADJUSTER  = new JoystickAdjuster(DRIVE_DEADBAND, 1.5);
    }

    public static class Field
    {
        public static final double CHARGE_ANGLE_RANGE_DEG = 15;
        public static final double CHARGE_ANGLE_DEADBAND = 2.5;
    }
}
