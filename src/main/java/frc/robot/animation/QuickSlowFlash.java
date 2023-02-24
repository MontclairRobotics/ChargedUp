package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.math.Math555;

public class QuickSlowFlash extends Animation
{
    private Color flashColor;
    private double proportionQuick;

    public QuickSlowFlash(Color color)
    {
        super(5);
        flashColor = color;
        proportionQuick = 0.33;
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