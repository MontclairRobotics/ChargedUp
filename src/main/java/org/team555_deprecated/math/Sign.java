package org.team555_deprecated.math;

public enum Sign 
{
    PLUS(1),
    MINUS(-1),
    ZERO(0),
    UNDEFINED(Double.NaN)
    ;

    public final double value;
    private Sign(double sign)
    {
        this.value = sign;
    }
}
