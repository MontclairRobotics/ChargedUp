package frc.robot.constants;

import org.team555.frc.specs.Ratio;
import org.team555.units.Quantity;

import static org.team555.units.StandardUnits.*;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public final class Drivebase 
{
    private Drivebase() {}


    public static final double   TICKS_PER_ROTATION  = 42;
    public static final Ratio    GEAR_RATIO          = Ratio.of(8.14, 1);
    public static final Quantity WHEEL_DIAMETER      = Quantity.of(4, inch);
    public static final Quantity WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER.mul(Math.PI);

    public static final Quantity MOTOR_RPM = Quantity.of(11_000, rotation.per(minute));

    public static final Quantity CONVERSION_RATE_FROM_ROTATION
        = WHEEL_CIRCUMFERENCE.div(GEAR_RATIO.inToOut);
    public static final Quantity CONVERSION_RATE
        = CONVERSION_RATE_FROM_ROTATION.mul(TICKS_PER_ROTATION);
    
    public static final Quantity MOTOR_SPEED = CONVERSION_RATE_FROM_ROTATION.mul(MOTOR_RPM.as(rotation.per(second)));


    public static final int 
        ANGLE_P = 0,
        ANGLE_I = 0,
        ANGLE_D = 0
    ;

    /**
     * Rotator port first, driver port second
     * 
     * FL - 0
     * FR - 1
     * BL - 2
     * BR - 3
     */
    public static final int[][] PORTS = {
        {1, 2},
        {3, 4},
        {5, 6},
        {7, 8}
    };

    /**
     * Rotator type first, driver type second
     * 
     * FL - 0
     * FR - 1
     * BL - 2
     * BR - 3
     */
    public static final MotorType[][] TYPES = {
        {MotorType.kBrushless, MotorType.kBrushless},
        {MotorType.kBrushless, MotorType.kBrushless},
        {MotorType.kBrushless, MotorType.kBrushless},
        {MotorType.kBrushless, MotorType.kBrushless}
    };

    public static final Quantity DRIVE_SPEED = Quantity.of(15, mile.per(hour));
    public static final Quantity TURN_SPEED  = Quantity.of(180, degree.per(second));
}
