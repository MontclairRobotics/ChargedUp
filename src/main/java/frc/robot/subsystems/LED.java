package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerBase;
import org.team555.util.color.HSV;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.ChargedUp;
import frc.robot.constants.Constants;

public class LED extends ManagerBase 
{
    private AddressableLED led = new AddressableLED(Constants.Robot.LED_PWM_PORT);
    private AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(150);

    public static Color fade(Color col, double x)
    {
        return new Color((int)(col.red * x), (int)(col.green * x), (int)(col.blue * x));
    }

    public void solidColor(Color col)
    {
        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            ledBuffer.setLED(i, col);
        }
    }

    public void fadeRotate(Color col, int timeMs)
    {
        int center = (int)((System.currentTimeMillis() / timeMs) % ledBuffer.getLength());

        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            ledBuffer.setLED
            (
                (i + center) % ledBuffer.getLength(),       // Index
                fade(col, 1 - (i / ledBuffer.getLength())) //  Color Faded
            );
        }
    }

    @Override
    public void always() {
        led.setData(ledBuffer);
    }
}