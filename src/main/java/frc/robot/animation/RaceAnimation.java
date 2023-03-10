package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.math.Math555;

public class RaceAnimation extends Animation
{
    Color color;

    public RaceAnimation(double length, Color color) 
    {
        super(length);
        this.color = color;
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        int n = Math555.repeatingCycle(percentFinished(), 0, 1, 1, ledBuffer.getLength());
        for (int i = 0; i < ledBuffer.getLength(); i++)
        {
            if (i >= n-1 && i <= n+1) color = Color.kAqua;
            else        color = Color.kYellow;

            ledBuffer.setLED(i, color);
        }
    }
       
}
