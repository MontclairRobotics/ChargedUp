package org.team555.units;

public class Prefix 
{
    private final String name;
    private final String prefix;
    private final double modifier;

    public String name() { return name; }
    public String prefix() { return prefix; }
    public double modifier() { return modifier; }

    public Prefix(String name, String prefix, double modifier)
    {
        this.name = name;
        this.prefix = prefix;
        this.modifier = modifier;
    }

    public Unit of(Unit unit)
    {
        var u = Unit.from(
            name + unit.name(),
            name + unit.pluralName(), 
            prefix + unit.symbol(),
            unit.value * modifier, 
            unit.dim
        );
        return u;
    }

    public static Prefix from(String name, String prefix, double modifier)
    {
        return new Prefix(name, prefix, modifier);
    }
}
