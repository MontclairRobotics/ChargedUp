package frc.robot.util.frc;

import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDevice.Direction;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;

public class LimitSwitch implements Sendable
{
    private final boolean invert;
    public final DigitalInput dio;
    private final SimDevice sim;
    private final SimBoolean bool;

    public LimitSwitch(int channel, boolean invert)
    {
        this.invert = invert;
        dio = new DigitalInput(channel);

        if(RobotBase.isSimulation())
        {
            sim = SimDevice.create("Limit Switch["+channel+"]");
            bool = sim.createBoolean("value", Direction.kOutput, false);
            dio.setSimDevice(sim);
        }
        else 
        {
            sim = null;
            bool = null;
        }
    }

    public boolean get() 
    {
        if(RobotBase.isReal()) return dio.get() ^ invert;
        else                   return bool.get() ^ invert;
    }
    public int getChannel() {return dio.getChannel();}

    /**
     * Set the simulated value of this limit switch.
     * Reports an error if called with a non-simulation robot.
     */
    public void set(boolean value)
    {
        if(RobotBase.isReal())
        {
            Logging.error("Cannot set the value of a digital limit switch when not in simulation mode!");
            return;
        }

        bool.set(value ^ invert);
    }

    @Override
    public void initSendable(SendableBuilder builder) {dio.initSendable(builder);}
}
