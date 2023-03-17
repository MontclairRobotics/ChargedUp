package org.team555.constants;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import org.team555.inputs.JoystickAdjuster;

public class StingerConstants
{
    //TODO: Get actual digital inputs
    public static final int OUTER_LIMIT_SWITCH = 3;
    public static final int INNER_LIMIT_SWITCH = 4;

    public static final double
        IN_OUT_KP = 0.1,
        IN_OUT_KI = 0.1,
        IN_OUT_KD = 0
    ;

    //Pneu stuffs
    public static final double PNEU_TIME = 1.1;
    
    
    public static final PIDController inout()
    {
        PIDController p = new PIDController(IN_OUT_KP, IN_OUT_KI, IN_OUT_KD);
        // p.setTolerance(0.01);
        return p;
    }

    public static final double MID_LENGTH_MUL = 0.65;
    public static final double HIGH_LENGTH_MUL = 0.95;

    public static final double MID_LENGTH = Units.inchesToMeters(32);
    public static final double LOW_LENGTH = Units.inchesToMeters(10);

    public static final double MIN_LENGTH = Units.inchesToMeters(10.0); // 10 inches
    public static final double MAX_LENGTH = Units.inchesToMeters(60.0); // 60 inches

    public static final double EXT_LENGTH = MAX_LENGTH - MIN_LENGTH;

    public static final double SPEED = 1;
    public static final double DEADBAND = 0.05;

    public static final JoystickAdjuster JOY_ADJUSTER = new JoystickAdjuster(DEADBAND, 2);

    public static final double MAX_STINGER_VEL = 0.09;
}