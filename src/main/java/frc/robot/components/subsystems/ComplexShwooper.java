package frc.robot.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import static frc.robot.constants.ShwooperConstants.*;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;



public class ComplexShwooper extends ManagerSubsystemBase implements Shwooper
{
    private final Solenoid shwooperSolenoid = new Solenoid(PneumaticsModuleType.REVPH, PNEU_PORT);

    private final CANSparkMax motorLeft = new CANSparkMax(LEFT_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax motorRight = new CANSparkMax(RIGHT_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax motorCenter = new CANSparkMax(CENTER_MOTOR_PORT, MotorType.kBrushless);

    private final MotorControllerGroup motors = new MotorControllerGroup(new MotorController[] {
        motorLeft,
        motorRight,
        motorCenter
    });
    
    public ComplexShwooper() 
    {
        motorLeft.setInverted(TOP_OR_LEFT_INVERSION);
        motorRight.setInverted(BOTTOM_OR_RIGHT_INVERSION);
        motorCenter.setInverted(CENTER_INVERSION);
    }

    public void suck() 
    {
        motors.set(SPEED);
    }

    /**
     * Expel game pieces from intake
     */
    public void spit() 
    {
        motors.set(-SPEED);
    }

    /**
     * Stops intake motors
     */
    public void stop() 
    {
        motors.set(0);
    }

    /**
     * Extends the intake using solenoid. Solenoid is not set at the default state
     */
    public void extendShwooper() 
    {
        shwooperSolenoid.set(!SOLENOID_DEFAULT_STATE);
    }

    /**
     * Retracts the intake using solenoid. Solenoid is set at the default state
     */
    public void retractShwooper() 
    {
        shwooperSolenoid.set(SOLENOID_DEFAULT_STATE);
    }

    /**
     * toggle the Shwooper
     * <p>
     * - if it is extended, <b>retract</b>
     * <p>
     * - if it is retracted, <b>extend</b>
     */
    public void toggleShwooper() 
    {
        shwooperSolenoid.set(!shwooperSolenoid.get());
    }

    /**
     * Get if the shwooper is inside or outside of the robot. True means outside, false means inside.
     */
    public boolean isShwooperOut()
    {
        return !SOLENOID_DEFAULT_STATE;
    }
    

    
}
