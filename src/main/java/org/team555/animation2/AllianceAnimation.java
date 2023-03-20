package org.team555.animation2;

import org.team555.animation2.api.AnimationBase;
import org.team555.animation2.api.LEDBuffer;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * The default animation for the LEDs.
 * Takes the current alliance color from the driver station and sets the LEDs to the correct color.
 */
public class AllianceAnimation extends AnimationBase
{
    @Override
    public void render() 
    {
        Alliance alliance = DriverStation.getAlliance();
        Color color = Color.kDimGray;

        if (alliance == Alliance.Blue) color = Color.kBlue; 
        else if (alliance == Alliance.Red) color = Color.kRed;
        else if (alliance == Alliance.Invalid) color = Color.kWhite;

        LEDBuffer.fill(getBuffer(), color);
    }
}
