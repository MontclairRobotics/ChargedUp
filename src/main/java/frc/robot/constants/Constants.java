package frc.robot.constants;

import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.MechanicalConfiguration;
import com.swervedrivespecialties.swervelib.MkSwerveModuleBuilder;
import com.swervedrivespecialties.swervelib.MotorType;

import java.text.FieldPosition;
import java.util.ArrayList;

import org.team555.frc.controllers.GameController;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
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
    }

    public static class Drive
    {
        public static final MechanicalConfiguration CONFIGURATION = new MechanicalConfiguration(
            Drivebase.WHEEL_DIAMETER_METER, 
            8.14, 
            false, 
            8.14, 
            false
        );

        private static final MotorType DRIVE_TYPE = MotorType.FALCON;
        private static final MotorType STEER_TYPE = MotorType.NEO;

        private static final SwerveModuleSpec FRONT_LEFT = 
            new SwerveModuleSpec(CONFIGURATION, DRIVE_TYPE, 10, STEER_TYPE, 7,  12,  358.651157 - 90);
        private static final SwerveModuleSpec FRONT_RIGHT = 
            new SwerveModuleSpec(CONFIGURATION, DRIVE_TYPE, 1, STEER_TYPE, 18, 13,  087.078116 - 90);
        private static final SwerveModuleSpec BACK_LEFT = 
            new SwerveModuleSpec(CONFIGURATION, DRIVE_TYPE,  3, STEER_TYPE, 28, 11,  219.871863 - 90);
        private static final SwerveModuleSpec BACK_RIGHT =
            new SwerveModuleSpec(CONFIGURATION, DRIVE_TYPE,  41, STEER_TYPE,  29, 4,  250.479320 - 90);
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
            public static final double KP = 1;
            public static final double KI = 0;
            public static final double KD = 0;

            public static final PIDConstants KConsts = new PIDConstants(KP, KI, KD);
        }
        public static class ThetaPID
        {
            public static final double KP = 1;
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

        private static Translation2d FLPosition = new Translation2d(-Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2); //FL
        private static Translation2d FRPosition = new Translation2d( Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2); //FR
        private static Translation2d BLPosition = new Translation2d(-Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2); //BL
        private static Translation2d BRPosition = new Translation2d( Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2); //BR
        public static final SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            FLPosition, //FL
            FRPosition, //FR
            BLPosition, //BL
            BRPosition  //BR
        );

        public static final SwerveModulePosition FL_SWERVE_POS = new SwerveModulePosition(FLPosition.getNorm(), FLPosition.getAngle());
        public static final SwerveModulePosition FR_SWERVE_POS = new SwerveModulePosition(FRPosition.getNorm(), FRPosition.getAngle());
        public static final SwerveModulePosition BL_SWERVE_POS = new SwerveModulePosition(BLPosition.getNorm(), BLPosition.getAngle());
        public static final SwerveModulePosition BR_SWERVE_POS = new SwerveModulePosition(BRPosition.getNorm(), BRPosition.getAngle());

        public static final SwerveModulePosition[] POSITIONS = 
        {
            FL_SWERVE_POS,
            FR_SWERVE_POS,
            BL_SWERVE_POS,
            BR_SWERVE_POS
        };

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
        public static final double ROBOT_MASS_KG      = Units.lbsToKilograms(60);
        public static final double ROBOT_MOMENT_KG_M2 = 1.0/12.0 * ROBOT_MASS_KG * Math.pow((Drive.WHEEL_BASE_H_M*1.1),2) * 2;

        public static final double TREAD_STATIC_FRICTION_COEF  = 0.60;
        public static final double TREAD_KINETIC_FRICTION_COEF = 0.45;

        public static final double NORMAL_FORCE_ON_MODULE_N = Units.lbsToKilograms(45) * 9.81 / 4;

        public static final Rotation2d NAVX_OFFSET = Rotation2d.fromDegrees(0);

        public static final boolean CHARGER_STATION_INCLINE_INVERT = false;

        public static class Elevator 
        {
            public static final int MOTOR_PORT = 4;
            public static final boolean INVERTED = false;

            public static final double SPEED = 0.1;  //TODO: Tweak these values

            public static final double MAX_HEIGHT = Units.feetToMeters(73.0/12);  // 73 inches
            public static final double MID_HEIGHT = 0.5;
            public static final double HIGH_HEIGHT = 1;

            public static final double UP_DOWN_CONVERSION_FACTOR = -1;

            public static final double DEADBAND = 0.05;
            public static final JoystickAdjuster JOY_ADJUSTER = new JoystickAdjuster(DEADBAND, 2.2);   

            public static final double BUFFER_SPACE_TO_INTAKE = 0.75; //in meters

            public static final double 
                UP_DOWN_KP = 1,
                UP_DOWN_KI = 1,
                UP_DOWN_KD = 1
            ;

            public static final PIDController updown() 
            {
                return new PIDController(UP_DOWN_KP, UP_DOWN_KI, UP_DOWN_KD);
            }
        }

        public static class Shwooper 
        {
            public static final int PORT = 0;
            public static final int PNEU_PORT = 2;

            public static final boolean SOLENOID_DEFAULT_STATE = false;

            public static final double SPEED = 0.5;
            public static final boolean INVERSION = false;

            public static final double SUCK_TIME_FOR_PICKUP_AUTO = 1;
        }  

        public static class Grabber 
        {
            public static final boolean SOLENOID_DEFAULT_STATE = false;
        }

        public static class LED 
        {
            public static final int PWM_PORT = 9;
        }


        public static class Limelight {
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

        public static class Stinger 
        {
            public static final int MOTOR_PORT = 0;

            public static final double
                IN_OUT_KP = 1,
                IN_OUT_KI = 0,
                IN_OUT_KD = 0
            ;
            
            public static final PIDController inout()
            {
                return new PIDController(IN_OUT_KP, IN_OUT_KI, IN_OUT_KD);
            }

            public static final double IN_OUT_CONVERSION_FACTOR = -1;

            public static final double MID_LENGTH_MUL = 0.5;
            public static final double HIGH_LENGTH_MUL = 1;

            public static final double EXT_LENGTH = Units.feetToMeters(52.0/12); // 52 inches

            public static final double SPEED = 1;
            public static final double DEADBAND = 0.05;

            public static final JoystickAdjuster JOY_ADJUSTER = new JoystickAdjuster(DEADBAND, 2);
        }

        public static class Arm 
        {
            public static final double UP_DOWN_SPEED = 0.5;
            public static final double IN_OUT_SPEED = 0.5;
            
            public static final int UP_DOWN_PORT = 4;
            public static final int IN_OUT_PORT = -1;

            public static final double 
                IN_OUT_KP = 1,
                IN_OUT_KI = 0,
                IN_OUT_KD = 0
            ;

            public static final double IN_OUT_POSITION_CONVERSION_FACTOR = -1;

            public static final PIDController inout()
            {
                return new PIDController(IN_OUT_KP, IN_OUT_KI, IN_OUT_KD);
            }

            public static final double 
                UP_DOWN_KP = 1,
                UP_DOWN_KI = 0,
                UP_DOWN_KD = 0
            ;

            public static final double UP_DOWN_POSITION_CONVERSION_FACTOR = -1;

            public static PIDController updown()
            {
                PIDController pid = new PIDController(UP_DOWN_KP, UP_DOWN_KI, UP_DOWN_KD);
                pid.enableContinuousInput(-Math.PI, Math.PI);
                return pid;
            }

            //arm commands angles and lengths 
            public static final double RETURN_POSITION = 0;

            //in theory the length extension should be the same for pegs and shelves
            public static final double MID_LENGTH = 0.5; //to be changed when arm is constructed (currently in m)
            public static final double HIGH_LENGTH = 1; // ^^^^

            public static final double MID_PEG_ANGLE = Rotation2d.fromDegrees(90).getRadians();
            public static final double MID_SHELF_ANGLE = Rotation2d.fromDegrees(95).getRadians();

            public static final double HIGH_PEG_ANGLE = Rotation2d.fromDegrees(120).getRadians();
            public static final double HIGH_SHELF_ANGLE = Rotation2d.fromDegrees(125).getRadians();
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
        public static final double CHARGE_ANGLE_RANGE_DEG = 15; //TODO: what should this be???
        public static final double CHARGE_ANGLE_DEADBAND = 2.5;
    }

    public static final String DYLAN = "DYLAN";
}
