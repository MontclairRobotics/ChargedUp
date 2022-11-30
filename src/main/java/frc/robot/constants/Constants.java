package frc.robot.constants;

import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper.GearRatio;

import org.team555.frc.controllers.GameController;

import edu.wpi.first.math.util.Units;
import frc.robot.Units555;
import frc.robot.structure.SwerveModuleMk4iSpec;
import frc.robot.structure.SwerveModuleSpec;

public final class Constants 
{
    private Constants() {}

    public static class Drive
    {
        /**
         * Rotator port first, driver port second
         * 
         * FL - 0
         * FR - 1
         * BL - 2
         * BR - 3
         */
        public static final SwerveModuleSpec[] MODULES = {
            new SwerveModuleMk4iSpec(GearRatio.L1,  1,  28, 10,  270.0),
            new SwerveModuleMk4iSpec(GearRatio.L1,  7,  4,  11,  271.6),
            new SwerveModuleMk4iSpec(GearRatio.L1,  29, 5,  12,  270.0),
            new SwerveModuleMk4iSpec(GearRatio.L1,  3,  14, 13,  273.2),
        };

        public static final int MODULE_COUNT = MODULES.length;

        
        public static final double MAX_VOLTAGE_V = 12.0;

        public static final double MAX_SPEED_MPS            = Units555.mphToMps(10);
        public static final double MAX_TURN_SPEED_RAD_PER_S = Math.PI * 2;

        public static final double WHEEL_BASE_W_M = Units.inchesToMeters(30);
        public static final double WHEEL_BASE_H_M = Units.inchesToMeters(30);
    }

    public static class Robot 
    {
        public static final double ROBOT_MASS_KG      = Units.lbsToKilograms(60);
        public static final double ROBOT_MOMENT_KG_M2 = 1.0/12.0 * ROBOT_MASS_KG * Math.pow((Drive.WHEEL_BASE_H_M*1.1),2) * 2;

        public static final double TREAD_STATIC_FRICTION_COEF  = 0.60;
        public static final double TREAD_KINETIC_FRICTION_COEF = 0.45;

        public static final double NORMAL_FORCE_ON_MODULE_N = Units.lbsToKilograms(45) * 9.81 / 4;
    }

    public static class ControlScheme
    {
        public static final GameController.Type OPERATOR_CONTROLLER_TYPE = GameController.Type.PS4;
        public static final GameController.Type DRIVER_CONTROLLER_TYPE   = GameController.Type.PS4;

        public static final int OPERATOR_CONTROLLER_PORT = 1;
        public static final int DRIVER_CONTROLLER_PORT   = 0;

        public static final double DEADBAND    = 0.05;
        public static final double INPUT_POWER = 2.2;
    }
}
