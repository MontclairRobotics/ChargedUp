package frc.robot.framework.color;

import org.team555_deprecated.math.MathUtils;

public class RGB implements RawColor
{
    public final int r;
    public final int g;
    public final int b;

    RGB(int r, int g, int b)
    {
        this.r = MathUtils.clamp(r, 0, 255);
        this.g = MathUtils.clamp(g, 0, 255);
        this.b = MathUtils.clamp(b, 0, 255);
    }

    public RGB rgb() {return this;}
    public HSV hsv()
    {
        final var vclh = RawColor.vclhFrom(r, g, b);
        final int v = vclh[0], c = vclh[1], l = vclh[2], h = vclh[3];

        final var s = (v == 0 ? 0 : c * 255 / v);

        return new HSV(h, s, v);
    }
    public HSL hsl()
    {
        final var vclh = RawColor.vclhFrom(r, g, b);
        final int v = vclh[0], c = vclh[1], l = vclh[2], h = vclh[3];

        final var s = (l == 0 || l == 1) ? 0 : c / (1 - Math.abs(2*v - c - 1));

        return new HSL(h, s, l);
    }
}
