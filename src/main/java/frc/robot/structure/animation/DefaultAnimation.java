package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DriverStation;

public class DefaultAnimation extends Animation{

    public DefaultAnimation(){
        super(Double.POSITIVE_INFINITY);
    }
    
    /**
    * This method takes the current alliance color from the driver station and sets the LEDs to the correct color.
    */
    public void run(AddressableLEDBuffer ledBuffer) 
    {
        Alliance alliance = DriverStation.getAlliance();
        Color color = Color.kGray;
        // var color = switch(DriverStation.getAlliance()) //Todo uncomment when java 17
        // {
        //     case Blue -> Color.kBlue,
        //     case Red  -> Color.kRed,
        //     case Invalid -> Color.kWhite
        // };
        if (alliance == Alliance.Blue) color = Color.kBlue; 
        else if (alliance == Alliance.Red) color = Color.kRed;
        else if (alliance == Alliance.Invalid) color = Color.kWhite;
        setColor(ledBuffer, color);
    }
}
