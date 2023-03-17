package org.team555.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class CircusAnimation extends Animation
{
    public static final double SHIFT_SPEED = 0.1;
    private int offset = 0;

    public CircusAnimation(double length) 
    {
        super(length);
    }
    public void run(AddressableLEDBuffer ledBuffer)
    {
        offset = (int)(timeElapsed() / SHIFT_SPEED);

        for(int i = 0; i < ledBuffer.getLength(); i++) 
        {
            if ((i + offset) % 3 == 0) 
            {
                ledBuffer.setLED(i, Color.kBlack);
            } 
            else 
            {
                ledBuffer.setLED(i, Color.kOrange);
            }
        }
    }
}
