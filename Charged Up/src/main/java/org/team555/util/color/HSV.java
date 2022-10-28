package org.team555.util.color;

import static org.team555.util.ArrConstruct.*;

import org.team555.math.MathUtils;

public class HSV implements RawColor 
{
    public final int h;
    public final int s;
    public final int v;

    HSV(int h, int s, int v)
    {
        this.h = h % 360;
        this.s = MathUtils.clamp(s, 0, 255);
        this.v = MathUtils.clamp(v, 0, 255);
    }

    public RGB rgb()
    {
        final var c = v / 255.0 * s / 255.0;
        final var H = h / 60.0;
        final var x = c * (1 - Math.abs(H % 2  - 1));
        final var m = v - c;

        var r1g1b1 = RawColor.r1g1b1From(H, c, x, m);

        return new RGB(
            (int)r1g1b1[0],
            (int)r1g1b1[1],
            (int)r1g1b1[2]
        );
    }
    public HSV hsv() {return this;}
    public HSL hsl()
    {
        var l = v * (1 - s / 2);
        return new HSL(
            h,
            (v == 0 ? 0 : (v - l) / (Math.min(l, 1 - l))),
            l
        );
    }
}
