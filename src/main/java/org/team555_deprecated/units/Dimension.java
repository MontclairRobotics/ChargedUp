package org.team555_deprecated.units;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.team555_deprecated.collections.CountBag;
import org.team555_deprecated.collections.DivCountBag;

/**
 * A class which represents a type of unit.
 * Examples of a Dimension include length and mass.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class Dimension
{
    private static final Map<String, String> registeredNames = new HashMap<>();

    public static final String DIMENSIONLESS_NAME = "1";

    /**
     * Get the name of this dimension. 
     * 
     * <p>
     * Will either be the same as the {@link #compositionalName} or will be
     * a name specified by a call to {@link #setName(String)}.
     * 
     * <p>
     * Note that all dimensions with the same makeup will have the same name, 
     * even if they are different objects.
     * 
     * @return the name of this dimension
     */
    public String name()
    {
        if(registeredNames.containsKey(compositionalName))
        {
            return registeredNames.get(compositionalName);
        }
        else
        {
            return compositionalName;
        }
    }

    /**
     * The compositional name of this dimension. Is equal to {@value #DIMENSIONLESS_NAME}
     * when the dimension represents the absence of a significant unit.
     * 
     * <p>
     * For instance, the dimension of {@link StandardUnits#radian} has a 
     * {@link #compositionalName} of {@value #DIMENSIONLESS_NAME}.
     * 
     * @apiNote This field is used to uniquely identify this dimension.
     */
    public final String compositionalName;
    /**
     * 
     */
    public final DivCountBag<String> composition;

    private Dimension(DivCountBag<String> composition)
    {
        this.composition = composition;
        this.compositionalName = composition.asDivString(Function.identity(), "1");
    }

    public boolean isCompatible(Dimension other) 
    {
        return compositionalName.equals(other.compositionalName);
    }

    public Dimension mul(Dimension other)
    {
        return new Dimension(composition.mul(other.composition));
    }
    public Dimension per(Dimension other)
    {
        return new Dimension(composition.div(other.composition));
    }
    public Dimension pow(int exp)
    {
        return new Dimension(composition.pow(exp));
    }
    public Dimension squared()
    {
        return this.pow(2);
    }
    public Dimension cubed()
    {
        return this.pow(3);
    }
    public Dimension inverse()
    {
        return dimensionless.per(this);
    }

    public void setName(String name)
    {
        registeredNames.put(compositionalName, name);
    }
    public void resetName()
    {
        registeredNames.remove(compositionalName);
    }

    public static Dimension named(String name)
    {
        var d = new Dimension(DivCountBag.of(CountBag.of(name, 1), CountBag.of()));
        d.setName(name);
        return d;
    }
    public static final Dimension dimensionless = new Dimension(DivCountBag.of());

    public String toString()
    {
        return name();
    }
}
