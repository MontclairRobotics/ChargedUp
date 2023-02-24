package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

/**
 * An animation which wipes from one color to another (without any blurring).
 */
public class WipeTransition extends Transition 
{
    public WipeTransition(double length) 
    {
        super(length);
    }

    public void run(AddressableLEDBuffer ledBuffer) 
    {
        final int wipeOffset = (int)(ledBuffer.getLength() * percentFinished());

        LEDBuffer.copy(oldBuffer, ledBuffer);

        for (int i = 0; i < wipeOffset; i++) 
        {
            LEDBuffer.copy(i, newBuffer, ledBuffer);
        }
    }
}
