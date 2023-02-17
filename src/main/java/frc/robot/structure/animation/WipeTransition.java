package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

/**
 * An animation which wipes from one color to another (without any blurring).
 */
public class WipeTransition extends Transition 
{
    public WipeTransition(double length, AddressableLEDBuffer old, AddressableLEDBuffer newb) 
    {
        super(length, old, newb);
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
