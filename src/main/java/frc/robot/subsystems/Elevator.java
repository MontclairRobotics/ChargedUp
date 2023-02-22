package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import frc.robot.ChargedUp;
import frc.robot.Constants.*;
import frc.robot.framework.commandrobot.ManagerSubsystemBase;
import frc.robot.structure.PIDMechanism;

public class Elevator extends ManagerSubsystemBase 
{
    private boolean exists;

    private boolean shouldStop;

    private CANSparkMax motor;

    private DigitalInput toplimitSwitch = new DigitalInput(Robot.Elevator.TOP_LIMIT_SWITCH);
    private DigitalInput bottomlimitSwitch = new DigitalInput(Robot.Elevator.BOTTOM_LIMIT_SWITCH);
    private DigitalInput startlimitSwitch = new DigitalInput(Robot.Elevator.START_LIMIT_SWITCH);

    // does not work, copied off https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-simulation/physics-sim.html
    // private ElevatorSim elevatorSim =
    //     new ElevatorSim(
    //     gearbox,
    //     40,
    //     15,
    //     0.939,
    //     0,
    //     2,
    //     false,
    //     VecBuilder.fill(0.01));

    // private final EncoderSim m_encoderSim = new EncoderSim(m_encoder);

    PIDMechanism elevatorPID = new PIDMechanism(Robot.Elevator.updown());
    RelativeEncoder elevatorEncoder;
    
    public Elevator(boolean exists)
    {
        this.exists = exists;
        if(!exists) return;

        motor = new CANSparkMax(Robot.Elevator.MOTOR_PORT, MotorType.kBrushless);
        motor.setInverted(Robot.Elevator.INVERTED);

        elevatorEncoder = motor.getEncoder();
        elevatorEncoder.setPositionConversionFactor(Robot.Elevator.UP_DOWN_CONVERSION_FACTOR);
    }
    public Elevator() {this(true);}

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
     * Resets the elevator encoder to 0
     */
    public void resetElevatorEncoder() {
        elevatorEncoder.setPosition(0);
    }

    /**
     * returns if the limit switch for the starting position of the elevator is triggered
     * @return boolean
     */
    public boolean isAtStartPosition() 
    {
        return startlimitSwitch.get(); 
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
        shouldStop = false;

        if(!exists) return;

        if (ChargedUp.shwooper.isShwooperOut() && elevatorEncoder.getPosition() <= Robot.Elevator.BUFFER_SPACE_TO_INTAKE) {
            elevatorPID.cancel();
            if (elevatorPID.getSpeed() < 0) 
            {
                elevatorPID.setSpeed(0);
            }
        }

        if (elevatorPID.getSpeed() > 0) 
        {
            if (toplimitSwitch.get()) 
            {
                shouldStop = true;
                elevatorPID.setSpeed(0);
            }
        } 
        else 
        {
            if (bottomlimitSwitch.get()) 
            {
                shouldStop = true;
                elevatorPID.setSpeed(0);
            }
        }

        elevatorPID.setMeasurement(elevatorEncoder.getPosition());
        elevatorPID.update();
        
        if(shouldStop) motor.set(0);
        else motor.set(elevatorPID.getSpeed());
    }
}
