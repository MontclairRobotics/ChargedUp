package frc.robot.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class ASCIImation extends Animation 
{
    private final Color[] bits;

    public ASCIImation(double length, String text, Color lo, Color hi, Color buf, Color wrd)
    {
        super(length);

        final int tlen9 = text.length() * 9;
        bits = new Color[tlen9 + 2];

        bits[0]     = wrd;
        bits[tlen9] = wrd;

        int idx = 2;
        for(char c : text.toCharArray()) 
        {
            int ci = c;
            for(int i = 0; i < 8; i++)
            {
                bits[idx++] = (ci & 1) == 0 ? lo : hi;
                ci <<= 1;
            }

            if(idx == tlen9)
            {
                bits[idx++] = buf;
            }
        }
    }

    @Override
    public void run(AddressableLEDBuffer ledBuffer)
    {
        final int offset = (int)(timeElapsed() / 0.1);

        for(int i = 0; i < ledBuffer.getLength(); i++)
        {
            final int x = (i + offset) % ledBuffer.getLength();
            final Color c = bits[x % bits.length];

            ledBuffer.setLED(x, c);
        }
    }
}
