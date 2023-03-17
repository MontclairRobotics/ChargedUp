package org.team555.animation;

import java.util.Random;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class DeathAnimation extends Animation
{
    public DeathAnimation(double length) 
    {
        super(length);
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            Random random = new Random();
            ledBuffer.setHSV(i, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
    }
}
