package org.team555.constants;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import org.team555.inputs.JoystickAdjuster;
import org.team555.util.frc.Tunable;

public class ElevatorConstants
{
    private ElevatorConstants() {}
    // GEAR RATIO  :=  40 in : 1 out
    // PULLY SIZE  :=  4 in.

    public static final double GEAR_RATIO_OUT_OVER_IN = 1.0 / 40.0;
    public static final double SPROCKET_DIAMETER = Units.inchesToMeters(1.685);
    public static final double STAGE_COUNT = 3;
    public static final double MYSTERY_MULTIPLIER = 1.58259149357; //TODO: this is very weird????

    public static final double ENCODER_CONVERSION_FACTOR 
        = GEAR_RATIO_OUT_OVER_IN * SPROCKET_DIAMETER * Math.PI * STAGE_COUNT * MYSTERY_MULTIPLIER;

    public static final boolean INVERTED = true;
    // public static final boolean ENCODER_INVERTED = true;

    public static final double BUFFER_UP = 0.1;
    public static final double BUFFER_DOWN = 0.15;

    public static final int TOP_LIMIT_SWITCH = 0;
    public static final int BOTTOM_LIMIT_SWITCH = 1;
    public static final int START_LIMIT_SWITCH = 2; //not used anyomore

    public static final double SPEED = 1;  
    
    
    public static final double ELEVATOR_MIN_TO_FLOOR = Units.feetToMeters(6.0/12);

    public static final double MAX_HEIGHT = Units.inchesToMeters(65);
    public static final double MIN_HEIGHT = Units.inchesToMeters(23);
    
    public static final double MID_HEIGHT_CONE  = MAX_HEIGHT;//Units.feetToMeters(2.0 + 10.0/12) - ELEVATOR_MIN_TO_FLOOR + GRABBER_HEIGHT_NO_OBJECT;
    public static final double MID_HEIGHT_CUBE = MAX_HEIGHT - Units.inchesToMeters(10);

    public static final double BUFFER_SPACE_TO_INTAKE = MIN_HEIGHT + Units.feetToMeters(16.0/12);

    public static final double DEADBAND = 0.05;
    public static final JoystickAdjuster JOY_ADJUSTER = new JoystickAdjuster(DEADBAND, 2.2);   

    public static final Tunable<Double> FEED_FORWARD_VOLTS = Tunable.of(0.05*12, "elevator.ff_volts");

    public static final double 
        KP = 0.75,
        KI = 0.1,
        KD = 0
    ;

    public static final PIDController updown() 
    {
        PIDController p = new PIDController(KP, KI, KD);
        // p.setTolerance(0.04);
        return p;
    }
}