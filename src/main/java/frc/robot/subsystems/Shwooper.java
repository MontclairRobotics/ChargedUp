package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.Constants;



public class Shwooper extends ManagerSubsystemBase {

    private final CANSparkMax intakeMotor = new CANSparkMax(Constants.INTAKE_PORT, MotorType.kBrushless);
        // INTAKE PORT HAS NO VALUE
    public Shwooper() {
        intakeMotor.setInverted(Constants.INTAKE_INVERSION);
    }

    public void suck() {
        intakeMotor.set(Constants.INTAKE_SPEED);
    }
    public void spit() {
        intakeMotor.set(-Constants.INTAKE_SPEED);
    }
    public void stop() {
        intakeMotor.set(0);
    }
    

    
}
