package org.team555.units;

/**
 * A class that stores the information surrounding the naming of a Unit,
 * consisting of its singular, plural, and abbreviated names.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class Naming 
{
    public final String name;
    public final String pluralName;
    public final String symbol;

    public Naming(String name, String pluralName, String symbol)
    {
        this.name = name;
        this.pluralName = pluralName;
        this.symbol = symbol;
    }

    /**
     * Create a new Naming object from the given information.
     * @param name : the singular name
     * @param pluralName : the plural name
     * @param symbol : the abbreviated name
     */
    public static Naming of(String name, String pluralName, String symbol)
    {
        return new Naming(name, pluralName, symbol);
    }
    /**
     * Create a new Naming object from the given information.
     * The plural name defaults to the provided name with a postpended s.
     * @param name : the singular name
     * @param symbol : the abbreviated name
     */
    public static Naming of(String name, String symbol)
    {
        return new Naming(name, name + "s", symbol);
    }
    /**
     * Create a new Naming object from the given information.
     * The plural name defaults to the provided name with a postpended s.
     * The abbreviated name defaults to the provided name.
     * @param name : the singular name
     */
    public static Naming of(String name)
    {
        return new Naming(name, name + "s", name);
    }
}
