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
    
    PIDMechanism armUpDownPID = new PIDMechanism(Robot.armUpDown());
    RelativeEncoder armUpDownEncoder = armUpDown.getEncoder();

    public Arm(){
        armInOutEncoder.setPositionConversionFactor(Robot.ARM_IN_OUT_POSITION_CONVERSION_FACTOR);
        armUpDownEncoder.setPositionConversionFactor(Robot.ARM_UP_DOWN_POSITION_CONVERSION_FACTOR);
    }

    /*
     * rotates the arm with the armUpDown motor at speed ARM_Speed
     */
    public void rotateWithSpeed(double speed){
        armUpDownPID.setSpeed(speed * Robot.ARM_SPEED);
    }

    /*
     * takes an angle as a double and targets that angle with PID
     */
    public void rotateTo(double angle){
        armUpDownPID.setTarget(angle);
    }

    /*
     * returns a boolean of whether or not armUpDown is currently free, true is yes and false is no
     */
    public boolean isUpDownPIDFree(){
        return !armUpDownPID.active();
    }

    /*
     * extends the arm out using armInOut motor at ARM_IN_OUT_SPEED 
     */
    public void startExtending(){
        armInOutPID.setSpeed(Robot.ARM_IN_OUT_SPEED);
    }

    /*
     * retracts the arm using armInOut motor at ARM_IN_OUT_SPEED
     */
    public void startRetracting(){
        armInOutPID.setSpeed(Robot.ARM_IN_OUT_SPEED);
    }

    /*
     * stops arm in out movement
     */
    public void stop(){
        armInOutPID.setSpeed(0);
    }

    /*
     * takes in a target length as a double and extends the arm to that length (meters)
     */
    public void extendTo(double length)
    {
        armInOutPID.setTarget(length);
    }

    /*
     * returns a boolean of whether or not armInOut is currently free, true is yes false is no
     */
    public boolean isInOutPIDFree()
    {
        return !armInOutPID.active();
    }
    
    /*
     * continuously sets the position of armInOut and armUpDown motors using armInOutEncoder and armUpDownEncoder. 
     * This updates the PID and the motors are set to new speeds using new PID calculation
     */
    @Override
    public void always() {
        armInOutPID.setMeasurement(armInOutEncoder.getPosition());
        armInOutPID.update();
        armInOut.set(armInOutPID.getSpeed());

        armUpDownPID.setMeasurement(armUpDownEncoder.getPosition());
        armUpDownPID.update();
        armUpDown.set(armUpDownPID.getSpeed());
    }
}
