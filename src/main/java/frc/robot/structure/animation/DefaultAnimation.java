package frc.robot.structure.animation;

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
    public DefaultAnimation()
    {
        super(Double.POSITIVE_INFINITY);
    }
    
    @Override
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        Alliance alliance = DriverStation.getAlliance();
        Color color = Color.kGray;

        if (alliance == Alliance.Blue) color = Color.kBlue; 
        else if (alliance == Alliance.Red) color = Color.kRed;
        else if (alliance == Alliance.Invalid) color = Color.kWhite;
        fill(ledBuffer, color);
    }
}
