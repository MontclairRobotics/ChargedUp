package frc.robot.structure.animation;

import java.util.Random;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.framework.Math555;
import frc.robot.framework.PerlinNoise;
import frc.robot.framework.PerlinNoiseRing;

public class MagicAnimation extends Animation
{
    public MagicAnimation(double length, Color base, Color magic) 
    {
        super(length);

        noiseRing = new PerlinNoiseRing(length);

        this.base = base;
        this.magic = magic;
    }
    private Color base;
    private Color magic;

    private PerlinNoiseRing noiseRing;

    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        // Render each pixel
        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            // Remap this value
            double heatRemap = noiseRing.get(timer.get(), i, ledBuffer.getLength());

            // Perform the linear interpolations needed to get the hue and value,
            // using (0 -> 10) as the hue range and (0 -> 255) for the value range.
            // To change the color of the flames, modify these values.
            Color color = Math555.lerp(base, magic,  Math.pow(heatRemap, 2.0));
            // int val = Math555.lerp(0, 255, Math.pow(heatRemap, 4.0));

            // Push the pixel color to the led buffer
            ledBuffer.setLED(i, color);
        }   
    } 

    public static MagicAnimation fire(double length)
    {
        return new MagicAnimation(length, Color.kBlack, Color.kOrangeRed);
    }
    public static MagicAnimation galaxy(double length)
    {
        return new MagicAnimation(length, new Color(45, 0, 200), Color.kDeepPink);
    }
}
