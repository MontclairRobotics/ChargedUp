package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class FlippedAnimation extends Animation
{
    Animation internalAnimation;

    public FlippedAnimation(Animation internal)
    {
        super(internal.length());
        internalAnimation = internal;
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        internalAnimation.run(ledBuffer);

        for(int x = 0; x < ledBuffer.getLength() / 2; x++)
        {
            int antix = ledBuffer.getLength() - x - 1;

            Color temp = ledBuffer.getLED(x);

            ledBuffer.setLED(x, ledBuffer.getLED(antix));
            ledBuffer.setLED(antix, temp);
        }
    }
}
