package org.team555.frc.devices;

import java.util.function.BooleanSupplier;

public class GenericLimitSwitch  extends LimitSwitch
{
    private final BooleanSupplier di;

    public GenericLimitSwitch(BooleanSupplier input, boolean isInverted)
    {
        super(isInverted);

        di = input;
    }
    public GenericLimitSwitch(BooleanSupplier input)
    {
        this(input, false);
    }

    @Override
    protected boolean getRaw() 
    {
        return di.getAsBoolean();
    }
}
