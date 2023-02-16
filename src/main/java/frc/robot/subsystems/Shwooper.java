package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.constants.Constants;
import frc.robot.constants.Constants.*;
import frc.robot.framework.commandrobot.ManagerSubsystemBase;



public class Shwooper extends ManagerSubsystemBase {

    private final Solenoid shwooperSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Robot.Shwooper.PNEU_PORT);

    private final CANSparkMax intakeMotor = new CANSparkMax(Robot.Shwooper.PORT, MotorType.kBrushless);
        // INTAKE PORT HAS NO VALUE
    public Shwooper() 
    {
        intakeMotor.setInverted(Robot.Shwooper.INVERSION);
    }

    /**
     * Intake starts sucking
     */
    public void suck() 
    {
        intakeMotor.set(Robot.Shwooper.SPEED);
    }

    /**
     * Expel game pieces from intake
     */
    public void spit() 
    {
        intakeMotor.set(-Robot.Shwooper.SPEED);
    }

    /**
     * Stops intake motors
     */
    public void stop() 
    {
        intakeMotor.set(0);
    }

    /**
     * Extends the intake using solenoid. Solenoid is not set at the default state
     */
    public void extend() 
    {
        shwooperSolenoid.set(!Robot.Shwooper.SOLENOID_DEFAULT_STATE);
    }

    /**
     * Retracts the intake using solenoid. Solenoid is set at the default state
     */
    public void retract() 
    {
        shwooperSolenoid.set(Robot.Shwooper.SOLENOID_DEFAULT_STATE);
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

    /**
     * Get if the shwooper is inside or outside of the robot. True means outside, false means inside.
     */
    public boolean isShwooperOut()
    {
        return !Robot.Shwooper.SOLENOID_DEFAULT_STATE;
    }
    

    
}
