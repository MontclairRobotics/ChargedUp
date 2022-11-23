package frc.robot.constants;

import org.team555.frc.specs.Ratio;

import edu.wpi.first.math.util.Units;

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
}
