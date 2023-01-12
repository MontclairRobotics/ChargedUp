package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Arm extends ManagerSubsystemBase{

    
    CANSparkMax armUpDown = new CANSparkMax(Constants.ARM_UP_DOWN_PORT, MotorType.kBrushless);
    CANSparkMax armInOut = new CANSparkMax(Constants.ARM_IN_OUT_PORT, MotorType.kBrushless);
    
    //rotates the arm using armUpDown motor
    public void rotateWithSpeed(double speed){
        armUpDown.set(speed * Constants.ARM_SPEED);
    }
    
    //extends the arm out using armInOut motor
    public void startExtending(){
        armInOut.set(Constants.ARM_IN_OUT_SPEED);
    }

    //starts retracting the arm using armInOut motor
    public void startRetracting(){
        armInOut.set(Constants.ARM_IN_OUT_SPEED);
    }

    //stops arm in out movement
    public void stop(){
        armInOut.set(0);
    }
}
