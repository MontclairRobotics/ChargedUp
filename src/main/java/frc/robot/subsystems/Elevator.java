import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.constants.Constants.*;

public class Elevator {
    private CANSparkMax motor = new CANSparkMax(ELEVATOR_MOTOR_PORT, MotorType.kBrushless);
    
    public Elevator(){
        motor.setInverted(ELEVATOR_INVERTED);
    }

    public void elevate(){
        motor.set(ELEVATOR_MOTOR_SPEED);
    }

    public void delevate(){
        motor.set(-ELEVATOR_MOTOR_SPEED);
    }

    public void stop(){
        motor.set(0);
    }
}
