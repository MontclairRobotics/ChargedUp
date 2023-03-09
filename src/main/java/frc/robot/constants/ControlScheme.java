package frc.robot.constants;

import frc.robot.inputs.JoystickAdjuster;
import frc.robot.util.frc.GameController;

public class ControlScheme
{
    public static final GameController.Type OPERATOR_CONTROLLER_TYPE = GameController.Type.PS4;
    public static final GameController.Type DRIVER_CONTROLLER_TYPE   = GameController.Type.PS4;
    public static final GameController.Type DEBUG_CONTROLLER_TYPE   = GameController.Type.XBOX;

    public static final int OPERATOR_CONTROLLER_PORT = 0;
    public static final int DRIVER_CONTROLLER_PORT   = 1;
    public static final int DEBUG_CONTROLLER_PORT   = 2;

    public static final double DRIVE_DEADBAND = 0.05;
    
    public static final JoystickAdjuster DRIVE_ADJUSTER = new JoystickAdjuster(DRIVE_DEADBAND, 2.2);
    public static final JoystickAdjuster TURN_ADJUSTER  = new JoystickAdjuster(DRIVE_DEADBAND, 1.5);
}
