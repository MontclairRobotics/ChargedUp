package org.team555.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;

import static org.team555.constants.ShwooperConstants.*;

import org.team555.ChargedUp;
import org.team555.constants.Ports;
import org.team555.util.frc.EdgeDetectFilter;
import org.team555.util.frc.EdgeDetectFilter.EdgeType;
import org.team555.util.frc.can.CANSafety;
import org.team555.util.frc.commandrobot.ManagerSubsystemBase;



public class Shwooper extends ManagerSubsystemBase
{
    private final Debouncer hasObjectRisingDebouncer = new Debouncer(0.1, DebounceType.kRising);
    private final Debouncer hasObjectFallingDebouncer = new Debouncer(1.0, DebounceType.kFalling);
    private final EdgeDetectFilter edgeFilter = new EdgeDetectFilter(EdgeType.RISING);

    private boolean lastFrameHasObject;
    
    private final CANSparkMax motorTop = new CANSparkMax(Ports.SHWOOPER_LEFT_MOTOR_PORT, MotorType.kBrushless);
    // private final CANSparkMax motorBottom = new CANSparkMax(Robot.Shwooper.RIGHT_MOTOR_PORT, MotorType.kBrushless);

    // private final MotorControllerGroup motors = new MotorControllerGroup(new MotorController[] {
    //     motorTop,
    //     motorBottom,
    // });
    
    public Shwooper() 
    {
        motorTop.setInverted(TOP_OR_LEFT_INVERSION);
        CANSafety.monitor(motorTop);
        // motorBottom.setInverted(Robot.Shwooper.RIGHT_INVERSION);
    }

    /**
     * Intake starts sucking
     */
    public void suck() 
    {
        //This could be opposite
        motorTop.set(SUCK_SPEED);
        // motorBottom.set(Robot.Shwooper.INVERT_SIMPLE_SCHWOOPER * -Robot.Shwooper.SPEED);
    }

    /**
     * Expel game pieces from intake
     */
    public void spit() 
    {
        motorTop.set(SPIT_SPEED);
        // motorBottom.set(Robot.Shwooper.INVERT_SIMPLE_SCHWOOPER * Robot.Shwooper.SPEED);
    }

    /**
     * Stops intake motors
     */
    public void stop() 
    {
        motorTop.set(0);
    }

    public double getCurrent()
    {
        return motorTop.getOutputCurrent();
    }

    public boolean hasObject()
    {
        return lastFrameHasObject;
    }

    @Override
    public void always() 
    {
        lastFrameHasObject = hasObjectRisingDebouncer.calculate(getCurrent() > CUBE_CURRENT);    
        lastFrameHasObject = hasObjectFallingDebouncer.calculate(lastFrameHasObject);

        if(edgeFilter.calculate(lastFrameHasObject))
            ChargedUp.led.celebrate();
    }

    @Override
    public void reset()
    {
        stop();
    }

    public String currentMode() 
    {
        if(motorTop.get() > 0)
        {
            return "SUCK";
        }
        else if(motorTop.get() < 0)
        {
            return "SPIT";
        }
        else
        {
            return "NONE";
        }
    }
 
}
