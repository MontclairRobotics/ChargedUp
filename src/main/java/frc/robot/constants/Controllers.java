package frc.robot.constants;

import org.team555.frc.controllers.GameController;

public final class Controllers 
{
    private Controllers() {}

    public static final GameController.Type OPERATOR_CONTROLLER_TYPE = GameController.Type.PS4;
    public static final GameController.Type DRIVER_CONTROLLER_TYPE = GameController.Type.PS4;

    public static final int OPERATOR_CONTROLLER_PORT = 1;
    public static final int DRIVER_CONTROLLER_PORT = 0;
}
