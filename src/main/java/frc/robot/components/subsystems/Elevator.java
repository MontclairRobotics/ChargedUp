package frc.robot.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVPhysicsSim;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDeviceJNI;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.simulation.SimDeviceSim;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismObject2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.ChargedUp;
import frc.robot.constants.Ports;
import frc.robot.constants.SimulationConstants;
import frc.robot.math.Math555;
import frc.robot.util.frc.LimitSwitch;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.SimulationUtility;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

import static frc.robot.constants.ElevatorConstants.*;

public class Elevator extends ManagerSubsystemBase 
{
    private boolean shouldStop;

    private CANSparkMax motor;
    RelativeEncoder encoder;

    SimDeviceSim motorSim;

    private LimitSwitch toplimitSwitch = new LimitSwitch(TOP_LIMIT_SWITCH);
    private LimitSwitch bottomlimitSwitch = new LimitSwitch(BOTTOM_LIMIT_SWITCH);
    // private LimitSwitch startlimitSwitch = new LimitSwitch(START_LIMIT_SWITCH);

    public final PIDMechanism PID = new PIDMechanism(updown());

    MechanismLigament2d ligament;

    double maxSpeed = 1;

    public MechanismObject2d getMechanismObject() {return ligament;}
    
    public Elevator()
    {
        motor = new CANSparkMax(Ports.ELEVATOR_PORT, MotorType.kBrushless);
        motor.setInverted(INVERTED);

        encoder = motor.getEncoder();
        encoder.setPositionConversionFactor(ENCODER_CONVERSION_FACTOR);
        encoder.setPosition(MIN_HEIGHT);

        motorSim = new SimDeviceSim("SPARK MAX [" + motor.getDeviceId() + "]");

        MechanismRoot2d elevatorRoot = ChargedUp.mainMechanism.getRoot("Elevator::root", 2.5, 0.5);
        ligament = elevatorRoot.append(new MechanismLigament2d("Elevator::length", 0, 90));
        ligament.setColor(new Color8Bit(SimulationConstants.ELEVATOR_COLOR));
    }

    /**
     * Set elevator to a desired height
     * @param height (double) the desired height
     */
    private void setHeight(double height)
    {
        if(height > MAX_HEIGHT || height < MIN_HEIGHT)
        {
            Logging.info(
                "Invalid height [" + height + " m] provided to Elevator.setHeight, clamping to acceptable range" +
                "[" + MIN_HEIGHT + " m, " + MAX_HEIGHT + " m]"
            );
        }

        height = Math555.clamp(height, MIN_HEIGHT, MAX_HEIGHT);

        PID.setTarget(height);
    }

    /**
     * Set the elevator to {@link Robot#HIGH_HEIGHT the high height}
     */
    public void setHigh()
    {
        setHeight(HIGH_HEIGHT);
    }

    /**
     * Set the elevator to {@link Robot#MID_HEIGHT the middle height}
     */
    public void setMid()
    {
        setHeight(MID_HEIGHT);
    }

    /**
     * Set elevator to LOW position
     */
    public void setLow()
    {
        setHeight(0);
    }

    /**
     * Lowers elevator height manually at the negative {@link Robot#ELEVATOR_SPEED elevator speed constant}
     */
    public void delevate()
    {
        PID.setSpeed(-SPEED);
    }

    /**
     * Raises elevator height manually at the {@link Robot#ELEVATOR_SPEED elevator speed constant}
     */
    public void elevate()
    {
        PID.setSpeed(SPEED);
    }

    /**
     * Set the speed of the {@link Elevator#motor Elevator motor}
     * @param speed desired speed
     */
    public void setSpeed(double speed) 
    {
        PID.setSpeed(speed);
    }

    /** 
     * Sets elevator speed to zero
     */
    public void stop()
    {
        PID.setSpeed(0);
    }

    /** 
     * Resets the elevator encoder to 0
     */
    public void resetElevatorEncoder() 
    {
        encoder.setPosition(0);
    }

    /**
     * returns if the limit switch for the starting position of the elevator is triggered
     * @return boolean
     */
    public boolean isAtBottom() 
    {
        return bottomlimitSwitch.get(); 
    }

    /**
     * returns if the elevator is currently not using {@link Elevator#PID PID}
     * @return boolean
     */
    public boolean isPIDFree()
    {
        return !PID.active();
    }

    /**
     * Cancel the elevator PIDing using {@link Elevator#PID}
     */
    public void stopPIDing()
    {
        PID.cancel();
    }

    
    @Override
    public void always() 
    {
        shouldStop = false;

        if (ChargedUp.shwooper.isShwooperOut() && encoder.getPosition() <= BUFFER_SPACE_TO_INTAKE) 
        {
            if (PID.getSpeed() < 0) 
            {
                PID.cancel();
                PID.setSpeed(0);
            }
        }

        if (PID.getSpeed() > 0) 
        {
            if (toplimitSwitch.get()) 
            {
                shouldStop = true;
                PID.setSpeed(0);
            }
        } 
        else 
        {
            if (bottomlimitSwitch.get()) 
            {
                shouldStop = true;
                PID.setSpeed(0);
            }
        }

        // Prevent stupid
        maxSpeed = encoder.getPosition() > MAX_HEIGHT - BUFFER_TO_MAX 
                || encoder.getPosition() < MIN_HEIGHT + BUFFER_TO_MAX ? 0.1 : 1;

        PID.setMeasurement(encoder.getPosition());
        PID.update();

        // System.out.println("----------------------------------------------");
        // System.out.println("Elevator::measurement = " + PID.getMeasurement());
        // System.out.println("Elevator::error       = " + PID.getPIDController().getPositionError());
        // System.out.println("Elevator::setpoint    = " + PID.getPIDController().getSetpoint());
        // System.out.println("Elevator::speed       = " + PID.getSpeed());
        // System.out.println("Elevator::position    = " + encoder.getPosition());
        // System.out.println("Elevator::raw_speed   = " + PID.getPIDController().calculate(PID.getMeasurement()));
        // System.out.println("Elevator::pid_on = " + PID.active());
        // System.out.println("Stinger::pid_on  = " + ChargedUp.stinger.PID.active());
        
        if(shouldStop) motor.set(0);
        else           motor.set(PID.getSpeed() * maxSpeed);


        if(RobotBase.isSimulation())
        {
            SimulationUtility.simulateNEO(motor, encoder);
        }

        ligament.setLength(encoder.getPosition());
    }
}
