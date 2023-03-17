package org.team555.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import org.team555.math.Math555;

public class ZoomAnimation extends Animation
{
    public static final double WRAP_TIME = 0.5;
    private final Color col;

    public ZoomAnimation(double length, Color col) 
    {
        super(length);
        this.col = col;
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        final int offset = (int)(ledBuffer.getLength() * ((timeElapsed() / WRAP_TIME) % 1.0));

        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            final int x = (offset + i) % ledBuffer.getLength();
            final double t = i * 1.0 / ledBuffer.getLength();

            ledBuffer.setLED(x, Math555.lerp(Color.kBlack, col, t));
        }
    }
}
