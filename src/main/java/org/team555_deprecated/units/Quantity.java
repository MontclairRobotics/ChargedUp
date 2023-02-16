package org.team555_deprecated.units;

public class Quantity
{
    public final double rawValue;
    public final Unit unit;

    private Quantity(double value, Unit unit)
    {
        this.rawValue = value;
        this.unit = unit;
    }

    public static Quantity of(double value, Unit unit)
    {
        return new Quantity(value, unit);
    }
    public static Quantity of(double value1, Unit unit1, double value2, Unit unit2)
    {
        return Quantity.of(value1, unit1).plus(Quantity.of(value2, unit2));
    }
    public static Quantity of(double value1, Unit unit1, double value2, Unit unit2, double value3, Unit unit3)
    {
        return Quantity.of(value1, unit1).plus(Quantity.of(value2, unit2)).plus(Quantity.of(value3, unit3));
    }

    public Quantity as(Unit unit)
    {
        if(!unit.dim.isCompatible(this.unit.dim))
        {
            throw new IllegalArgumentException("Cannot convert from " + this.unit.pluralName() + " to " + unit.pluralName() + ".");
        }
        return new Quantity(rawValue / this.unit.value * unit.value, unit);
    }
    public Quantity asIf(Unit unit)
    {
        return new Quantity(rawValue, unit);
    }

    public Quantity plus(Quantity other)
    {
        if(!unit.dim.isCompatible(other.unit.dim))
        {
            throw new IllegalArgumentException("Cannot add " + other.unit.pluralName() + " to " + unit.pluralName() + ".");
        }
        return new Quantity(rawValue + other.value(unit), unit);
    }
    public Quantity plus(double other, Unit otherUnit)
    {
        return plus(Quantity.of(other, otherUnit));
    }
    public Quantity minus(Quantity other)
    {
        if(!unit.dim.isCompatible(other.unit.dim))
        {
            throw new IllegalArgumentException("Cannot subtract " + other.unit.pluralName() + " from " + unit.pluralName() + ".");
        }
        return new Quantity(rawValue - other.value(unit), unit);
    }
    public Quantity minus(double other, Unit otherUnit)
    {
        return minus(Quantity.of(other, otherUnit));
    }

    public Quantity mul(Quantity other)
    {
        return new Quantity(rawValue * other.rawValue, unit.mul(other.unit));
    }
    public Quantity mul(double other)
    {
        return new Quantity(rawValue * other, unit);
    }
    public Quantity mul(double other, Unit otherUnit)
    {
        return mul(Quantity.of(other, otherUnit));
    }

    public Quantity div(Quantity other)
    {
        return new Quantity(rawValue / other.rawValue, unit.per(other.unit));
    }
    public Quantity div(double other)
    {
        return new Quantity(rawValue / other, unit);
    }
    public Quantity div(double other, Unit otherUnit)
    {
        return div(Quantity.of(other, otherUnit));
    }

    public Quantity pow(int n)
    {
        return new Quantity(Math.pow(rawValue, n), unit.pow(n));
    }
    public Quantity squared()
    {
        return pow(2);
    }
    public Quantity cubed()
    {
        return pow(3);
    }

    public Quantity require(Dimension dim, String valueName)
    {
        if(!unit.dim.isCompatible(dim))
        {
            throw new IllegalArgumentException("Value " + valueName + " cannot be in " + unit.pluralName() + ". The unit must be of dimension " + dim + ".");
        }

        return this;
    }
    public Quantity require(Dimension dim)
    {
        if(!unit.dim.isCompatible(dim))
        {
            throw new IllegalArgumentException("Value cannot be in " + unit.pluralName() + ". The unit must be of dimension " + dim + ".");
        }

        return this;
    }
    public Quantity require(Unit unit, String valueName)
    {
        if(!this.unit.identicalTo(unit))
        {
            throw new IllegalArgumentException("Value " + valueName + " cannot be in " + this.unit.pluralName() + ". The unit must be " + unit.pluralName() + ".");
        }

        return this;
    }
    public Quantity require(Unit unit)
    {
        if(!this.unit.identicalTo(unit))
        {
            throw new IllegalArgumentException("Value cannot be in " + this.unit.pluralName() + ". The unit must be " + unit.pluralName() + ".");
        }

        return this;
    }

    public double value(Unit unit)
    {
        return as(unit).rawValue;
    }

    @Override
    public String toString() 
    {
        if(rawValue == 1 || rawValue == -1)
        {
            return rawValue + " " + unit.name();
        }
        else
        {
            return rawValue + " " + unit.pluralName();
        }
    }
}
