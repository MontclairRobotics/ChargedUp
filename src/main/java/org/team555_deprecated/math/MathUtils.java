package org.team555_deprecated.math;

public final class MathUtils
{
    private MathUtils() {}

    public static final double STD_EPSILON = 1E-7;

    public static boolean approximately(double x, double y, double epsilon)
    {
        return Math.abs(x - y) <= epsilon;
    }
    public static boolean notApproximately(double x, double y, double epsilon)
    {
        return Math.abs(x - y) > epsilon;
    }

    public static boolean approximately(double x, double y)
    {
        return approximately(x, y, STD_EPSILON);
    }
    public static boolean notApproximately(double x, double y)
    {
        return notApproximately(x, y, STD_EPSILON);
    }

    public static boolean inRange(double x, double min, double max)
    {
        return (min < x) && (x < max);
    }
    public static boolean inRangeInclusive(double x, double min, double max)
    {
        return (min <= x) && (x <= max);
    }

    public static boolean inRadius(double x, double center, double radius)
    {
        return (center - radius < x) && (x < center + radius);
    }

    public static Sign signof(double x)
    {
        if(x == 0) return Sign.ZERO;
        else if(Double.isNaN(x)) return Sign.UNDEFINED;
        else if(x < 0) return Sign.MINUS;
        else return Sign.PLUS;
    }

    public static boolean signsDoNotMatch(double x, double y)
    {
        return (x >= 0) != (y >= 0);
    }

    public static boolean signsMatch(double x, double y)
    {
        return (x >= 0) == (y >= 0);
    }

    public static double signFromBoolean(boolean x)
    {
        return x ? 1 : -1;
    }

    /**
     * Returns abs(x)^y * sign(x).
     * Generally used to raise an input to a power and preserve the sign.
     */
    public static double powsign(double x, double y)
    {
        return signof(x).value * Math.pow(Math.abs(x), y);
    }

    public static double average(double... xs)
    {
        return sum(xs) / xs.length;
    }
    public static double sum(double... xs)
    {
        var tot = 0.0;
        for(var x : xs)
        {
            tot += x;
        }
        return tot;
    }

    public static double max(double... xs)
    {
        var tot = Double.MIN_VALUE;
        for(var x : xs)
        {
            tot = Math.max(tot, x);
        }
        return tot;
    }
    public static double min(double... xs)
    {
        var tot = Double.MAX_VALUE;
        for(var x : xs)
        {
            tot = Math.min(tot, x);
        }
        return tot;
    }

    public static int max(int... xs)
    {
        var tot = Integer.MIN_VALUE;
        for(var x : xs)
        {
            tot = Math.max(tot, x);
        }
        return tot;
    }
    public static int min(int... xs)
    {
        var tot = Integer.MAX_VALUE;
        for(var x : xs)
        {
            tot = Math.min(tot, x);
        }
        return tot;
    }

    public static int average(int... xs)
    {
        return sum(xs) / xs.length;
    }
    public static int sum(int... xs)
    {
        var tot = 0;
        for(var x : xs)
        {
            tot += x;
        }
        return tot;
    }

    public static double lerp(double a, double b, double t)
    {
        return a + t * (b - a);
    }
    public static double invlerp(double a, double b, double v)
    {
        return (v - a) / (v - b);
    }

    public static double clamp(double value, double min, double max)
    {
        return value < min ? min : (value > max ? max : value);
    }
    public static int clamp(int value, int min, int max)
    {
        return value < min ? min : (value > max ? max : value);
    }
}
