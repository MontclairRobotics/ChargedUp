package org.team555.util.frc;

import org.team555.util.Unreachable;

public class EdgeDetectFilter 
{
    public static enum EdgeType 
    {
        RISING, FALLING, EITHER;
    }

    private boolean last;
    private boolean current;

    private final EdgeType detectionType;

    public EdgeDetectFilter(EdgeType type)
    {
        detectionType = type;
    }

    public void reset() 
    {
        last = false;
        current = true;
    }

    public boolean isRising() {return current && !last;}
    public boolean isFalling() {return !current && last;}
    public boolean isEdge() {return current ^ last;}

    public boolean calculate(boolean value)
    {
        last = current;
        current = value;

        if(detectionType == EdgeType.RISING) return isRising();
        if(detectionType == EdgeType.FALLING) return isFalling();
        if(detectionType == EdgeType.EITHER) return isEdge();

        return Unreachable.here();
    }
}
