package frc.robot.components.subsystems;

import frc.robot.ChargedUp;
import frc.robot.animation.DefaultAnimation;
import frc.robot.constants.PneuConstants;
import frc.robot.structure.GamePiece;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

import static frc.robot.constants.GrabberConstants.*;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.util.Color;


/**
 *      /\      // Output
 *      ||
 *    [####]
 *    /\  \/    // Select between input or none
 *    ||
 *   [######]   // Select between "low" and "high" pressure inputs
 *   /\    /\
 */
public class Grabber extends ManagerSubsystemBase 
{
    Solenoid outputSolenoid = new Solenoid(PneuConstants.PH_PORT, PneumaticsModuleType.REVPH, PneuConstants.GRABBER_SOLENOID_PORT);
    // Solenoid pressureSolenoid = new Solenoid(PneumaticsModuleType.REVPH, PneuConstants.GRABBER_PSI_SOLENOID_PORT);
    private boolean holdingCone = false;

    GamePiece heldObject = GamePiece.NONE;

    public GamePiece getHeldObject() {return heldObject;}
    public String getHeldObjectName() {return heldObject.toString().toLowerCase();}
    
    // /**
    //  * Update the pressure of the airflow to respect current readings.
    //  */
    // public void updatePressure()
    // {
    //     // if(ChargedUp.colorSensor.seesCone()) setPSIHigh();
    //     // else                                 setPSINormal();
    //     if (seesCone) setPSIHigh();
    //     else          setPSINormal();
    // }

    /**
     * Sets pneumatic state of grabber to <b>grabbed</b> (<b>non-default</b> state of solenoid)
     */
    public void grab() 
    {
        outputSolenoid.set(!SOLENOID_DEFAULT_STATE);
        // updatePressure();
        
        if (holdingCone) heldObject = GamePiece.CONE;
        else             heldObject = GamePiece.CUBE;
    }

    /**
     * Sets pneumatic state of grabber to <b>release</b> (<b>default</b> state of solenoid)
     */
    public void release() 
    {
        outputSolenoid.set(SOLENOID_DEFAULT_STATE);
        heldObject = GamePiece.NONE;
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
        // if (!outputSolenoid.get()) updatePressure();
        outputSolenoid.toggle();
    }

    public void toggleHoldingCone() 
    {
        holdingCone = !holdingCone;
    }
    public void setHoldingCone(boolean v) 
    {
        holdingCone = v;
    }

    public boolean getHoldingCone() 
    {
        return holdingCone;
    }


    // /**
    //  * Sets pneumatic state of grabber to <b>high pressure</b> (<b>non-default</b> state of solenoid)
    //  */
    // public void setPSIHigh()
    // {
    //     pressureSolenoid.set(!PSI_SOLENOID_DEFAULT_STATE);
    // }

    // /**
    //  * Sets pneumatic state of grabber to <b>normal pressure</b> (<b>default</b> state of solenoid)
    //  */
    // public void setPSINormal()
    // {
    //     pressureSolenoid.set(PSI_SOLENOID_DEFAULT_STATE);
    // }

    // /**
    //  * Toggle the Pressure
    //  * <p>
    //  * - if it is high pressure, then <b>normal pressure</b>
    //  * <p>
    //  * - if it is normal pressure, then <b>high pressure</b> 
    //  */
    // public void togglePressure() 
    // {
    //     pressureSolenoid.toggle();
    // }

    @Override
    public void always() 
    {
        // switch(heldObject)
        // {
        //     case CONE: DefaultAnimation.setYellow();  break;
        //     case CUBE: DefaultAnimation.setViolet();  break;
        //     case NONE: DefaultAnimation.setDefault(); break;
        // }
    }

    
}

// Add multiple pressures using another solenoid 
// Utilize a color sensor to make sure that higher pressure is applied only when we are sure of the presence of a cone