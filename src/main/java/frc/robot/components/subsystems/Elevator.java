package frc.robot.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismObject2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.ChargedUp;
import frc.robot.Constants.*;
import frc.robot.math.Math555;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

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

    MechanismLigament2d ligament;

    public MechanismObject2d getMechanismObject() {return ligament;}
    
    public Elevator(boolean exists)
    {
        this.exists = exists;
        if(!exists) return;

        motor = new CANSparkMax(Robot.Elevator.MOTOR_PORT, MotorType.kBrushless);
        motor.setInverted(Robot.Elevator.INVERTED);

        elevatorEncoder = motor.getEncoder();
        elevatorEncoder.setPositionConversionFactor(Robot.Elevator.ENCODER_CONVERSION_FACTOR);

        MechanismRoot2d elevatorRoot = ChargedUp.mainMechanism.getRoot("Elevator::root", 2.5, 0.5);
        ligament = elevatorRoot.append(new MechanismLigament2d("Elevator::length", 0, 90));
        ligament.setColor(new Color8Bit(Simulation.ELEVATOR_COLOR));
    }
    public Elevator() {this(true);}

    /**
     * Set elevator to a desired height
     * @param height (double) the desired height
     */
    private void setHeight(double height)
    {
        if(height > Robot.Elevator.MAX_HEIGHT || height < Robot.Elevator.MIN_HEIGHT)
        {
            Logging.info(
                "Invalid height [" + height + " m] provided to Elevator.setHeight, clamping to acceptable range" +
                "[" + Robot.Elevator.MIN_HEIGHT + " m, " + Robot.Elevator.MAX_HEIGHT + " m]"
            );
        }

        height = Math555.clamp(height, Robot.Elevator.MIN_HEIGHT, Robot.Elevator.MAX_HEIGHT);

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
    public void resetElevatorEncoder() 
    {
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

        if (ChargedUp.shwooper.isShwooperOut() && elevatorEncoder.getPosition() <= Robot.Elevator.BUFFER_SPACE_TO_INTAKE) 
        {
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

        // TODO: actual simulation
        double len = ligament.getLength() + 0.02;
        if(len > Robot.Elevator.MAX_HEIGHT) len = 0;
        ligament.setLength(len);
    }
}
