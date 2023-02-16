package org.team555_deprecated.units;

import static org.team555_deprecated.units.Dimension.dimensionless;

public final class StandardUnits 
{
    private StandardUnits() {}

    // DIMENSIONS //
    public static final Dimension length = Dimension.named("length");
    public static final Dimension mass = Dimension.named("mass");
    public static final Dimension time = Dimension.named("time");
    public static final Dimension storage = Dimension.named("storage");
    
    public static final Dimension area = length.squared();
    public static final Dimension volume = length.cubed();
    public static final Dimension velocity = length.per(time);
    public static final Dimension acceleration = length.per(time.pow(2));
    public static final Dimension force = mass.mul(acceleration);
    public static final Dimension pressure = force.per(area);
    public static final Dimension frequency = time.inverse();
    public static final Dimension energy = force.mul(length);
    public static final Dimension power = energy.per(time);
    public static final Dimension linearDensity = mass.per(length);
    public static final Dimension surfaceDensity = mass.per(length.squared());
    public static final Dimension density = mass.per(length.cubed());

    // PREFIXES //
    public static final Prefix nano = Prefix.from("nano", "n", 1e-9);
    public static final Prefix micro = Prefix.from("micro", "u", 1e-6);
    public static final Prefix milli = Prefix.from("milli", "m", 1 / 1000.0);
    public static final Prefix centi = Prefix.from("centi", "c", 1 / 100.0);
    public static final Prefix deci = Prefix.from("deci", "d", 1 / 10.0);
    public static final Prefix deca = Prefix.from("deca", "da", 10);
    public static final Prefix hecto = Prefix.from("hecto", "h", 100);
    public static final Prefix kilo = Prefix.from("kilo", "k", 1000);
    public static final Prefix mega = Prefix.from("mega", "M", 1000000);
    public static final Prefix giga = Prefix.from("giga", "G", 1000000000);
    public static final Prefix tera = Prefix.from("tera", "T", 1000000000000.0);

    // UNITS //
    public static final Unit meter = Unit.from("meter", "meters", "m", 1, length);
    public static final Unit centimeter = centi.of(meter);
    public static final Unit millimeter = milli.of(meter);
    public static final Unit kilometer = kilo.of(meter);
    public static final Unit foot = Unit.from("foot", "feet", "ft", 0.3048, length);
    public static final Unit inch = Unit.from("inch", "inches", "in", 0.0254, length);
    public static final Unit yard = Unit.from("yard", "yards", "yd", 0.9144, length);
    public static final Unit mile = Unit.from("mile", "miles", "mi", 1609.344, length);
    public static final Unit nauticalMile = Unit.from("nautical mile", "nautical miles", "nmi", 1852.0, length);
    public static final Unit lightYear = Unit.from("light year", "light years", "ly", 9.4607e15, length);
    public static final Unit parsec = Unit.from("parsec", "parsecs", "pc", 3.08567758e16, length);

    public static final Unit gram = Unit.from("gram", "grams", "g", 0.001, mass);
    public static final Unit kilogram = kilo.of(gram);
    public static final Unit pound = Unit.from("pound", "pounds", "lb", 0.45359237, mass);
    public static final Unit ounce = Unit.from("ounce", "ounces", "oz", 0.028349523125, mass);
    public static final Unit ton = Unit.from("ton", "tons", "ton", 907.18474, mass);
    public static final Unit metric_ton = Unit.from("metric ton", "metric tons", "ton", 1000, mass);
    public static final Unit short_ton = Unit.from("short ton", "short tons", "ton", 907.18474 * 2240, mass);
    public static final Unit long_ton = Unit.from("long ton", "long tons", "ton", 907.18474 * 2240 * 2, mass);

