package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class FadeAnimation extends Animation 
{
    private Color oldColor;
    private Color newColor;
    int index = 0;

    public FadeAnimation(Color oldColor, Color newColor, double length) {
        super(length);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    public void run(AddressableLEDBuffer ledBuffer) {
        for (int i = 0; i<index; i++) {
            ledBuffer.setLED(i, newColor);
            
        }
        index += 3;

    }
}
