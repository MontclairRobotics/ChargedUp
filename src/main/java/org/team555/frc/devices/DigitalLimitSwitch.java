package org.team555.frc.devices;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalLimitSwitch extends LimitSwitch 
{
    private final DigitalInput di;

    public DigitalLimitSwitch(DigitalInput input, boolean isInverted)
    {
        super(isInverted);

        di = input;
    }
    public DigitalLimitSwitch(DigitalInput input)
    {
        this(input, false);
    }

    @Override
    protected boolean getRaw() 
    {
        return di.get();
    }
}
