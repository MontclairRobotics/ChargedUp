package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.Constants;
import frc.robot.constants.Constants.*;



public class Shwooper extends ManagerSubsystemBase {

    private final Solenoid shwooperSolenoid = new Solenoid(PneumaticsModuleType.REVPH, 1);
    private boolean isExtended = false;

    private final CANSparkMax intakeMotor = new CANSparkMax(Robot.INTAKE_PORT, MotorType.kBrushless);
        // INTAKE PORT HAS NO VALUE
    public Shwooper() {
        intakeMotor.setInverted(Robot.INTAKE_INVERSION);
    }

    public void suck() {
        intakeMotor.set(Robot.INTAKE_SPEED);
    }
    public void spit() {
        intakeMotor.set(-Robot.INTAKE_SPEED);
    }
    public void stop() {
        intakeMotor.set(0);
    }
    private void extend() {
        shwooperSolenoid.set(!Constants.Robot.SHWOOPER_SOLENOID_DEFAULT_STATE);
    }
    private void retract() {
        shwooperSolenoid.set(Constants.Robot.SHWOOPER_SOLENOID_DEFAULT_STATE);
    }
    public void toggle() {
        if (isExtended) {
            retract();
        }
        else
        {
            extend();
        }
        isExtended = !isExtended;
    }
    

    
}
