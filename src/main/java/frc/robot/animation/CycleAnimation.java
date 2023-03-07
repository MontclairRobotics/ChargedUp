package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.math.Math555;

public class CycleAnimation extends Animation {
    private Color[] colors;

    public CycleAnimation(double length, Color... colors) 
    {
        super(length);
        this.colors = colors;
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        for (int i = 0; i < ledBuffer.getLength(); i++)
        {
            int variable = (int)(i+timeElapsed()*10) % ledBuffer.getLength();
            int n = Math555.repeatingCycle(variable, 0, ledBuffer.getLength(), ledBuffer.getLength()/colors.length, colors.length);
            ledBuffer.setLED(i, colors[n]);
        }
    }
    
}
