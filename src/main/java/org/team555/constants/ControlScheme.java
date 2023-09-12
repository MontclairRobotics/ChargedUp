package org.team555.constants;

import org.team555.inputs.FlightStickAdjuster;
import org.team555.inputs.JoystickAdjuster;
import org.team555.util.frc.GameController;

public class ControlScheme
{
    public static final GameController.Type OPERATOR_CONTROLLER_TYPE = GameController.Type.PS4;
    public static final GameController.Type DRIVER_CONTROLLER_TYPE   = GameController.Type.PS4;
    public static final GameController.Type DEBUG_CONTROLLER_TYPE   = GameController.Type.XBOX;

    public static final int OPERATOR_CONTROLLER_PORT = 0;
    public static final int DRIVER_CONTROLLER_PORT   = 1;
    public static final int DEBUG_CONTROLLER_PORT    = 2;
    public static final int DRIVER_STEER_JOYSTICK = 3;
    public static final int DRIVER_DRIVE_JOYSTICK = 4;

    public static final double DRIVE_DEADBAND = 0.05;
    public static final double DRIVE_POWER = 2.2;
    public static final double STEER_POWER = 1.5;
    
    public static final JoystickAdjuster DRIVE_ADJUSTER = new JoystickAdjuster(DRIVE_DEADBAND, DRIVE_POWER);
    public static final JoystickAdjuster TURN_ADJUSTER  = new JoystickAdjuster(DRIVE_DEADBAND, STEER_POWER);

    // FLIGHT STICK ADJUSTERS

    public static final FlightStickAdjuster DRIVE_STICK_ADJUSTER = new FlightStickAdjuster(DRIVE_DEADBAND, DRIVE_POWER, true, true);
    public static final FlightStickAdjuster STEER_STICK_ADJUSTER = new FlightStickAdjuster(DRIVE_DEADBAND, STEER_POWER, true, true);
}
