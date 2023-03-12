package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.math.Math555;

public class FlashAnimation extends Animation
{
    private Color flashColor;

    public FlashAnimation(double length, Color color)
    {
        super(length);
        flashColor = color;
    } 
    public FlashAnimation(double length)
    {
        this(5, Color.kDarkOrange);
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        Color color = flashColor;
        double time = timeElapsed() % 2;

        int state = Math555.repeatingCycle(time, 0, 2, 2, 200);
        color = Math555.lerp(Color.kBlack, flashColor, state / 200.0);

        LEDBuffer.fill(ledBuffer, color);
    }
    
}