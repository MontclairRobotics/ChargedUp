package org.team555_deprecated.math.easing;

import org.team555_deprecated.math.MathUtils;

@FunctionalInterface
public interface Easer 
{
    double ease(double t);
    default double ease(double a, double b, double t)
    {
        return MathUtils.lerp(a, b, ease(t));
    }
}
