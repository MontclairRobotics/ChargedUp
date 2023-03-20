package org.team555.animation2;

import java.util.Stack;

import org.team555.animation2.api.Animation;
import org.team555.animation2.api.LEDBuffer;
import org.team555.animation2.api.SimpleAnimationBase;
import org.team555.animation2.api.TransitionBase;
import org.team555.util.frc.EdgeDetectFilter;
import org.team555.util.frc.Logging;
import org.team555.util.frc.EdgeDetectFilter.EdgeType;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

/**
 * Handles a stack of animations which features transitions.
 */
public class AnimationStack extends SimpleAnimationBase 
{
    private final Stack<Animation> animations = new Stack<>();
    private final TransitionBase transition;

    EdgeDetectFilter startedTransitioning = new EdgeDetectFilter(EdgeType.RISING);

    boolean transitioning;
    Animation animationOut;

    public AnimationStack(Animation defaultAnimation, TransitionBase transition, double transtime)
    {
        this.transition = transition;
        transition.setLength(transtime);

        push(defaultAnimation);
    }
    
    private Animation top() 
    {
        return animations.peek();
    }

    @Override
    public void start() 
    {
        startedTransitioning.calculate(false);
    }

    public void push(Animation anim)
    {
        animations.push(anim);
        anim.start();
    }
    public void pop()
    {
        if(animations.size() <= 1) 
        {
            Logging.warning("Cannot pop the last remaining animation from an animation stack!");
        }

        transitioning = true;
        if(animationOut == null) animationOut = animations.pop();
    }

    @Override
    public void render() 
    {
        // Kill off animations as necessary
        if(!transitioning)
        {
            while(top().isFinished())
            {
                pop();
            }
        }
        
        // Handle transitions
        boolean startedTrans = startedTransitioning.calculate(transitioning);

        if(startedTrans)
        {
            transition.start();

            transition.setOut(animationOut);
            transition.setIn(top());
        }

        // Handle animations
        if(transitioning)
        {
            transition.render();

            if(transition.isFinished())
            {
                transitioning = false;
                animationOut  = null;
            }
        }
        else 
        {
            top().render();
        }
    }

    @Override
    public AddressableLEDBuffer getBuffer() 
    {
        if(transitioning) return transition.getBuffer();
        return animations.peek().getBuffer();
    }
}
