package org.team555.util.color;

import org.team555.math.MathUtils;

public class HSL implements RawColor
{
    public final int h;
    public final int s;
    public final int l;

    HSL(int h, int s, int l)
    {
        this.h = h % 360;
        this.s = MathUtils.clamp(s, 0, 255);
        this.l = MathUtils.clamp(l, 0, 255);
    }

    public RGB rgb()
    {
        final var c = (1 - Math.abs(2 * l - 1)) * s;
        final var H = h / 60.0;
        final var x = c * (1 - Math.abs(H % 2  - 1));
        final var m = l - c / 2;

        var r1g1b1 = RawColor.r1g1b1From(H, c, x, m);

        return new RGB(
            (int)r1g1b1[0],
            (int)r1g1b1[1],
            (int)r1g1b1[2]
        );
    }
    public HSV hsv() 
    {
        var v = l + s * Math.min(l, 1 - l);
        return new HSV(
            h,
            (v == 0 ? 0 : 2 * ( 1- l / v )),
            v
        );
    }
    public HSL hsl() {return this;}
}
