package org.team555_deprecated.frc.devices;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.DigitalInput;

public abstract class LimitSwitch 
{
    private boolean isInverted;

    public LimitSwitch(boolean isInverted)
    {
        this.isInverted = isInverted;
    }

    public boolean isInverted()
    {
        return isInverted;
    }
    public void setInverted(boolean isInverted)
    {
        this.isInverted = isInverted;
    }

    public boolean get()
    {
        return getRaw() ^ isInverted;
    }

    protected abstract boolean getRaw();
}
