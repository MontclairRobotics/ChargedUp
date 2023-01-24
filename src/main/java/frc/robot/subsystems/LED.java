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
    private AddressableLED led = new AddressableLED(Constants.Robot.LED_PWM_PORT);
    private AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(150);
    private RainbowAnimation rainbow = new RainbowAnimation();
    private GamePiece gamePiece = GamePiece.NONE;

    // States:
    // Holding Cone -> yeller
    // Holding Cube -> purple
    // Holding None -> {
    //      AllianceColor or Rainbow
    // }
    private Stack<Animation> animationStack;

    public boolean hasCurrent()
    {
        return !animationStack.empty();
    }
    public Animation current() 
    {
        return animationStack.peek();
    }

    public LED()
    {
        led.setLength(ledBuffer.getLength());
        setColor(Color.kBlue);
        led.setData(ledBuffer);
        led.start();
        
        animationStack = new Stack<Animation>();
    }
   
    @Override
    public void always()
    {
        // While the top is finished, cancel it
        while(hasCurrent() && current().isFinished()) cancel();

        // After this, if the top still exists, run it
        if(hasCurrent()) current().run(ledBuffer);

        //setAllianceColor();
        //led.setData(ledBuffer);
        //System.out.println("We set the coloooorooroor");
    }

    /**
     * Add a command to the stack, interrupting the previous command
     */
    public void add(Animation animation) 
    {
        if(hasCurrent())
        {
            current().pause();
        }

        animationStack.add(animation);
        animation.begin();
    }

    /**
     * Cancel the top command and return to the previous
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
     * Replace the current command with the given command.
     */
    public void replace(Animation animation)
    {
        animationStack.pop();
        add(animation);
    }
    
    public void setColor(Color color)
    {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setLED(i,color);
        }
    }

    /**
    * This method takes in the currently held game piece and changes the LEDS to the correct color.
    * @param piece the game piece currently being held by the robot
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
