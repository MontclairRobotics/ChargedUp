package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.constants.Constants.*;
import frc.robot.framework.commandrobot.ManagerSubsystemBase;
import frc.robot.structure.PIDMechanism;

public class Arm extends ManagerSubsystemBase{

    
    CANSparkMax armUpDown = new CANSparkMax(Robot.Arm.UP_DOWN_PORT, MotorType.kBrushless);
    CANSparkMax armInOut = new CANSparkMax(Robot.Arm.IN_OUT_PORT, MotorType.kBrushless);

    PIDMechanism armInOutPID = new PIDMechanism(Robot.Arm.inout());
    RelativeEncoder armInOutEncoder = armInOut.getEncoder();
    
    PIDMechanism armUpDownPID = new PIDMechanism(Robot.Arm.updown());
    RelativeEncoder armUpDownEncoder = armUpDown.getEncoder();

    public Arm()
    {
        armInOutEncoder.setPositionConversionFactor(Robot.Arm.IN_OUT_POSITION_CONVERSION_FACTOR);
        armUpDownEncoder.setPositionConversionFactor(Robot.Arm.UP_DOWN_POSITION_CONVERSION_FACTOR);
    }

    /**
     * rotates the arm with the armUpDown motor at {@link Robot#UP_DOWN_SPEED arm speed constant}
     */
    public void rotateWithSpeed(double speed)
    {
        armUpDownPID.setSpeed(speed * Robot.Arm.UP_DOWN_SPEED);
    }

    /**
     * takes an angle as a double and targets that angle with PID
     * 
     * @param angle target angle to rotate to
     */
    public void rotateTo(double angle)
    {
        armUpDownPID.setTarget(angle);
    }

    /**
     * whether or not armUpDown is currently free (if it is not currently pidding)
     * @return boolean, true is yes and false is no
     */
    public boolean isUpDownPIDFree()
    {
        return !armUpDownPID.active();
    }

    /**
     * extends the arm out using armInOut motor at {@link Robot#IN_OUT_SPEED arm speed constant} 
     */
    public void startExtending()
    {
        armInOutPID.setSpeed(Robot.Arm.IN_OUT_SPEED);
    }

    /**
     * retracts the arm using armInOut motor at -{@link Robot#IN_OUT_SPEED arm speed constant}
     */
    public void startRetracting()
    {
        armInOutPID.setSpeed(-Robot.Arm.IN_OUT_SPEED);
    }

    /**
     * stops arm in out movement
     */
    public void stop()
    {
        armInOutPID.setSpeed(0);
    }

    /**
     * takes in a target length as a double and extends the arm to that length (meters)
     */
    public void extendTo(double length)
    {
        armInOutPID.setTarget(length);
    }

    /**
     * whether or not armInOut is currently free (if not currently pidding)
     * @return boolean, true is yes false is no
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
