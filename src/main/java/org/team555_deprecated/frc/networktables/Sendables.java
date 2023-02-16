package org.team555_deprecated.frc.networktables;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public final class Sendables 
{
    private Sendables() {}

    public static <T extends Enum<T>> SendableChooser<T> enumChooser(T defaultValue, Supplier<T[]> enumValues)
    {
        var s = new SendableChooser<T>();

        s.setDefaultOption(defaultValue.name(), defaultValue);

        for(var e : enumValues.get())
        {
            s.addOption(e.name(), e);
        }

        return s;
    }
    @SafeVarargs
    public static <T> SendableChooser<T> chooser(T defaultValue, T... values)
    {
        var s = new SendableChooser<T>();

        s.setDefaultOption(defaultValue.toString(), defaultValue);

        for(var e : values)
        {
            s.addOption(e.toString(), e);
        }

        return s;
    }
}
