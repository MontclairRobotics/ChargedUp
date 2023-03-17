package org.team555.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import org.team555.math.Math555;

public class QuickSlowFlash extends Animation
{
    private Color flashColor;
    private double proportionQuick;

    public QuickSlowFlash(double length, Color color)
    {
        super(length);
        flashColor = color;
        proportionQuick = 0.33;
    }
    public QuickSlowFlash(Color color)
    {
        this(5, color);
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        double percentFinished = percentFinished();
        Color color = flashColor;

        if (percentFinished < proportionQuick)
        {
            int state = Math555.repeatingCycle(percentFinished, 0, proportionQuick, 2, 200);
            color = Math555.lerp(Color.kBlack, flashColor, state / 200.0);
        }
        else 
        {
            int state = Math555.repeatingCycle(percentFinished, proportionQuick, 1, 2, 200);
            color = Math555.lerp(Color.kBlack, flashColor, state / 200.0);
        }

        LEDBuffer.fill(ledBuffer, color);
    }
    
}