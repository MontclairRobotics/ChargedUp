package frc.robot.structure.helpers;

public class Units555
{
    public static double miphToMps(double value)
    {
        return value * 0.44704;
    }
    public static double mpsToMiph(double value)
    {
        return value / 0.44704;
    }

    public static double miphpsToMpsps(double value)
    {
        return value * 0.44704;
    }
    public static double mpspsToMiphps(double value)
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
