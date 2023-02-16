package frc.robot.framework;

import edu.wpi.first.math.MathUtil;

public class Math555 
{
    private Math555() {}

    public static double clamp(double v, double min, double max)
    {
        if(v < min) return min;
        if(v > max) return max;

        return v;
    }

    public static double signpow(double v, double pow)
    {
        if(v > 0) return +Math.pow(+v, pow);
        else      return -Math.pow(-v, pow);
    }
}
