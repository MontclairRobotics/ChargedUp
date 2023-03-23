package org.team555.constants;

import com.pathplanner.lib.auto.PIDConstants;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import org.team555.math.Units555;
import org.team555.util.frc.SwerveModuleSpec;
import org.team555.util.frc.Tunable;

public class DriveConstants
{
    public static final boolean CHARGER_STATION_INCLINE_INVERT = true;
    public static final Tunable<Double> CHARGER_STATION_MUL = Tunable.of(0.2, "drive.charge_mul");
    public static final Tunable<Double> CHARGER_STATION_TILT_SPEED_THRESHOLD = Tunable.of(2.0, "drive.charge_tilt_speed_thresh");
    public static final Tunable<Double> CHARGER_STATION_AT_REST_DEBOUNCE_TIME = Tunable.of(0.5, "drive.charge_rest_debounce_time");
    
    public static final MotorType DRIVE_TYPE = MotorType.FALCON;
    public static final MotorType STEER_TYPE = MotorType.NEO;
    public static final double VISION_ESTIMATE_MAX_DISPLACEMENT = 1;

    public static final Tunable<Boolean> DRIVE_INVERT = Tunable.of(true, "drive.drive_invert");
    public static final Tunable<Boolean> STEER_INVERT = Tunable.of(false, "drive.steer_invert");

      ////////////////////////////////////////////
     // REBOOT THE ROBOT WHEN CHANGING OFFSETS //
    ////////////////////////////////////////////
    private static final SwerveModuleSpec FRONT_LEFT = 
        new SwerveModuleSpec(
            SdsModuleConfigurations.MK4I_L1, 
            DRIVE_TYPE, Ports.DRIVE_FL_PORT, DRIVE_INVERT.get(), 
            STEER_TYPE, Ports.STEER_FL_PORT, STEER_INVERT.get(),  
            Ports.CANCO_FL_PORT, 268.242188
        ); //fl
    private static final SwerveModuleSpec FRONT_RIGHT = 
        new SwerveModuleSpec(
            SdsModuleConfigurations.MK4I_L1, 
            DRIVE_TYPE, Ports.DRIVE_FR_PORT, DRIVE_INVERT.get(), 
            STEER_TYPE, Ports.STEER_FR_PORT, STEER_INVERT.get(), 
            Ports.CANCO_FR_PORT, 305.771484
        ); //fr
    private static final SwerveModuleSpec BACK_LEFT = 
        new SwerveModuleSpec(
            SdsModuleConfigurations.MK4I_L1, 
            DRIVE_TYPE, Ports.DRIVE_BL_PORT, DRIVE_INVERT.get(), 
            STEER_TYPE, Ports.STEER_BL_PORT, STEER_INVERT.get(), 
            Ports.CANCO_BL_PORT, 250.048828
        ); //bl
    private static final SwerveModuleSpec BACK_RIGHT =
        new SwerveModuleSpec(
            SdsModuleConfigurations.MK4I_L1, 
            DRIVE_TYPE, Ports.DRIVE_BR_PORT, DRIVE_INVERT.get(), 
            STEER_TYPE, Ports.STEER_BR_PORT, STEER_INVERT.get(), 
            Ports.CANCO_BR_PORT, 149.765625
        ); //br
    
    /**
     * Rotator port first, driver port second
     * 
     * FL - 0
     * FR - 1
     * BL - 2
     * BR - 3
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
        public static final Tunable<Double> KP = Tunable.of(3.1, "drive.pos.kp");
        public static final Tunable<Double> KI = Tunable.of(0.0, "drive.pos.ki");
        public static final Tunable<Double> KD = Tunable.of(0.25, "drive.pos.kd");

        public static PIDConstants consts()
        {
            return new PIDConstants(
                KP.get(), 
                KI.get(), 
                KD.get()
            );
        }
    }
    public static class ThetaPID
    {
        public static final Tunable<Double> KP = Tunable.of(0.55, "drive.theta.kp");
        public static final Tunable<Double> KI = Tunable.of(0.0, "drive.theta.ki");
        public static final Tunable<Double> KD = Tunable.of(0.06, "drive.theta.kd");
        
        public static PIDConstants consts()
        {
            return new PIDConstants(
                KP.get(),  
                KI.get(), 
                KD.get()
            );
        }

        
        public static final Tunable<Double> KAutoP = Tunable.of(3.5, "drive.auto.theta.kp");
        public static final Tunable<Double> KAutoI = Tunable.of(0.0, "drive.auto.theta.ki");
        public static final Tunable<Double> KAutoD = Tunable.of(0.22, "drive.auto.theta.kd");

        public static PIDConstants autoconsts()
        {
            return new PIDConstants(
                KAutoP.get(), 
                KAutoI.get(), 
                KAutoD.get()
            );
        }
    }


    public static final int MODULE_COUNT = MODULES.length;
    
    public static final double MAX_VOLTAGE_V = 12.0;

    public static final double MAX_SPEED_MPS             = Units.feetToMeters(11);
    public static final double MAX_ACCEL_MPS2            = Units555.miphpsToMpsps(10);
    public static final double MAX_TURN_SPEED_RAD_PER_S  = Math.PI * 2;
    public static final double MAX_TURN_ACCEL_RAD_PER_S2 = Units.degreesToRadians(360);

    public static final double WHEEL_BASE_W_M = Units.inchesToMeters(27);
    public static final double WHEEL_BASE_H_M = Units.inchesToMeters(30);

    private static Translation2d FLPosition = new Translation2d(+WHEEL_BASE_H_M/2, +WHEEL_BASE_W_M/2); //FL
    private static Translation2d FRPosition = new Translation2d(+WHEEL_BASE_H_M/2, -WHEEL_BASE_W_M/2); //FR
    private static Translation2d BLPosition = new Translation2d(-WHEEL_BASE_H_M/2, +WHEEL_BASE_W_M/2); //BL
    private static Translation2d BRPosition = new Translation2d(-WHEEL_BASE_H_M/2, -WHEEL_BASE_W_M/2); //BR
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

    public static final double[][] SPEEDS  = {{0.25, 0.25}, {0.5, 0.5}, {0.75, 0.75}, {1.0, 1.0}};  
    // 1st element is drive speed, 2nd is angular speed

    public static final String[] MODULE_NAMES = {"FL", "FR", "BR", "BL"};

    public static final Tunable<Double> TIME_FULL_STOP_SEC = Tunable.of(0.25, "drive.time_full_stop");
    public static double inputRateLimit() {return 1 / TIME_FULL_STOP_SEC.get();}

    public static final Translation2d DESIRED_APRIL_TAG_SCORE_POSE = new Translation2d(
        Units.feetToMeters(1 + 11.5 / 12),  // see game manual v7 page 26
        0
    );
}
