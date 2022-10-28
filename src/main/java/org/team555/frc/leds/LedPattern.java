package org.team555.frc.leds;

import java.util.function.Supplier;

import org.team555.frc.RawColorUtil;
import org.team555.util.color.RawColor;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

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
