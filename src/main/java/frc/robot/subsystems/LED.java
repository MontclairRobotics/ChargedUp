package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerBase;

import com.ctre.phoenix.led.RainbowAnimation;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.ChargedUp;
import frc.robot.constants.Constants;

public class LED extends ManagerBase {
    private AddressableLED led = new AddressableLED(Constants.Robot.LED_PWM_PORT);
    private AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(150);
    private RainbowAnimation rainbow = new RainbowAnimation();
    private Holding holding = Holding.None;

    // States:
    // Holding Cone -> yeller
    // Holding Cube -> purple
    // Holding None -> {
    //      AllianceColor or Rainbow
    // }


    public LED() {
        led.setLength(ledBuffer.getLength());
    }

   
    @Override
    public void always() {
        setAllianceColor();
        led.setData(ledBuffer);
    }

    public void setColor(Color color) {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setLED(i,color);
        }
    }

    public void setAllianceColor() {
        Alliance alliance = DriverStation.getAlliance();
        Color color = Color.kGray;
        // var color = switch(DriverStation.getAlliance()) //Todo uncomment when java 17
        // {
        //     case Blue -> Color.kBlue,
        //     case Red  -> Color.kRed,
        //     case Invalid -> Color.kWhite
        // };
        if (alliance == Alliance.Blue) { 
            color = Color.kBlue;
        } else if (alliance == Alliance.Red) {
            color = Color.kRed;
        } else if (alliance == Alliance.Invalid) {
            color = Color.kWhite;
        }
        setColor(color);
    }

    public void setHolding(Holding object) {
        holding = object;
        switch (holding) {
            case Cube:
                break;
            case Cone:
                break;
            case None:
                break;
        }
    }

}