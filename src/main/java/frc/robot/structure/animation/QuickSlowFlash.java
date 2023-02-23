package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.framework.Math555;

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

    public static int repeatingCycle(double value, double minimum, double maximum, int cycles, int cycleLength)
    {
        return (int)(Math555.invlerp(value, minimum, maximum) * cycles * cycleLength) % cycleLength;
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        double percentFinished = percentFinished();
        Color color = flashColor;

        if (percentFinished < proportionQuick)
        {
            int state = repeatingCycle(percentFinished, 0, proportionQuick, 2, 200000);
            color = Math555.lerp(Color.kBlack, flashColor, state / 200000.0);
        }
        else 
        {
            int state = repeatingCycle(percentFinished, proportionQuick, 1, 2, 200000);
            color = Math555.lerp(Color.kBlack, flashColor, state / 200000.0);
        }

        LEDBuffer.fill(ledBuffer, color);
    }
    
}