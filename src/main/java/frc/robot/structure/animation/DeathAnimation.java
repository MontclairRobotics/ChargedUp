package frc.robot.structure.animation;

import java.util.Random;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class DeathAnimation extends Animation
{
    public DeathAnimation(double length) 
    {
        super(length);
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            Random random = new Random();
            ledBuffer.setHSV(i, random.nextInt(0, 255), random.nextInt(0, 255), random.nextInt(0, 255));
        }
    }
}
