package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public abstract class Transition extends Animation
{
    public Transition(double length, AddressableLEDBuffer old, AddressableLEDBuffer newb) 
    {
        super(length);

        oldBuffer = old;
        newBuffer = newb;
    }
    
    protected final AddressableLEDBuffer oldBuffer;
    protected final AddressableLEDBuffer newBuffer;
}
