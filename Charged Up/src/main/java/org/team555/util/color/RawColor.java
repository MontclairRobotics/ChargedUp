package org.team555.util.color;

import org.team555.math.MathUtils;
import static org.team555.util.ArrConstruct.*;

public interface RawColor 
{
    RGB rgb();
    HSV hsv();
    HSL hsl();

    default RawColor blendAdd(RawColor other)
    {
        var a = this.rgb();
        var b = other.rgb();

        return new RGB(
            a.r + b.r,
            a.b + b.b,
            a.g + b.g
        );
    }
    default RawColor blendSubtract(RawColor other)
    {
        var a = this.rgb();
        var b = other.rgb();

        return new RGB(
            a.r - b.r,
            a.b - b.b,
            a.g - b.g
        );
    }

    default RawColor rgbLerp(RawColor other, double t)
    {
        var a = this.rgb();
        var b = other.rgb();

        return new RGB(
            (int)MathUtils.lerp(a.r, b.r, t),
            (int)MathUtils.lerp(a.g, b.g, t),
            (int)MathUtils.lerp(a.b, b.b, t)
        );
    }
    default RawColor hsvLerp(RawColor other, double t)
    {
        var a = this.hsv();
        var b = other.hsv();

        return new HSV(
            (int)MathUtils.lerp(a.h, b.h, t),
            (int)MathUtils.lerp(a.s, b.s, t),
            (int)MathUtils.lerp(a.v, b.v, t)
        );
    }
    default RawColor hslLerp(RawColor other, double t)
    {
        var a = this.hsl();
        var b = other.hsl();

        return new HSL(
            (int)MathUtils.lerp(a.h, b.h, t),
            (int)MathUtils.lerp(a.s, b.s, t),
            (int)MathUtils.lerp(a.l, b.l, t)
        );
    }

    static int[] r1g1b1From(double H, double c, double x, double m)
    {
        var r1g1b1 = arr(0.0, 0.0, 0.0);

        if(0 <= H && H < 1)
        {
            r1g1b1 = arr(c, x, 0);
        }
        else if(1 <= H && H < 2)
        {
            r1g1b1 = arr(x, c, 0);
        }
        else if(2 <= H && H < 3)
        {
            r1g1b1 = arr(0, c, x);
        }
        else if(3 <= H && H < 4)
        {
            r1g1b1 = arr(0, x, c);
        }
        else if(4 <= H && H < 5)
        {
            r1g1b1 = arr(x, 0, c);
        }
        else if(5 <= H && H < 6)
        {
            r1g1b1 = arr(c, 0, x);
        }

        return arr(
            (int)((r1g1b1[0] + m) * 255.0),
            (int)((r1g1b1[1] + m) * 255.0),
            (int)((r1g1b1[2] + m) * 255.0)
        );
    }
    static int[] vclhFrom(int r, int g, int b)
    {        
        final var max = MathUtils.max(r, g, b);
        final var min = MathUtils.min(r, g, b);

        final var v = max;
        final var c = max - min;
        final var l = v - c / 2;

        int h;

        if(c == 0)
        {
            h = 0;
        }
        else if(v == r)
        {
            h = 60 * (0 + (g - b) / c);
        }
        else if(v == g) 
        {
            h = 60 * (2 + (b - r) / c);
        }
        else //if(v == b)
        {
            h = 60 * (4 + (r - g) / c);
        }

        return new int[] {v, c, l, h};
    }

    public static RawColor rgb(int r, int g, int b)
    {
        return new RGB(r, g, b);
    }
    public static RawColor hsv(int h, int s, int v)
    {
        return new HSV(h, s, v);
    }
    public static RawColor hsl(int h, int s, int l)
    {
        return new HSL(h, s, l);
    }
}
