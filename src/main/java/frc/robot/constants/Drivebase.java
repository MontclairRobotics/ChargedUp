package frc.robot.constants;

import org.team555.frc.specs.Ratio;

import edu.wpi.first.math.util.Units;
import frc.robot.Units555;
import frc.robot.structure.SwerveModuleMk4iSpec;
import frc.robot.structure.SwerveModuleSpec;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.swervelib.Mk4ModuleConfiguration;
import frc.swervelib.Mk4iSwerveModuleHelper.GearRatio;

public final class Drivebase 
{
    private Drivebase() {}


    public static final double TICKS_PER_ROTATION        = 42;
    public static final Ratio GEAR_RATIO                 = Ratio.of(8.14, 1);
    public static final double WHEEL_DIAMETER_METER      = Units.inchesToMeters(4);
    public static final double WHEEL_CIRCUMFERENCE_METER = WHEEL_DIAMETER_METER * Math.PI;

    public static final double MOTOR_RPM = 11_000;

    public static final double CONVERSION_RATE_FROM_ROTATION
        = WHEEL_CIRCUMFERENCE_METER / GEAR_RATIO.inToOut;
    public static final double CONVERSION_RATE
        = CONVERSION_RATE_FROM_ROTATION * TICKS_PER_ROTATION;
    
    public static final double MOTOR_SPEED = CONVERSION_RATE_FROM_ROTATION * MOTOR_RPM;


    /**
     * Rotator port first, driver port second
     * 
     * FL - 0
     * FR - 1
     * BL - 2
     * BR - 3
     */
    public static final SwerveModuleMk4iSpec[] MODULE_SPECS = {
        new SwerveModuleMk4iSpec(GearRatio.L1,  0,  1,  2,  0.0,  "FL"),
        new SwerveModuleMk4iSpec(GearRatio.L1,  3,  4,  5,  0.0,  "FR"),
        new SwerveModuleMk4iSpec(GearRatio.L1,  6,  7,  8,  0.0,  "BL"),
        new SwerveModuleMk4iSpec(GearRatio.L1,  9,  10, 11, 0.0,  "BR"),
    };

    
    public static final double MAX_VOLTAGE_V = 12.0;

    public static final double MAX_SPEED_MPS            = Units555.mphToMps(10);
    public static final double MAX_STRAFE_SPEED_MPS     = Units555.mphToMps(10);
    public static final double MAX_TURN_SPEED_RAD_PER_S = Math.PI;

    public static final double WHEEL_BASE_W_M = Units.inchesToMeters(30);
    public static final double WHEEL_BASE_H_M = Units.inchesToMeters(30);
}
