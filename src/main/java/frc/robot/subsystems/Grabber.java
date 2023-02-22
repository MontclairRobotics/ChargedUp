package frc.robot.subsystems;

import frc.robot.framework.commandrobot.ManagerSubsystemBase;

import static frc.robot.Constants.*;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.util.Color;


/**
 *    ^         // Output
 *    |
 *    [#####]
 *    ^     #   // Select between input or none
 *    |
 *   [######]   // Select between "low" and "high" pressure inputs
 *   ^      ^
 */
public class Grabber extends ManagerSubsystemBase 
{
    Solenoid outputSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Pneu.GRABBER_SOLENOID_PORT);
    Solenoid pressureSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Pneu.GRABBER_PSI_SOLENOID_PORT);

    ColorSensorV3 colorSensor = new ColorSensorV3(Robot.Grabber.COLOR_SENSOR_PORT);
    ColorMatch colorMatch = new ColorMatch();

    public Grabber() 
    {
        colorMatch.addColorMatch(Robot.Grabber.CONE_COLOR);
        colorMatch.addColorMatch(Robot.Grabber.CUBE_COLOR);

        colorMatch.setConfidenceThreshold(Robot.Grabber.COLOR_CONFIDENCE);
    }

    public ColorMatchResult colorResult()
    {
        return colorMatch.matchClosestColor(colorSensor.getColor());
    }
    public Color color()
    {
        return colorResult().color;
    }
    public double colorConfidence()
    {
        return colorResult().confidence;
    }

    public 

    
    /**
     * Sets pneumatic state of grabber to <b>grabbed</b> (<b>non-default</b> state of solenoid)
     */
    public void grab() 
    {
        outputSolenoid.set(!Robot.Grabber.SOLENOID_DEFAULT_STATE);
    }

    /**
     * Sets pneumatic state of grabber to <b>release</b> (<b>default</b> state of solenoid)
     */
    public void release() 
    {
        outputSolenoid.set(Robot.Grabber.SOLENOID_DEFAULT_STATE);
    }

    /**
     * Toggle the Grabber
     * <p>
     * - if it is grabbed something, then <b>release</b>
     * <p>
     * - if it is released, <b>grab</b> 
     */
    public void toggle()
    {
        outputSolenoid.toggle();
    }

    /**
     * Sets pneumatic state of grabber to <b>high pressure</b> (<b>non-default</b> state of solenoid)
     */
    public void setPSIHigh()
    {
        pressureSolenoid.set(!Robot.Grabber.PSI_SOLENOID_DEFAULT_STATE);
    }

    /**
     * Sets pneumatic state of grabber to <b>normal pressure</b> (<b>default</b> state of solenoid)
     */
    public void setPSINormal()
    {
        pressureSolenoid.set(Robot.Grabber.PSI_SOLENOID_DEFAULT_STATE);
    }

    /**
     * Toggle the Pressure
     * <p>
     * - if it is high pressure, then <b>normal pressure</b>
     * <p>
     * - if it is normal pressure, then <b>high pressure</b> 
     */

    public void togglePressure() 
    {
        pressureSolenoid.toggle();
    }
}

// Add multiple pressures using another solenoid 
// Utilize a color sensor to make sure that higher pressure is applied only when we are sure of the presence of a cone