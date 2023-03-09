package frc.robot.constants;

public class Ports 
{
    private Ports() {}

    // MOTORS //
    public static final int ELEVATOR_PORT = 5;

    public static final int STINGER_MOTOR_PORT = 17;

    public static final int SHWOOPER_LEFT_MOTOR_PORT = 34; //USED FOR SIMPLE INTAKE
    public static final int SHWOOPER_RIGHT_MOTOR_PORT = 90;
    public static final int SHWOOPER_CENTER_MOTOR_PORT = 92;

    public static final int 
        DRIVE_FL_PORT = 10,
        DRIVE_FR_PORT = 1,
        DRIVE_BL_PORT = 3,
        DRIVE_BR_PORT = 41
    ;

    public static final int 
        STEER_FL_PORT = 7,
        STEER_FR_PORT = 18,
        STEER_BL_PORT = 28,
        STEER_BR_PORT = 29
    ;

    // PNEUMATICS //
    public static final int STINGER_PNEU_PORT  = 6;
    public static final int SHWOOPER_PNEU_PORT = 2;

    // CANCODERS //
    public static final int 
        CANCO_FL_PORT = 12,
        CANCO_FR_PORT = 13,
        CANCO_BL_PORT = 11,
        CANCO_BR_PORT = 4
    ;

    // PWM PORTS //
    public static final int LED_PWM_PORT = 9;
}
