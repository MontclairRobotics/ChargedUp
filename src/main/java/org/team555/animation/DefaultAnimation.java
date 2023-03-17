package org.team555.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * The default animation for the LEDs.
 * Takes the current alliance color from the driver station and sets the LEDs to the correct color.
 */
public class DefaultAnimation extends Animation
{
    private static boolean shouldYellow = false;
    private static boolean shouldViolet = false;
    private static boolean shouldDefault = true;
  
    public static void setYellow()
    {
        shouldYellow = true;
        shouldViolet = false;
        shouldDefault = false;
    }

    public static void setViolet()
    {
        shouldViolet = true;
        shouldYellow = false;
        shouldDefault = false;
    }

    public static void setDefault()
    {
        shouldDefault = true;
        shouldYellow = false;
        shouldViolet = false;
    }


    public DefaultAnimation()
    {
        super(Double.POSITIVE_INFINITY);
    }
    
    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        Alliance alliance = DriverStation.getAlliance();
        Color color = Color.kGray;
        if(shouldDefault == true) 
        {
            if (alliance == Alliance.Blue) color = Color.kBlue; 
            else if (alliance == Alliance.Red) color = Color.kRed;
            else if (alliance == Alliance.Invalid) color = Color.kWhite;
        }
        if(shouldYellow == true)
        {
            color = Color.kYellow;
        }
        if(shouldViolet == true)
        {
            color = Color.kViolet;
        }
        LEDBuffer.fill(ledBuffer, color);
    }
}
