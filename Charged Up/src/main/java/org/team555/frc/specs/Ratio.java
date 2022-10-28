package org.team555.frc.specs;

public class Ratio 
{
    public final double in;
    public final double out;
    public final double inToOut;

    private Ratio(double in, double out)
    {
        if(in == 0)
        {
            throw new IllegalArgumentException("Ratio input cannot be zero.");
        }
        if(out == 0)
        {
            throw new IllegalArgumentException("Ratio output cannot be zero.");
        }

        this.in = in;
        this.out = out;

        inToOut = in / out;
    }

    public Ratio into(Ratio other)
    {
        return new Ratio(in, out / other.inToOut);
    }
    public Ratio inverse()
    {
        return new Ratio(out, in);
    }

    public static Ratio of(double in, double out)
    {
        return new Ratio(in, out);
    }
    public static final Ratio one = of(1, 1);

    @Override
    public String toString() 
    {
        return in + ":" + out;
    }
}
