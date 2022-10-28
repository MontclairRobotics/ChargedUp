package org.team555.math.easing;

import org.team555.math.MathUtils;

@FunctionalInterface
public interface Easer 
{
    double ease(double t);
    default double ease(double a, double b, double t)
    {
        return MathUtils.lerp(a, b, ease(t));
    }
}
