package frc.robot;

public class Units555
{
    public static double mphToMps(double value)
    {
        return value * 0.44704;
    }
    public static double mpsToMph(double value)
    {
        return value / 0.44704;
    }

    public static double mphpsToMpsps(double value)
    {
        return value * 0.44704;
    }
    public static double mpspsToMphps(double value)
    {
        return value / 0.44704;
    }

    public static double lbFt2ToKgM2(double value)
    {
        return value * 0.0421401101;
    }
    public static double kgM2ToLbFt2(double value)
    {
        return value / 0.0421401101;
    }
}
