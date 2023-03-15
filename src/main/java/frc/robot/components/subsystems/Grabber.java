package frc.robot.components.subsystems;

import frc.robot.constants.PneuConstants;
import frc.robot.structure.GamePiece;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

import static frc.robot.constants.GrabberConstants.*;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

public class Grabber extends ManagerSubsystemBase 
{
    Solenoid outputSolenoid = new Solenoid(PneuConstants.PH_PORT, PneumaticsModuleType.REVPH, PneuConstants.GRABBER_SOLENOID_PORT);
    private boolean holdingCone = false;

    GamePiece heldObject = GamePiece.NONE;

    public GamePiece getHeldObject() {return heldObject;}
    public String getHeldObjectName() {return heldObject.toString().toLowerCase();}

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