package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.Constants.*;
import frc.robot.structure.PIDMechanism;

public class Elevator extends ManagerSubsystemBase {
    private CANSparkMax motor = new CANSparkMax(Robot.Elevator.ELEVATOR_MOTOR_PORT, MotorType.kBrushless);

    PIDMechanism elevatorPID = new PIDMechanism(Robot.Elevator.elevatorUpDown());
    RelativeEncoder elevatorEncoder = motor.getEncoder();
    
    public Elevator()
    {
        motor.setInverted(Robot.Elevator.ELEVATOR_INVERTED);
        elevatorEncoder.setPositionConversionFactor(Robot.Elevator.ELEVATOR_UP_DOWN_CONVERSION_FACTOR);
    }

    /**
     * Set elevator to a desired height
     * @param height (double) the desired height
     */
    private void setHeight(double height)
    {
        if (height > Robot.Elevator.ELEVATOR_MAX_HEIGHT) {
            height = Robot.Elevator.ELEVATOR_MAX_HEIGHT;
        }
        elevatorPID.setTarget(height);
    }
    /**
     * Set the elevator to {@link Robot#ELEVATOR_HIGH_HEIGHT the high height}
     */
    public void setHigh()
    {
        setHeight(Robot.Elevator.ELEVATOR_HIGH_HEIGHT);
    }
    /**
     * Set the elevator to {@link Robot#ELEVATOR_MID_HEIGHT the middle height}
     */
    public void setMid()
    {
        setHeight(Robot.Elevator.ELEVATOR_MID_HEIGHT);
    }
    /**
     * Set elevator to LOW position
     */
    public void setLow() 
    {
        elevatorPID.setTarget(0);
    }


    /**
     * Raise elevator height manually at the negative {@link Robot#ELEVATOR_SPEED elevator speed constant}
     */
    public void delevate()
    {
        elevatorPID.setSpeed(-Robot.Elevator.ELEVATOR_SPEED);
    }

    /**
     * Lower elevator height manually at the {@link Robot#ELEVATOR_SPEED elevator speed constant}
     */
    public void elevate()
    {
        elevatorPID.setSpeed(Robot.Elevator.ELEVATOR_SPEED);
    }

    /**
     * Set the speed of the Elevator motor
     * @param speed desired speed
     */
    public void setSpeed(double speed) 
    {
        elevatorPID.setSpeed(speed);
    }

    /** 
     * Set elevator speed to zero
     */
    public void stop()
    {
        elevatorPID.setSpeed(0);
    }

    /**
     * returns if the elevator is currently not using pid
     * @return boolean
     */
    public boolean isPIDFree()
    {
        return !elevatorPID.active();
    }
    /**
     * Cancel the elevator PIDing 
     */
    public void stopPIDing()
    {
        elevatorPID.cancel();
    }
    // run PID
    @Override
    public void always() 
    {
        elevatorPID.setMeasurement(elevatorEncoder.getPosition());
        elevatorPID.update();
        motor.set(elevatorPID.getSpeed());
    }
}
