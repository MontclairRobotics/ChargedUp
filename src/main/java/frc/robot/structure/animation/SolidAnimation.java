package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class SolidAnimation extends Animation{
    private Color color;
    
    public SolidAnimation(double time, Color color){
        super(time);
        this.color = color;
    }

    public void run(AddressableLEDBuffer ledBuffer) 
    {
        setColor(ledBuffer, color);
    }
}
