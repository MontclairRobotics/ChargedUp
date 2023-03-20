package org.team555.components.subsystems;

import org.team555.constants.PneuConstants;
import org.team555.structure.GamePiece;
import org.team555.util.frc.EdgeDetectFilter;
import org.team555.util.frc.EdgeDetectFilter.EdgeType;
import org.team555.util.frc.commandrobot.ManagerSubsystemBase;

import static org.team555.constants.GrabberConstants.*;

import org.team555.ChargedUp;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

public class Grabber extends ManagerSubsystemBase 
{
    Solenoid outputSolenoid = new Solenoid(PneuConstants.PH_PORT, PneumaticsModuleType.REVPH, PneuConstants.GRABBER_SOLENOID_PORT);
    private boolean holdingCone = false;

    GamePiece heldObject = GamePiece.NONE;

    public GamePiece getHeldObject() {return heldObject;}
    public String getHeldObjectName() {return heldObject.toString().toLowerCase();}

    public boolean isOpen  () {return outputSolenoid.get() == SOLENOID_DEFAULT_STATE;}
    public boolean isClosed() {return outputSolenoid.get() == !SOLENOID_DEFAULT_STATE;}

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

    private EdgeDetectFilter filter = new EdgeDetectFilter(EdgeType.RISING);

    @Override
    public void always() 
    {
        //if elevator becomes within lower buffer (previously was not in lower buffer), release
        if (filter.calculate(ChargedUp.elevator.isWithinLowerBuffer())) release();

        // switch(heldObject)
        // {
        //     case CONE: DefaultAnimation.setYellow();  break;
        //     case CUBE: DefaultAnimation.setViolet();  break;
        //     case NONE: DefaultAnimation.setDefault(); break;
        // }
    }

    
}