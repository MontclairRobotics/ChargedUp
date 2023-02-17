package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public abstract class Transition extends Animation
{
    public Transition(double length) 
    {
        super(length);
    }
    
    protected AddressableLEDBuffer oldBuffer;
    protected AddressableLEDBuffer newBuffer;

    public void setOld(AddressableLEDBuffer buf)
    {
        oldBuffer = buf;
    }

    public void setNew(AddressableLEDBuffer buf)
    {
        newBuffer = buf;
    }
}
