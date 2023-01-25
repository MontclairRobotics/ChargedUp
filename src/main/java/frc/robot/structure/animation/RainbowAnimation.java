package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.ChargedUp;
import frc.robot.structure.helpers.Logging;

public class RainbowAnimation extends Animation
{
    int rainbowFirstPixelHue = 0;

    public RainbowAnimation(double length)
    {
        super(length);
    }
    
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        //Logging.fatal("WHYYYYYYYYY");

        // For every pixel
        for (int i = 0; i < ledBuffer.getLength(); i++)
        {
            // Calculate the hue - hue is easier for rainbows because the color
            // shape is a circle so only one value needs to precess
            final int hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;
            // Set the value
            ledBuffer.setHSV(i, hue, 255, 128);
        }

        // Increase by to make the rainbow "move"
        rainbowFirstPixelHue += 3;

        // Assert bounds
        rainbowFirstPixelHue %= 180;
    }
}