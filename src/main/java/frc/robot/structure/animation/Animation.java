package frc.robot.structure.animation;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

/**
 * The base class for an LED animation.
 * Has a built-in timer and length in order to keep track of animation completion.
 * May have a length of {@link Double#POSITIVE_INFINITY} to emulate an un-ending animation.
 */
public abstract class Animation 
{
    protected final Timer timer = new Timer();
    private double length;
    
    public Animation(double length)
    {
        this.length = length;
    }
    
    /**
     * The driving method of animation classes; will be called each frame that the animation is active
     * @param ledBuffer The buffer to render into
     */
    public abstract void run(AddressableLEDBuffer ledBuffer);
    
    /**
     * Gets the percentage completion of this animation.
     * If the length of this animation is {@link Double#POSITIVE_INFINITY}, always returns 0
     */
    public double percentFinished()
    {
        return timer.get() / length;
    }

    /**
     * Starts the execution of this animation
     */
    public void begin()
    {
        timer.reset();
    }
    /**
     * Pauses execution of this animation
     */
    public void pause()
    {
        timer.stop();
    }
    /**
     * Resumes execution of this animation.
     * This does nothing if the animation is already running
     */
    public void resume()
    {
        timer.start();
    }
    /**
     * Checks if this animation has completed.
     * Always returns {@code false} if this animation has a length of {@link Double#POSITIVE_INFINITY}
     */
    public boolean isFinished()
    {
        return timer.hasElapsed(length);
    }

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
}
