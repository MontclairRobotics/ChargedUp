package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.constants.Constants;
import frc.robot.constants.Constants.*;



public class Shwooper extends ManagerSubsystemBase {

    private final Solenoid shwooperSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Robot.Shwooper.INTAKE_PNEU_PORT);

    private final CANSparkMax intakeMotor = new CANSparkMax(Robot.Shwooper.INTAKE_PORT, MotorType.kBrushless);
        // INTAKE PORT HAS NO VALUE
    public Shwooper() 
    {
        intakeMotor.setInverted(Robot.Shwooper.INTAKE_INVERSION);
    }

    /**
    * intake starts sucking
    */
    public void suck() 
    {
        intakeMotor.set(Robot.Shwooper.INTAKE_SPEED);
    }
    /**
    * intake start spitting
    */
    public void spit() 
    {
        intakeMotor.set(-Robot.Shwooper.INTAKE_SPEED);
    }
    /**
    * stop the intake
    */
    public void stop() 
    {
        intakeMotor.set(0);
    }

    public void extend() 
    {
        shwooperSolenoid.set(!Robot.Shwooper.SHWOOPER_SOLENOID_DEFAULT_STATE);
    }

    public void retract() 
    {
        shwooperSolenoid.set(Robot.Shwooper.SHWOOPER_SOLENOID_DEFAULT_STATE);
    }

    /**
    * toggle the Shwooper
    * <p>
    * - if it is extended, <b>retract</b>
    * <p>
    * - if it is retracted, <b>extend</b>
    */
    public void toggle() 
    {
        shwooperSolenoid.set(!shwooperSolenoid.get());
    }
    

    
}
