package org.team555_deprecated.frc.vendors.blinkinled;

import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class BlinkinLEDDriver 
{
    private Spark inner;
    private BlinkinLEDMode defaultMode;

    public BlinkinLEDDriver(int index, BlinkinLEDMode defaultMode)
    {
        inner = new Spark(index);
        inner.enableDeadbandElimination(false);

        this.defaultMode = defaultMode;

        set(defaultMode);
    }

    public void set(BlinkinLEDMode mode)
    {
        inner.set(mode.getSparkValue());
    }
    public void returnToDefault()
    {        
        set(defaultMode);
    }
}
