package frc.robot.subsystems.managers;

import java.util.Stack;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Constants;
import frc.robot.framework.commandrobot.ManagerBase;
import frc.robot.structure.GamePiece;
import frc.robot.structure.animation.*;

public class LED extends ManagerBase 
{
    private AddressableLED led = new AddressableLED(Constants.Robot.LED.PWM_PORT);
    private AddressableLEDBuffer displayBuffer = new AddressableLEDBuffer(LED_COUNT);
    private AddressableLEDBuffer oldBuffer = new AddressableLEDBuffer(LED_COUNT);
    private AddressableLEDBuffer newBuffer = new AddressableLEDBuffer(LED_COUNT);

    private GamePiece gamePiece = GamePiece.NONE;

    private Stack<Animation> animationStack;
    private TransitionConstructor transitionConstructor;
    private Transition currentTransition;

    public static final double TRANSITION_LENGTH = 0.2;
    public static final int LED_COUNT = 150;

    /**
     * Check if the led driver currently has an animation playing
     */
    public boolean hasCurrent()
    {
        return !animationStack.empty();
    }
    /**
     * Provide the current playing animation, throwing an error otherwise
     */
    public Animation current() 
    {
        return animationStack.peek();
    }
    public Animation previous() 
    {
        return animationStack.get(animationStack.size() - 2);
    }

    public LED()
    {
        led.setLength(LED_COUNT);
        led.setData(displayBuffer);
        led.start();
        
        animationStack = new Stack<Animation>();
        animationStack.add(new DefaultAnimation());

        transitionConstructor = WipeTransition::new;
    }
   
    @Override
    public void always()
    {
        // While the top is finished, cancel it
        while(hasCurrent() && current().isFinished()) 
        {
            cancel();
        }
        
        // Cancel the current transition if needed
        if(currentTransition != null && currentTransition.isFinished())
        {
            currentTransition = null;
        }

        // After this, if the top still exists, run it
        if(hasCurrent()) 
        {
            if(currentTransition == null)
            {
                current().run(displayBuffer);

                if(current().timeElapsed() > current().length() - TRANSITION_LENGTH)
                {
                    currentTransition = transitionConstructor.construct(TRANSITION_LENGTH);
                    currentTransition.begin();
                }
            }
            else 
            {
                if(current().timeElapsed() > TRANSITION_LENGTH)
                {
                    current ().run(oldBuffer);
                    previous().run(newBuffer);
                }
                else 
                {
                    current ().run(newBuffer);
                    previous().run(oldBuffer);
                }

                currentTransition.setOld(oldBuffer);
                currentTransition.setNew(newBuffer);

                currentTransition.run(displayBuffer);
            }
        }

        led.setData(displayBuffer);
    }

    /**
     * Add a command to the stack, interrupting the previous command
     */
    public void add(Animation animation, boolean shouldPause) 
    {
        animation.length(Math.max(animation.length(), TRANSITION_LENGTH * 2));

        if(hasCurrent() && shouldPause)
        {
            current().pause();
        }

        animationStack.add(animation);
        animation.begin();

        currentTransition = transitionConstructor.construct(TRANSITION_LENGTH);
        currentTransition.begin();
    }

    /**
     * Add a command to the stack, interrupting the previous command
     */
    public void add(Animation animation) 
    {
        add(animation, true);
    }

    /**
     * Cancel the top command and return to the previous
     * @return The cancelled command
     */
    public Animation cancel() 
    {
        Animation animPop = animationStack.pop();
        currentTransition = null;

        if(hasCurrent())
        {
            current().resume();
        }

        return animPop;
    }

    /**
     * Replace the current command with the given command
     * @return The cancelled command
     */
    public Animation replace(Animation animation)
    {
        Animation anim = animationStack.pop();
        add(animation);

        return anim;
    }

    /**
    * This method takes in the currently held game piece and changes the LEDS to the correct color
    * @param piece The game piece currently being held by the robot
    */
    public void updateColor() 
    {
        Color color = Color.kGray;
        switch (gamePiece) {
            case CUBE:
                color = Color.kViolet;
                replace(new SolidAnimation(Double.POSITIVE_INFINITY, color));
                break;
            case CONE:
                color = Color.kYellow;
                replace(new SolidAnimation(Double.POSITIVE_INFINITY, color));
                break;
            case NONE:
                replace(new DefaultAnimation());
                break;
            default:
                throw new Error("Don't pass null to the LEDs, stupid!");
        }
    }

    public void setTransition(TransitionConstructor cons)
    {
        transitionConstructor = cons;
    }
}
