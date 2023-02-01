package frc.robot.structure.animation;

import org.team555.util.color.RawColor;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

/**
 * An animation which fades the entire LED strip from one color to another.
 * Used to transition from two solid colors.
 */
public class FadeAnimation extends Animation 
{
    private Color oldColor;
    private Color newColor;

    public FadeAnimation(Color oldColor, Color newColor, double length) 
    {
        super(length);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    public void run(AddressableLEDBuffer ledBuffer) 
    {
        fill(ledBuffer, RawColor.from(oldColor).hsvLerp(RawColor.from(newColor), percentFinished()).to());
    }
}
