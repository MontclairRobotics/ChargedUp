package org.team555.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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

    MechanismLigament2d ligament;

    public MechanismObject2d getMechanismObject() {return ligament;}
    
    public Elevator()
    {
        motor = new CANSparkMax(Ports.ELEVATOR_PORT, MotorType.kBrushless);
        motor.setInverted(INVERTED);

        CANSafety.monitor(motor);

        encoder = motor.getEncoder();
        // encoder.setInverted(ENCODER_INVERTED);
        encoder.setPositionConversionFactor(ENCODER_CONVERSION_FACTOR);
        encoder.setPosition(MIN_HEIGHT);

        motorSim = new SimDeviceSim("SPARK MAX [" + motor.getDeviceId() + "]");

        MechanismRoot2d elevatorRoot = ChargedUp.mainMechanism.getRoot("Elevator::root", 2.5, 0.5);
        ligament = elevatorRoot.append(new MechanismLigament2d("Elevator::length", 0, 90));
        ligament.setColor(new Color8Bit(SimulationConstants.ELEVATOR_COLOR));
    }

    private double getHeightNormalized()
    {
        return Math555.invlerp(encoder.getPosition(), MIN_HEIGHT, MAX_HEIGHT);
    }

    private double getUpwardMultiplier()
    {
        double ynorm = getHeightNormalized();

        if(ynorm < 1 - BUFFER_UP) return SPEED;
        return Math555.clamp(SPEED - SPEED * (ynorm - 1 + BUFFER_UP) / BUFFER_UP, 0.25, 1);
    }
    private double getDownwardsMultiplier()
    {
        double ynorm = getHeightNormalized();

        if(ynorm > BUFFER_DOWN) return SPEED;
        return Math555.clamp(ynorm / BUFFER_DOWN * SPEED, 0.25, 1);
    }

    private double getModifiedSpeed(double speed)
    {
        if(speed > 0) return getUpwardMultiplier()    * speed;
        else          return getDownwardsMultiplier() * speed;
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
     * Set the elevator to {@link Robot#MID_HEIGHT the middle height}
     */
    public void setMidCube()
    {
        setHeight(MID_HEIGHT_CUBE);
    }

    /**
     * Sets the elevator to {@link ElevatorConstants#MID_HEIGHT_CUBE the middle cone height}
     */
    public void setMidCone()
    {
        setHeight(MID_HEIGHT_CONE);
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
        // Logging.info("" + PID.getSpeed());
        shouldStop = false;

        if (PID.getSpeed() > 0) 
        {
            if (toplimitSwitch.get()) 
            {
                // Logging.info("IM A TOP");
                shouldStop = true;
                PID.setSpeed(0);
                encoder.setPosition(MAX_HEIGHT);
            }
        } 
        else 
        {
            if (bottomlimitSwitch.get()) 
            {
                // Logging.info("IM A BOTTOM");
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

        // Logging.info("" + encoder.getPosition());

        // maxSpeed *= 0.1;

        double ff = FEED_FORWARD.get();
        if(getHeight() < BUFFER_SPACE_TO_INTAKE) ff = 0;

        // System.out.println("----------------------------------------------");
        // System.out.println("Elevator::measurement = " + PID.getMeasurement());
        // System.out.println("Elevator::error       = " + PID.getPIDController().getPositionError());
        // System.out.println("Elevator::setpoint    = " + PID.getPIDController().getSetpoint());
        // System.out.println("Elevator::speed       = " + PID.getSpeed());
        // System.out.println("Elevator::position    = " + encoder.getPosition());
        // System.out.println("Elevator::raw_speed   = " + PID.getPIDController().calculate(PID.getMeasurement()));
        // System.out.println("Elevator::pid_on = " + PID.active());
        // System.out.println("Stinger::pid_on  = " + ChargedUp.stinger.PID.active());
        // shouldStop = false;
        if(shouldStop) motor.set(0);
        else           motor.set(getModifiedSpeed(PID.getSpeed()) + ff);
        // Logging.info("" + motor.get());


        if(RobotBase.isSimulation())
        {
            SimulationUtility.simulateNEO(motor, encoder);
        }

        ligament.setLength(encoder.getPosition());
    }
    
    @Override
    public void reset() 
    {
        stop();
    }
}
