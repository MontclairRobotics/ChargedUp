package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.ChargedUp;

public class RainbowAnimation extends Animation
{
    int rainbowFirstPixelHue = 0;

    public RainbowAnimation(double length)
    {
        super(length);
    }
    
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        // For every pixel
        for (var i = 0; i < ledBuffer.getLength(); i++)
        {
            // Calculate the hue - hue is easier for rainbows because the color
            // shape is a circle so only one value needs to precess
            final var hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;
            // Set the value
            ledBuffer.setHSV(i, hue, 255, 128);
        }
        // Increase by to make the rainbow "move"
        rainbowFirstPixelHue += 3;
        // Check bounds
        rainbowFirstPixelHue %= 180;
    }
}

//while (!ChargedUp.animationTimer.hasTimeElapsed(timer.get())) {