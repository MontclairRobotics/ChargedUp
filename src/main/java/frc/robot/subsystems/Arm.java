package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.constants.Constants.*;
import frc.robot.structure.PIDMechanism;

public class Arm extends ManagerSubsystemBase{

    
    CANSparkMax armUpDown = new CANSparkMax(Robot.ARM_UP_DOWN_PORT, MotorType.kBrushless);
    CANSparkMax armInOut = new CANSparkMax(Robot.ARM_IN_OUT_PORT, MotorType.kBrushless);
    PIDMechanism armInOutPID = new PIDMechanism(Robot.armInOut());
    RelativeEncoder armInOutEncoder = armInOut.getEncoder();

    public Arm(){
        armInOutEncoder.setPositionConversionFactor(Robot.ARM_IN_OUT_POSITION_CONVERSION_FACTOR);
    }


    //rotates the arm using armUpDown motor
    public void rotateWithSpeed(double speed){
        armUpDown.set(speed * Robot.ARM_SPEED);
    }
    
    //extends the arm out using armInOut motor
    public void startExtending(){
        armInOutPID.setSpeed(Robot.ARM_IN_OUT_SPEED);
    }

    //starts retracting the arm using armInOut motor
    public void startRetracting(){
        armInOutPID.setSpeed(Robot.ARM_IN_OUT_SPEED);
    }

    //stops arm in out movement
    public void stop(){
        armInOutPID.setSpeed(0);
    }

    public void goToLength(double length)
    {
        armInOutPID.setTarget(length);
    }

    public boolean isInOutPIDFree()
    {
        return !armInOutPID.active();
    }
    

    @Override
    public void always() {
        armInOutPID.setMeasurement(armInOutEncoder.getPosition());
        armInOutPID.update();
        armInOut.set(armInOutPID.getSpeed());
    }
}
