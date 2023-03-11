package frc.robot.constants;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import frc.robot.inputs.JoystickAdjuster;
import frc.robot.util.frc.Tunable;

import static frc.robot.constants.GrabberConstants.*;

public class ElevatorConstants
{
    private ElevatorConstants() {}
    // GEAR RATIO  :=  40 in : 1 out
    // PULLY SIZE  :=  4 in.

    public static final double GEAR_RATIO_OUT_OVER_IN = 1.0 / 40.0;
    public static final double SPROCKET_DIAMETER = Units.inchesToMeters(8+0*1.685); //TODO: this is wrong but it works

    public static final double ENCODER_CONVERSION_FACTOR = GEAR_RATIO_OUT_OVER_IN * SPROCKET_DIAMETER * Math.PI;

    public static final boolean INVERTED = true;
    // public static final boolean ENCODER_INVERTED = true;

    public static final int TOP_LIMIT_SWITCH = 0;
    public static final int BOTTOM_LIMIT_SWITCH = 1;
    public static final int START_LIMIT_SWITCH = 2; //not used anyomore

    public static final double SPEED = 1;  
    
    
    public static final double ELEVATOR_MIN_TO_FLOOR = Units.feetToMeters(6.0/12);

    public static final double BUFFER_TO_MAX = Units.inchesToMeters(6);
    public static final double MAX_HEIGHT = Units.inchesToMeters(62);
    
    public static final double HIGH_HEIGHT = MAX_HEIGHT;//Units.feetToMeters(3.0 + 10.0/12) - ELEVATOR_MIN_TO_FLOOR + GRABBER_HEIGHT_NO_OBJECT;
    public static final double MID_HEIGHT  = MAX_HEIGHT - Units.inchesToMeters(0);//Units.feetToMeters(2.0 + 10.0/12) - ELEVATOR_MIN_TO_FLOOR + GRABBER_HEIGHT_NO_OBJECT;

    public static final double BUFFER_SPACE_TO_INTAKE = Units.feetToMeters(12.0/12);
    public static final double MIN_HEIGHT = 0;

    public static final double DEADBAND = 0.05;
    public static final JoystickAdjuster JOY_ADJUSTER = new JoystickAdjuster(DEADBAND, 2.2);   

    public static final Tunable<Double> FEED_FORWARD = Tunable.of(0.05, "elevator.ff");

    public static final double 
        KP = 0.25,
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