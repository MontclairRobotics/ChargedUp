package frc.robot.structure.animation;

import org.team555.util.color.RawColor;

import edu.wpi.first.cscore.raw.RawSource;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

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
