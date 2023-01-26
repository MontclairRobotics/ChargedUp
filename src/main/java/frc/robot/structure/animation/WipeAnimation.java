package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class WipeAnimation extends Animation 
{
    private Color oldColor;
    private Color newColor;
    private int index = 0;

    public WipeAnimation(double time, Color oldColor, Color newColor) 
    {
        super(time);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    public void run(AddressableLEDBuffer ledBuffer) 
    {
        fill(ledBuffer, oldColor);
        for (int i = 0; i < index; i++) {
            ledBuffer.setLED(i, newColor);

        }
        index += 3;

    }
}
