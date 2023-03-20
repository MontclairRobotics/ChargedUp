package org.team555.animation2;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

import org.team555.animation2.api.AnimationBase;
import org.team555.animation2.api.LEDBuffer;
import org.team555.math.Math555;

public class FlashAnimation extends AnimationBase
{
    private Color flashColor;

    public FlashAnimation(double length, Color color)
    {
        flashColor = color;
    }

    @Override
    public void render()
    {
        Color color = flashColor;
        double time = getTimeElapsed() % 2;

        int state = Math555.repeatingCycle(time, 0, 2, 2, 200);
        color = Math555.lerp(Color.kBlack, flashColor, state / 200.0);

        LEDBuffer.fill(getBuffer(), color);
    }
    
}