package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
import frc.robot.constants.Constants.*;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;


public class Grabber extends ManagerSubsystemBase {

    Solenoid solenoid = new Solenoid(PneumaticsModuleType.REVPH, Pneu.SOLENOID_PORT);
    //Grabber.grab()
    public void grab() {
        solenoid.set(true);
    }

    public void release() {
        solenoid.set(false);
    }
    //Grabber.release()
}