package frc.robot.subsystems;

import java.util.Stack;

import org.team555.frc.command.commandrobot.ManagerBase;
import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.ctre.phoenix.led.RainbowAnimation;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.ChargedUp; 
import frc.robot.constants.Constants;
import frc.robot.structure.GamePiece;
import frc.robot.structure.animation.*;

public class LED extends ManagerBase 
{
    private AddressableLED led = new AddressableLED(Constants.Robot.LED.PWM_PORT);
    private AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(150);

    private GamePiece gamePiece = GamePiece.NONE;

    private Stack<Animation> animationStack;

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

    public LED()
    {
        led.setLength(ledBuffer.getLength());
        led.setData(ledBuffer);
        led.start();
        
        animationStack = new Stack<Animation>();
        animationStack.add(new DefaultAnimation());
    }
   
    @Override
    public void always()
    {
        // While the top is finished, cancel it
        while(hasCurrent() && current().isFinished()) cancel();

        // After this, if the top still exists, run it
        if(hasCurrent()) current().run(ledBuffer);

        //setAllianceColor();
        led.setData(ledBuffer);
        //System.out.println("We set the coloooorooroor");
    }

    /**
     * Add a command to the stack, interrupting the previous command
     */
    public void add(Animation animation, boolean shouldPause) 
    {
        if(hasCurrent() && shouldPause)
        {
            current().pause();
        }

        animationStack.add(animation);
        animation.begin();
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

}
