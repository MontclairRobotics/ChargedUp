package org.team555_deprecated.frc.leds;

import java.util.function.Supplier;

import org.team555_deprecated.frc.RawColorUtil;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.framework.color.RawColor;

public abstract class LedPattern 
{
    public static final RawColor LED_OFF = RawColor.rgb(0, 0, 0);

    public void update() {}
    public abstract RawColor get(int index, int length);
    
    public final void processInto(AddressableLEDBuffer led, int index, int length)
    {
        led.setLED(index, RawColorUtil.wpiFrom(get(index, length)));
    }
}
