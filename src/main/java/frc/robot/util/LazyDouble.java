package frc.robot.util;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.networktables.DoubleArrayEntry;

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
