package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import frc.robot.constants.Constants;
import static frc.robot.constants.Constants.*;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;


public class Grabber extends ManagerSubsystemBase {
    Solenoid solenoid = new Solenoid(PneumaticsModuleType.REVPH, Pneu.GRABBER_SOLENOID_PORT);
    //Grabber.grab()
    private void grab() {
        solenoid.set(!Constants.Robot.GRABBER_SOLENOID_DEFAULT_STATE);
    }

    private void release() {
        solenoid.set(Constants.Robot.GRABBER_SOLENOID_DEFAULT_STATE);
    }
    public void toggle(){
        solenoid.set(!solenoid.get());
    }
}