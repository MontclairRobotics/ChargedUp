package org.team555.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import org.team555.math.Math555;

public class RaceAnimation extends Animation
{
    final Color bgCol;

    public RaceAnimation(double length, Color color) 
    {
        super(length);
        this.bgCol = color;
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        int n = Math555.repeatingCycle(percentFinished(), 0, 1, 1, ledBuffer.getLength());
        for (int i = 0; i < ledBuffer.getLength(); i++)
        {
            Color color;

            if (i >= n-1 && i <= n+1) color = Color.kAqua;
            else                      color = bgCol;

            ledBuffer.setLED(i, color);
        }
    }
       
}
