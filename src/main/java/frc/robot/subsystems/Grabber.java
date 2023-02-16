package frc.robot.subsystems;

import frc.robot.Constants;
import frc.robot.framework.commandrobot.ManagerSubsystemBase;

import static frc.robot.Constants.*;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;


public class Grabber extends ManagerSubsystemBase 
{
    Solenoid solenoid = new Solenoid(PneumaticsModuleType.REVPH, Pneu.GRABBER_SOLENOID_PORT);
    
    /**
     * Sets pneumatic state of grabber to <b>grabbed</b> (<b>non-default</b> state of solenoid)
     */
    public void grab() 
    {
        solenoid.set(!Constants.Robot.Grabber.SOLENOID_DEFAULT_STATE);
    }

    /**
     * Sets pneumatic state of grabber to <b>release</b> (<b>default</b> state of solenoid)
     */
    public void release() 
    {
        solenoid.set(Constants.Robot.Grabber.SOLENOID_DEFAULT_STATE);
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
        solenoid.set(!solenoid.get());
    }
}