package org.team555_deprecated.units;

import java.util.HashMap;
import java.util.Map;

import org.team555_deprecated.collections.CountBag;
import org.team555_deprecated.collections.DivCountBag;

/**
 * The unit class represents a unit of measurement.
 * 
 * Units are considered identical if they have the same underlying structure.
 * This means that two units with the same name are considered identical.
 * 
 * Units may be freely converted from one to another if they have the same dimensions.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class Unit
{
    /**
     * The registered names of units.
     * Keys of this map are the compositions of units, represented in verbose string form.
     * Values of this map are the names of the units.
     */
    private static final Map<String, Naming> registeredNames = new HashMap<>();

    /**
     * Gets the naming scheme for this unit.
     */
    public Naming naming()
    {
        if(registeredNames.containsKey(compositionalName()))
        {
            return registeredNames.get(compositionalName());
        }
        else
        {
            return compositionalNaming;
        }
    }
    /**
     * Gets the naming scheme representing the composition of this unit.
     * For example, {@code StandardUnits.newton.compositionalNaming().name} is equal to {@code "kilogram meter / second^2"}
     */
    public Naming compositionalNaming()
    {
        return compositionalNaming;
    }

    public String compositionalName() {return compositionalNaming.name;}
    public String compositionalPluralName() {return compositionalNaming.pluralName;}
    public String compositionalSymbol() {return compositionalNaming.symbol;}

    public String name() { return naming().name; }
    public String pluralName() { return naming().pluralName; }
    public String symbol() { return naming().symbol; }

    public final double value;
    public final Dimension dim;

    public final DivCountBag<Unit> composition;

    private final Naming compositionalNaming;
    
    Unit(double value, Dimension dim, DivCountBag<Unit> subUnits)
    {
        this.value = value;
        this.dim = dim;
        this.composition = subUnits;

        this.compositionalNaming = Naming.of(
            composition.asDivString(u -> u == this ? "[unresolved]" : u.name(), ""), 
            composition.asDivString(u -> u == this ? "[unresolved]" : u.pluralName(), u -> u == this ? "[unresolved]" : u.name(), ""),
            composition.asDivString(u -> u == this ? "[unresolved]" : u.symbol(), "")
        );
    }
    Unit(double value, Dimension dim, Naming naming)
    {
        this.value = value;
        this.dim = dim;
        this.composition = DivCountBag.of(CountBag.of(this, 1), CountBag.of());

        this.compositionalNaming = naming;
    }

    public Unit mul(Unit other)
    {
        return new Unit(value * other.value, dim.mul(other.dim), composition.mul(other.composition));
    }
    public Unit per(Unit other)
    {
        return new Unit(value / other.value, dim.per(other.dim), composition.div(other.composition));
    }
    public Unit pow(int n)
    {
        return new Unit(Math.pow(value, n), dim.pow(n), composition.pow(n));
    }
    public Unit squared()
    {
        return this.pow(2);
    }
    public Unit cubed()
    {
        return this.pow(3);
    }
    public Unit inverse()
    {
        return unitless.per(this);
    }

    public void setNaming(Naming naming)
    {
        registeredNames.put(compositionalName(), naming);
    }
    public void resetName()
    {
        registeredNames.remove(compositionalName());
    }

    public static Unit from(Naming naming, double value, Dimension dim)
    {
        return new Unit(value, dim, naming);
    }
    public static Unit from(String name, String pluralName, String symbol, double value, Dimension dim)
    {
        return new Unit(value, dim, Naming.of(name, pluralName, symbol));
    }
    public static Unit from(String name, String symbol, double value, Dimension dim)
    {
        return new Unit(value, dim, Naming.of(name, symbol));
    }
    public static Unit from(String name, double value, Dimension dim)
    {
        return new Unit(value, dim, Naming.of(name));
    }
    public static Unit from(Naming naming, double value)
    {
        return new Unit(value, Dimension.dimensionless, naming);
    }
    public static Unit from(String name, String pluralName, String symbol, double value)
    {
        return new Unit(value, Dimension.dimensionless, Naming.of(name, pluralName, symbol));
    }
    public static Unit from(String name, String symbol, double value)
    {
        return new Unit(value, Dimension.dimensionless, Naming.of(name, symbol));
    }
    public static Unit from(String name, double value)
    {
        return new Unit(value, Dimension.dimensionless, Naming.of(name));
    }

    @Override
    public String toString() 
    {
        return name();
    }

    public boolean identicalTo(Unit other)
    {
        return compositionalName().equals(other.compositionalName());
    }

    public static final Unit unitless = from("none", "", "", 1, Dimension.dimensionless);
}