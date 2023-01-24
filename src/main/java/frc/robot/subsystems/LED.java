package frc.robot.subsystems;

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
import frc.robot.structure.Animations;

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


    public LED()
    {
        led.setLength(ledBuffer.getLength());
        setColor(Color.kBlue);
        led.setData(ledBuffer);
        led.start();
    }
   
    @Override
    public void always()
    {
        setAllianceColor();
        led.setData(ledBuffer);
        // System.out.println("We set the coloooorooroor");
    }

    public void setColor(Color color)
    {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setLED(i,color);
        }
    }

    /**
    * This method takes the current alliance color from the driver station and sets the LEDs to the correct color.
    */
    public void setAllianceColor() 
    {
        Alliance alliance = DriverStation.getAlliance();
        Color color = Color.kGray;
        // var color = switch(DriverStation.getAlliance()) //Todo uncomment when java 17
        // {
        //     case Blue -> Color.kBlue,
        //     case Red  -> Color.kRed,
        //     case Invalid -> Color.kWhite
        // };
        if (alliance == Alliance.Blue) color = Color.kBlue; 
        else if (alliance == Alliance.Red) color = Color.kRed;
        else if (alliance == Alliance.Invalid) color = Color.kWhite;
        setColor(color);
    }

    public void setHolding(GamePiece piece) {
        gamePiece = piece;
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
                setColor(color);
                break;
            case CONE:
                color = Color.kYellow;
                setColor(color);
                break;
            case NONE:
                setAllianceColor();
                break;
            default:
                throw new Error("Don't pass null to the LEDs, stupid!");
        }
    }

}
