package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

/**
 * An animation which creates a cycling rainbow over the entirety of the LED strip.
 * Uses HSV Math in order to ensure that the 
 */
public class RainbowAnimation extends Animation
{
    public static final double CYCLE_TIME = 1.0;

    public RainbowAnimation(double length)
    {
        super(length);
    }
    
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        // Calculate strip offset
        final int offset = (int)(timer.get() / CYCLE_TIME * ledBuffer.getLength()) % ledBuffer.getLength();

        // For every pixel
        for (int i = 0; i < ledBuffer.getLength(); i++)
        {
            // Calculate the hue - hue is easier for rainbows because the color
            // shape is a circle so only one value needs to precess
            final int hue = (offset + (i * 180 / ledBuffer.getLength())) % 180;
            // Set the value
            ledBuffer.setHSV(i, hue, 255, 255);
        }
    }
}