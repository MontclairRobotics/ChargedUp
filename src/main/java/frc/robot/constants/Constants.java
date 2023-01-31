package frc.robot.constants;

import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper.GearRatio;

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
import frc.robot.structure.swerve.SwerveModuleMk4iSpec;
import frc.robot.structure.swerve.SwerveModuleSpec;

public final class Constants 
{

    private Constants() {}

    public static class Pneu
    {
        public static final int COMPRESSOR_PORT = 0;
        public static final PneumaticsModuleType MODULE_TYPE = PneumaticsModuleType.REVPH;
        public static final int GRABBER_SOLENOID_PORT = 0;
    }

    public static class Drive
    {
        private static final SwerveModuleSpec FRONT_LEFT = 
            new SwerveModuleMk4iSpec(GearRatio.L1,  29, 5,  12,  358.651157 - 90);
        private static final SwerveModuleSpec FRONT_RIGHT = 
            new SwerveModuleMk4iSpec(GearRatio.L1,  30, 28, 10,  087.078116 - 90);
        private static final SwerveModuleSpec BACK_LEFT = 
            new SwerveModuleMk4iSpec(GearRatio.L1,  3,  14, 13,  219.871863 - 90);
        private static final SwerveModuleSpec BACK_RIGHT =
            new SwerveModuleMk4iSpec(GearRatio.L1,  7,  4,  11,  250.479320 - 90);
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
        public static final double MAX_ACC = 4; // these numbers have origin inside my ass
    }
    public static class Robot 
    {
        public static final double ROBOT_MASS_KG      = Units.lbsToKilograms(60);
        public static final double ROBOT_MOMENT_KG_M2 = 1.0/12.0 * ROBOT_MASS_KG * Math.pow((Drive.WHEEL_BASE_H_M*1.1),2) * 2;

        public static final double TREAD_STATIC_FRICTION_COEF  = 0.60;
        public static final double TREAD_KINETIC_FRICTION_COEF = 0.45;

        public static final double NORMAL_FORCE_ON_MODULE_N = Units.lbsToKilograms(45) * 9.81 / 4;

        public static final Rotation2d NAVX_OFFSET = Rotation2d.fromDegrees(0);

        //Elevator Constants
        public static final double ELEVATOR_SPEED = 0.1;  //TODO: Tweak these values
        public static final int ELEVATOR_MOTOR_PORT = 4;
        public static final boolean ELEVATOR_INVERTED = false;

        public static final double ELEVATOR_MAX_HEIGHT = Units.feetToMeters(73.0/12);  // 73 inches
        public static final double ELEVATOR_MID_HEIGHT = 0.5;
        public static final double ELEVATOR_HIGH_HEIGHT = 1;

        public static final double ELEVATOR_UP_DOWN_CONVERSION_FACTOR = -1;

        public static final double ELEVATOR_DEADBAND = 0.05;

        public static final JoystickAdjuster ELEVATOR_JOY_ADJUSTER = new JoystickAdjuster(ELEVATOR_DEADBAND, 2.2);
        
        
        public static final double 
            ELEVATOR_UP_DOWN_KP = 1,
            ELEVATOR_UP_DOWN_KI = 1,
            ELEVATOR_UP_DOWN_KD = 1
        ;

        public static final PIDController elevatorUpDown() {
            return new PIDController(ELEVATOR_UP_DOWN_KP, ELEVATOR_UP_DOWN_KI, ELEVATOR_UP_DOWN_KD);
        }

        

        // Shwooper Constants
        public static final int INTAKE_PORT = 0;
        public static final int INTAKE_PNEU_PORT = 2;
        public static final double INTAKE_SPEED = 0.5;
        public static final boolean INTAKE_INVERSION = false;
        public static final boolean SHWOOPER_SOLENOID_DEFAULT_STATE = false;
        public static final double INTAKE_SUCK_TIME = 1;

        //Grabber Constants
        public static final boolean GRABBER_SOLENOID_DEFAULT_STATE = false;

