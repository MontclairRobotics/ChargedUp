package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.framework.Math555;

/**
 * An animation which fades the entire LED strip from one color to another.
 * Used to transition from two solid colors.
 */
public class FadeTransition extends Transition 
{
    public FadeTransition(double length) 
    {
        super(length);
    }

    public void run(AddressableLEDBuffer ledBuffer) 
    {
        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            ledBuffer.setLED(i, Math555.lerp(oldBuffer.getLED(i), newBuffer.getLED(i), percentFinished()));
        }
    }
}
