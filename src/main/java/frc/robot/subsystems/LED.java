package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerBase;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.ChargedUp;
import frc.robot.constants.Constants;

public class LED extends ManagerBase {
    private AddressableLED led = new AddressableLED(Constants.Robot.LED_PWM_PORT);
    private AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(150);

    public LED() {
        led.setLength(ledBuffer.getLength());
    }

   
    @Override
    public void always() {
        led.setData(ledBuffer);
    }
    public void setColor(Color color) {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setLED(i, color);
        }
    }
}