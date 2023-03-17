package org.team555.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class LEDBuffer 
{
    private LEDBuffer() {}

    /**
     * Fills the given {@link AddressableLEDBuffer} with the given {@link Color}
     */
    public static void fill(AddressableLEDBuffer ledBuffer, Color color)
    {
        for (int i = 0; i < ledBuffer.getLength(); i++) 
        {
            ledBuffer.setLED(i,color);
        }
    }
    
    /**
     * Copies the given LED index from the first buffer to the second
     */
    public static void copy(int i, AddressableLEDBuffer from, AddressableLEDBuffer to)
    {
        to.setLED(i, from.getLED(i));
    }

    /**
     * Copies all LEDs from the first buffer to the second
     */
    public static void copy(AddressableLEDBuffer from, AddressableLEDBuffer to)
    {
        assert from.getLength() == to.getLength() : "Cannot copy from a buffer of size " + from.getLength() + " to a buffer of differing size " + to.getLength();

        for (int i = 0; i < from.getLength(); i++) 
        {
            copy(i, from, to);
        }
    }
}
