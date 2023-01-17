// package frc.robot.subsystems;

// import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

// import com.revrobotics.CANSparkMax;
// import com.revrobotics.CANSparkMaxLowLevel.MotorType;

// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import frc.robot.constants.Constants.*;

// public class Elevator {
//     private CANSparkMax motor = new CANSparkMax(Robot.ELEVATOR_MOTOR_PORT, MotorType.kBrushless);
    
//     public Elevator(){
//         motor.setInverted(Robot.ELEVATOR_INVERTED);
//     }

//     public void elevate(){
//         motor.set(Robot.ELEVATOR_SPEED);
//     }

//     public void delevate(){
//         motor.set(-Robot.ELEVATOR_SPEED);
//     }

//     public void stop(){
//         motor.set(0);
//     }
// }
