package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class CircusAnimation extends Animation
{
    private int offset = 0;
    public CircusAnimation(double length) 
    {
        super(length);

    }
    public void run(AddressableLEDBuffer ledBuffer)
    {
        offset = (int)(timer.get() * 5);

        for(int i = 0; i < ledBuffer.getLength(); i++) 
        {
            if ((i + offset) % 3 == 0) 
            {
                ledBuffer.setLED(i, Color.kBlack);
            } 
            else 
            {
                ledBuffer.setLED(i, Color.kLightGoldenrodYellow);
            }
        }
    }
}