        //LED Constants
        public static final int LED_PWM_PORT = 9;
        
        // Stinger Constants :)
        public static final int STINGER_PORT = 0;
        public static final double
            STINGER_IN_OUT_KP = 1,
            STINGER_IN_OUT_KI = 0,
            STINGER_IN_OUT_KD = 0
            ;
        public static final double STINGER_IN_OUT_CONVERSION_FACTOR = -1;

        public static final double STINGER_MID_LENGTH = 0.5;
        public static final double STINGER_HIGH_LENGTH = 1;

        public static final double STINGER_EXTENSION_LENGTH = Units.feetToMeters(52.0/12); // 52 inches
        
        public static PIDController stingerInOut()
        {
            return new PIDController(STINGER_IN_OUT_KP,STINGER_IN_OUT_KI,STINGER_IN_OUT_KD);
        }

        public static final double STINGER_SPEED = 1;

        public static final double STINGER_DEADBAND = 0.05;
        public static final JoystickAdjuster STINGER_JOYSTICK_ADJUSTER = new JoystickAdjuster(STINGER_DEADBAND, 2);

        //Arm Constants
        public static final double ARM_SPEED = 0.5;
        public static final int ARM_UP_DOWN_PORT = 4;
        public static final int ARM_IN_OUT_PORT = -1;
        public static final double ARM_IN_OUT_SPEED = 0.5;

        public static final double 
            ARM_IN_OUT_KP = 1,
            ARM_IN_OUT_KI = 0,
            ARM_IN_OUT_KD = 0
        ;
        
        public static final double ARM_IN_OUT_POSITION_CONVERSION_FACTOR = -1;

        public static PIDController armInOut()
        {
            return new PIDController(ARM_IN_OUT_KP, ARM_IN_OUT_KI, ARM_IN_OUT_KD);
        }

        public static final double 
            ARM_UP_DOWN_KP = 1,
            ARM_UP_DOWN_KI = 0,
            ARM_UP_DOWN_KD = 0
        ;

        public static final double ARM_UP_DOWN_POSITION_CONVERSION_FACTOR = -1;

        public static PIDController armUpDown(){//I AM RIGHT
            PIDController pid = new PIDController(ARM_UP_DOWN_KP, ARM_UP_DOWN_KI, ARM_UP_DOWN_KD);
            pid.enableContinuousInput(-Math.PI, Math.PI);
            return pid;
        }

        //arm commands angles and lengths 
        public static final double ARM_RETURN_POSITION = 0;
        //in theory the length extension should be the same for pegs and shelves
        public static final double ARM_MID_LENGTH = 0.5; //to be changed when arm is constructed (currently in m)
        public static final double ARM_HIGH_LENGTH = 1; // ^^^^
        
        public static final double ARM_MID_PEG_ANGLE = Rotation2d.fromDegrees(90).getRadians();
        public static final double ARM_HIGH_PEG_ANGLE = Rotation2d.fromDegrees(120).getRadians();
        public static final double ARM_MID_SHELF_ANGLE = Rotation2d.fromDegrees(95).getRadians();
        public static final double ARM_HIGH_SHELF_ANGLE = Rotation2d.fromDegrees(125).getRadians();

    }

    public static class ControlScheme
    {
        public static final GameController.Type OPERATOR_CONTROLLER_TYPE = GameController.Type.PS4;
        public static final GameController.Type DRIVER_CONTROLLER_TYPE   = GameController.Type.PS4;

        public static final int OPERATOR_CONTROLLER_PORT = 0;
        public static final int DRIVER_CONTROLLER_PORT   = 1;

        public static final double DEADBAND = 0.05;
        
        public static final JoystickAdjuster DRIVE_ADJUSTER = new JoystickAdjuster(DEADBAND, 2.2);
        public static final JoystickAdjuster TURN_ADJUSTER  = new JoystickAdjuster(DEADBAND, 1.5);
    }
}
