package frc.robot.constants;

import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper.GearRatio;

import java.util.ArrayList;

import org.team555.frc.controllers.GameController;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
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
        public static final SwerveModuleSpec[] MODULES = {
            new SwerveModuleMk4iSpec(GearRatio.L1,  30, 28, 10,  087.078116 - 90),
            new SwerveModuleMk4iSpec(GearRatio.L1,  7,  4,  11,  250.479320 - 90),
            new SwerveModuleMk4iSpec(GearRatio.L1,  29, 5,  12,  358.651157 - 90),
            new SwerveModuleMk4iSpec(GearRatio.L1,  3,  14, 13,  219.871863 - 90),
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

        public static final double WHEEL_BASE_W_M = Units.inchesToMeters(30);
        public static final double WHEEL_BASE_H_M = Units.inchesToMeters(30);

        public static final SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            new Translation2d(-Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2), //FL
            new Translation2d( Drive.WHEEL_BASE_W_M/2,  Drive.WHEEL_BASE_H_M/2), //FR
            new Translation2d(-Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2), //BL
            new Translation2d( Drive.WHEEL_BASE_W_M/2, -Drive.WHEEL_BASE_H_M/2)  //BR
        );
        public static final double[][] speeds  = {{0.25, 0.25}, {0.5, 0.5}, {0.75, 0.75}, {1.0, 1.0}};  
        // 1st element is drive speed, 2nd is angular speed
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

        // Shwooper Constants
        public static final int INTAKE_PORT = 0;
        public static final double INTAKE_SPEED = 0.5;
        public static final boolean INTAKE_INVERSION = false;
        public static final boolean SHWOOPER_SOLENOID_DEFAULT_STATE = false;

        //Grabber Constants
        public static final boolean GRABBER_SOLENOID_DEFAULT_STATE = false;

        
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

        public static final int OPERATOR_CONTROLLER_PORT = 1;
        public static final int DRIVER_CONTROLLER_PORT   = 0;

        public static final double DEADBAND = 0.05;
        
        public static final JoystickAdjuster DRIVE_ADJUSTER = new JoystickAdjuster(DEADBAND, 2.2);
        public static final JoystickAdjuster TURN_ADJUSTER  = new JoystickAdjuster(DEADBAND, 1.5);
    }
}
