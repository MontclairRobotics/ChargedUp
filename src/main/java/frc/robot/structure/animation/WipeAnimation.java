package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

/**
 * An animation which wipes from one color to another (without any blurring).
 */
public class WipeAnimation extends Animation 
{
    private Color oldColor;
    private Color newColor;

    public WipeAnimation(double time, Color oldColor, Color newColor) 
    {
        super(time);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    public void run(AddressableLEDBuffer ledBuffer) 
    {
        final int wipeOffset = (int)(ledBuffer.getLength() * percentFinished());

        fill(ledBuffer, oldColor);

        for (int i = 0; i < wipeOffset; i++) 
        {
            ledBuffer.setLED(i, newColor);
        }
    }
}
