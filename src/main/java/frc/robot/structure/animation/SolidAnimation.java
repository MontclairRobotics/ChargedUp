package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

/**
 * An animation which simply fills the led buffer with a provided color for some amount of time.
 */
public class SolidAnimation extends Animation
{
    private Color color;
    
    public SolidAnimation(double time, Color color)
    {
        super(time);
        this.color = color;
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        fill(ledBuffer, color);
    }
}
