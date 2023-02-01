package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import frc.robot.constants.Constants;
import static frc.robot.constants.Constants.*;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;


public class Grabber extends ManagerSubsystemBase {
    Solenoid solenoid = new Solenoid(PneumaticsModuleType.REVPH, Pneu.GRABBER_SOLENOID_PORT);
    
    public void grab() 
    {
        solenoid.set(!Constants.Robot.Grabber.SOLENOID_DEFAULT_STATE);
    }

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