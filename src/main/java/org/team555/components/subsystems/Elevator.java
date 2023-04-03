package org.team555.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.SimDeviceSim;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismObject2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.team555.ChargedUp;
import org.team555.constants.Ports;
import org.team555.constants.SimulationConstants;
import org.team555.constants.ElevatorConstants;
import org.team555.math.Math555;
import org.team555.util.frc.LimitSwitch;
import org.team555.util.frc.Logging;
import org.team555.util.frc.PIDMechanism;
import org.team555.util.frc.SimulationUtility;
import org.team555.util.frc.can.CANSafety;
import org.team555.util.frc.commandrobot.ManagerSubsystemBase;

import static org.team555.constants.ElevatorConstants.*;

public class Elevator extends ManagerSubsystemBase 
{
    private boolean shouldStop;

    private CANSparkMax motor;
    RelativeEncoder encoder;

    SimDeviceSim motorSim;

    // Rev limit switches are true if not active, false if active --> so invert
    private LimitSwitch toplimitSwitch = new LimitSwitch(TOP_LIMIT_SWITCH, true); 
    private LimitSwitch bottomlimitSwitch = new LimitSwitch(BOTTOM_LIMIT_SWITCH, true);

    public final PIDMechanism PID = new PIDMechanism(updown());
    private final SlewRateLimiter speedRateLimiter = new SlewRateLimiter(1.0/0.5);

    MechanismLigament2d ligament;

    public MechanismObject2d getMechanismObject() {return ligament;}
    
    public Elevator()
    {
        motor = new CANSparkMax(Ports.ELEVATOR_PORT, MotorType.kBrushless);
        motor.setInverted(INVERTED);

        CANSafety.monitor(motor);

        encoder = motor.getEncoder();

        encoder.setPositionConversionFactor(ENCODER_CONVERSION_FACTOR);
        encoder.setPosition(MIN_HEIGHT);

        motorSim = new SimDeviceSim("SPARK MAX [" + motor.getDeviceId() + "]");

        MechanismRoot2d elevatorRoot = ChargedUp.mainMechanism.getRoot("Elevator::root", 2.5, 0.5);
        ligament = elevatorRoot.append(new MechanismLigament2d("Elevator::length", 0, 90));
        ligament.setColor(new Color8Bit(SimulationConstants.ELEVATOR_COLOR));
    }

    public double getHeightNormalized()
    {
        return Math555.invlerp(encoder.getPosition(), MIN_HEIGHT, MAX_HEIGHT);
    }

    public boolean isWithinLowerBuffer()
    {
        return getHeightNormalized() < BUFFER_DOWN;
    }
    public boolean isWithinUpperBuffer()
    {
        return getHeightNormalized() > (1 - BUFFER_UP);
    }
    
    public boolean isWithinGrabberBuffer()
    {
        System.out.println(getHeightNormalized());
        return getHeightNormalized() < GRABBER_BUFFER;
    }

    private double getUpwardMultiplier()
    {
        double ynorm = getHeightNormalized();

        //if not close to top (not with the buffer zone) return elevator max speed
        if (!isWithinUpperBuffer()) return 1;

        double reducedSpeed = 1 - 1 * (ynorm - 1 + BUFFER_UP) / BUFFER_UP;

        return Math555.clamp(reducedSpeed, 0.15, 1);
    }
    private double getDownwardsMultiplier()
    {
        double ynorm = getHeightNormalized();

        //if not close to bottom (not with the buffer zone) return elevator max speed
        if (!isWithinLowerBuffer()) return 1;

        double reducedSpeed = ynorm / BUFFER_DOWN * 1;

        return Math555.clamp(reducedSpeed, 0.15, 1);
    }

    public boolean stingerCanMove()
    {
        return encoder.getPosition() > BUFFER_SPACE_TO_INTAKE + Units.inchesToMeters(5);
    }
    public boolean stingerCannotMove()
    {
        return !stingerCanMove();
    }

    /**
     * Modify the speed of the elevator so that when it is close to a limit switch (@top and bottom)
     * it slows down proportionally to its distance to the top (or distance to the bottom)
     * The multiplier is clamped between [0.25, 1] so it will not go slower than 25% of the input
     * <p>
     * Essentially, it slows down from the max speed as it approaches top/bottom
     * <p>
     * <ul>
     * <li>When the elevator speed is positive (moving upwards), it is calculated using {@link Elevator#getUpwardMultiplier()}
     * <li>When the elevator speed is negative (moving downward), it is calculated using {@link Elevator#getDownwardsMultiplier()}
     * <p>
     */
    private double getModifiedSpeed(double speed)
    {
        if(speed > 0) return getUpwardMultiplier()    * speed;
        else          return getDownwardsMultiplier() * speed;
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
     * returns if the limit switch for the starting position of the elevator is triggered
     * @return boolean
     */
    public boolean isAtBottom() 
    {
        return bottomlimitSwitch.get(); 
    }
    public boolean isAtTop() 
    {
        return toplimitSwitch.get(); 
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

    public double getHeight()
    {
        return encoder.getPosition();
    }

    
    @Override
    public void always() 
    {
        PID.setMeasurement(getHeight());
        PID.update();

        shouldStop = false;

        if (PID.getSpeed() > 0) 
        {
            if (toplimitSwitch.get()) 
            {
                shouldStop = true;
                PID.setSpeed(0);
                encoder.setPosition(MAX_HEIGHT);
            }
        } 
        else 
        {
            if (bottomlimitSwitch.get()) 
            {
                shouldStop = true;
                PID.setSpeed(0);
                encoder.setPosition(MIN_HEIGHT);
            }
            else if (ChargedUp.stinger.isOut() && getHeight() <= BUFFER_SPACE_TO_INTAKE)
            {
                shouldStop = true;
                PID.setSpeed(0);
            }
        }

        double ff = FEED_FORWARD_VOLTS.get();
        if(getHeight() < BUFFER_SPACE_TO_INTAKE || RobotBase.isSimulation()) 
            ff = 0;

        double speed;
        if(shouldStop) 
        {
            speed = 0;
            reset();
        }
        else 
        {
            speed = getModifiedSpeed(PID.getSpeed());
            speed = speedRateLimiter.calculate(speed);
        }
        
        motor.set(speed + ff / 12);

        // SIMULATION //
        ligament.setLength(getHeight());
        
        if(RobotBase.isSimulation())
        {
            motor.set(motor.get() * 0.2); // Add 'load' factor

            SimulationUtility.simulateNEO(motor, encoder);
            bottomlimitSwitch.set(getHeight() <= MIN_HEIGHT);
            toplimitSwitch   .set(getHeight() >= MAX_HEIGHT);
        }
    }
    
    @Override
    public void reset() 
    {
        stop();
        speedRateLimiter.reset(0);
    }
}
