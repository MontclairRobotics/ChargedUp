package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

public abstract class Animation 
{
    protected final Timer timer = new Timer();
    private double length;
    
    public Animation(double length)
    {
        this.length = length;
    }
    
    public abstract void run(AddressableLEDBuffer ledBuffer);
    
    protected double percentFinished(){
        return timer.get() / length;
    }

    public void begin()
    {
        timer.reset();
    }
    public void pause()
    {
        timer.stop();
    }
    public void resume()
    {
        timer.start();
    }
    public boolean isFinished()
    {
        return timer.hasElapsed(length);
    }

    public static void setColor(AddressableLEDBuffer ledBuffer, Color color)
    {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setLED(i,color);
        }
    }

}