    public static final Unit second = Unit.from("second", "seconds", "s", 1, time);
    public static final Unit millisecond = milli.of(second);
    public static final Unit nanosecond = nano.of(second);
    public static final Unit minute = Unit.from("minute", "minutes", "min", 60, time);
    public static final Unit hour = Unit.from("hour", "hours", "h", 3600, time);
    public static final Unit day = Unit.from("day", "days", "d", 86400, time);
    public static final Unit week = Unit.from("week", "weeks", "wk", 604800, time);
    public static final Unit year = Unit.from("year", "years", "yr", 31556926, time);
    public static final Unit century = Unit.from("century", "centuries", "c", 31556926 * 100, time);
    public static final Unit millenium = Unit.from("millenium", "millenia", "m", 31556926 * 1000, time);

    public static final Unit ubyte = Unit.from("byte", "bytes", "B", 1, storage);
    public static final Unit kilobyte = kilo.of(ubyte);
    public static final Unit megabyte = mega.of(ubyte);
    public static final Unit gigabyte = giga.of(ubyte);
    public static final Unit terabyte = tera.of(ubyte);
    public static final Unit bit = Unit.from("bit", "bits", "b", 1 / 8.0, storage);
    public static final Unit kilobit = kilo.of(bit);
    public static final Unit megabit = mega.of(bit);
    public static final Unit gigabit = giga.of(bit);
    public static final Unit terabit = tera.of(bit);
    
    public static final Unit rotation = Unit.from("rotation", "rotations", "rot", 360, dimensionless);
    public static final Unit radian = Unit.from("radian", "radians", "rad", Math.PI / 180, dimensionless);
    public static final Unit degree = Unit.from("degree", "degrees", "deg", 1, dimensionless);

    public static final Unit percent = Unit.from("percent", "percent", "%", 0.01);
    public static final Unit permille = Unit.from("permille", "permille", "%.", 0.001);
    public static final Unit unit = Unit.from("unit", "unit", "", 1);

    public static final Unit netwon = kilogram.mul(meter).per(second.squared());
    public static final Unit hertz = second.inverse();
    public static final Unit joule = netwon.mul(meter);
    public static final Unit watt = joule.per(second);

    public static final Unit squareCentimeter = centimeter.squared();
    public static final Unit squareMillimeter = millimeter.squared();
    public static final Unit squareMeter = meter.squared();
    public static final Unit squareKilometer = kilometer.squared();
    public static final Unit squareFoot = foot.squared();
    public static final Unit squareInch = inch.squared();
    public static final Unit squareYard = yard.squared();
    public static final Unit squareMile = mile.squared();
    public static final Unit squareNauticalMile = nauticalMile.squared();
    public static final Unit squareLightYear = lightYear.squared();
    public static final Unit squareParsec = parsec.squared();

    public static final Unit cubicMeter = meter.cubed();
    public static final Unit cubicCentimeter = centimeter.cubed();
    public static final Unit cubicMillimeter = millimeter.cubed();
    public static final Unit cubicKilometer = kilometer.cubed();
    public static final Unit cubicFoot = foot.cubed();
    public static final Unit cubicInch = inch.cubed();
    public static final Unit cubicYard = yard.cubed();
    public static final Unit cubicMile = mile.cubed();
    public static final Unit cubicNauticalMile = nauticalMile.cubed();
    public static final Unit cubicLightYear = lightYear.cubed();
    public static final Unit cubicParsec = parsec.cubed();

    static
    {
        area.setName("area");
        volume.setName("volume");
        velocity.setName("velocity");
        acceleration.setName("acceleration");
        force.setName("force");
        pressure.setName("pressure");
        frequency.setName("frequency");
        energy.setName("energy");
        power.setName("power");
        linearDensity.setName("linear density");
        surfaceDensity.setName("surface density");
        density.setName("density");

        netwon.setNaming(Naming.of("newton", "newtons", "N"));
        hertz.setNaming(Naming.of("hertz", "hertz", "Hz"));
        joule.setNaming(Naming.of("joule", "joules", "J"));
        watt.setNaming(Naming.of("watt", "watts", "W"));
    }
}
