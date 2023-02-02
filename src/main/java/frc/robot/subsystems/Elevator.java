package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.Constants.*;
import frc.robot.structure.PIDMechanism;

public class Elevator extends ManagerSubsystemBase {
    private CANSparkMax motor = new CANSparkMax(Robot.Elevator.MOTOR_PORT, MotorType.kBrushless);

    PIDMechanism elevatorPID = new PIDMechanism(Robot.Elevator.updown());
    RelativeEncoder elevatorEncoder = motor.getEncoder();
    
    public Elevator()
    {
        motor.setInverted(Robot.Elevator.INVERTED);
        elevatorEncoder.setPositionConversionFactor(Robot.Elevator.UP_DOWN_CONVERSION_FACTOR);
    }

    /**
     * Set elevator to a desired height
     * @param height (double) the desired height
     */
    private void setHeight(double height)
    {
        if (height > Robot.Elevator.MAX_HEIGHT) {
            height = Robot.Elevator.MAX_HEIGHT;
        }
        elevatorPID.setTarget(height);
    }

    /**
     * Set the elevator to {@link Robot#HIGH_HEIGHT the high height}
     */
    public void setHigh()
    {
        setHeight(Robot.Elevator.HIGH_HEIGHT);
    }

    /**
     * Set the elevator to {@link Robot#MID_HEIGHT the middle height}
     */
    public void setMid()
    {
        setHeight(Robot.Elevator.MID_HEIGHT);
    }

    /**
     * Set elevator to LOW position
     */
    public void setLow() 
    {
        elevatorPID.setTarget(0);
    }

    /**
     * Lowers elevator height manually at the negative {@link Robot#ELEVATOR_SPEED elevator speed constant}
     */
    public void delevate()
    {
        elevatorPID.setSpeed(-Robot.Elevator.SPEED);
    }

    /**
     * Raises elevator height manually at the {@link Robot#ELEVATOR_SPEED elevator speed constant}
     */
    public void elevate()
    {
        elevatorPID.setSpeed(Robot.Elevator.SPEED);
    }

    /**
     * Set the speed of the {@link Elevator#motor Elevator motor}
     * @param speed desired speed
     */
    public void setSpeed(double speed) 
    {
        elevatorPID.setSpeed(speed);
    }

    /** 
     * Sets elevator speed to zero
     */
    public void stop()
    {
        elevatorPID.setSpeed(0);
    }

    /**
     * returns if the elevator is currently not using {@link Elevator#elevatorPID PID}
     * @return boolean
     */
    public boolean isPIDFree()
    {
        return !elevatorPID.active();
    }

    /**
     * Cancel the elevator PIDing using {@link Elevator#elevatorPID}
     */
    public void stopPIDing()
    {
        elevatorPID.cancel();
    }

    
    @Override
    public void always() 
    {
        elevatorPID.setMeasurement(elevatorEncoder.getPosition());
        elevatorPID.update();
        motor.set(elevatorPID.getSpeed());
    }
}
