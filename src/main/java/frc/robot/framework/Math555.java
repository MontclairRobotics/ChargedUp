package frc.robot.framework;

import java.util.Arrays;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

public class Math555 
{
    private Math555() {}

    public static double clamp(double v, double min, double max)
    {
        if(v < min) return min;
        if(v > max) return max;

        return v;
    }
    public static int clamp(int v, int min, int max)
    {
        if(v < min) return min;
        if(v > max) return max;

        return v;
    }

    public static double lerp(double a, double b, double t)
    {
        return t*(b-a)+a;
    }
    public static int lerp(int a, int b, double t)
    {
        return (int)(t*(b-a))+a;
    }

    public static Color lerp(Color a, Color b, double t) 
    {
        return new Color(lerp(a.red, b.red, t), lerp(a.green, b.green, t), lerp(a.blue, b.blue, t));
    }
    public static Color8Bit lerp(Color8Bit a, Color8Bit b, double t) 
    {
        return new Color8Bit(lerp(a.red, b.red, t), lerp(a.green, b.green, t), lerp(a.blue, b.blue, t));
    }

    public static double max(double... ds)
    {
        return Arrays.stream(ds)
            .max()
            .orElse(Double.NaN);
    }
    public static double min(double... ds)
    {
        return Arrays.stream(ds)
            .min()
            .orElse(Double.NaN);
    }
    
    public static int max(int... ds)
    {
        return Arrays.stream(ds)
            .max()
            .orElse(Integer.MAX_VALUE);
    }
    public static int min(int... ds)
    {
        return Arrays.stream(ds)
            .min()
            .orElse(Integer.MAX_VALUE);
    }

    public static double signpow(double v, double pow)
    {
        if(v > 0) return +Math.pow(+v, pow);
        else      return -Math.pow(-v, pow);
    }
}
