// package frc.robot.subsystems;

// import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
// import frc.robot.constants.Constants;

// import edu.wpi.first.wpilibj.PneumaticsModuleType;
// import edu.wpi.first.wpilibj.Solenoid;


// public class Grabber extends ManagerSubsystemBase {

//     Solenoid solenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.Pneu.COMPRESSOR_PORT);
//     //Grabber.grab()
//     public void grab() {
//         solenoid.set(Constants.Robot.GRABBER_SOLENOID_DEFAULT_STATE);
//     }

//     public void release() {
//         solenoid.set(!Constants.Robot.GRABBER_SOLENOID_DEFAULT_STATE);
//     }
//     //Grabber.release()
// }