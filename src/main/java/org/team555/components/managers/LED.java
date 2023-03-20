package org.team555.components.managers;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

import org.team555.animation2.AnimationStack;
import org.team555.animation2.RainbowAnimation;
import org.team555.animation2.api.Animation;
import org.team555.animation2.api.LEDBuffer;
import org.team555.animation2.api.TransitionBase;
import org.team555.constants.Ports;
import org.team555.util.frc.commandrobot.ManagerBase;

public class LED extends ManagerBase 
{
    private AddressableLED led = new AddressableLED(Ports.LED_PWM_PORT);
    private AnimationStack stack;

    public static final double TRANSITION_LENGTH = 0.4;
    public static final int LED_COUNT = 95;


    public LED(Animation defaultAnimation, TransitionBase transition)
    {
        stack = new AnimationStack(defaultAnimation, transition, TRANSITION_LENGTH);
        stack.start();

        led.setLength(LED_COUNT);
        led.start();
    }
   
    @Override
    public void always()
    {
        stack.render();
        led.setData(stack.getBuffer());
    }

    /**
     * Add a command to the stack, interrupting the previous command
     */
    public void add(Animation animation) 
    {
        stack.push(animation);
    }

    public void celebrate()
    {
        add(new RainbowAnimation().timeout(0.2));
    }

    /**
     * Cancel the top command and return to the previous
     * @return The cancelled command
     */
    public void pop() 
    {
        stack.pop();
    }

    /**
     * Replace the current command with the given command
     * @return The cancelled command
     */
    public void replace(Animation animation)
    {
        stack.pop();
        stack.push(animation);
    }
}
