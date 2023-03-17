package org.team555.util;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class LazyDouble extends Lazy<Double> implements DoubleSupplier
{

    public LazyDouble(Supplier<Double> f) 
    {
        super(f);
    }

    @Override
    public double getAsDouble() 
    {
        return get();
    }
    
}
