package frc.robot.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import frc.robot.Constants.*;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;



public class Shwooper extends ManagerSubsystemBase 
{
    private final Solenoid shwooperSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Robot.Shwooper.PNEU_PORT);

    private final CANSparkMax motorLeft = new CANSparkMax(Robot.Shwooper.LEFT_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax motorRight = new CANSparkMax(Robot.Shwooper.RIGHT_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax motorCenter = new CANSparkMax(Robot.Shwooper.CENTER_MOTOR_PORT, MotorType.kBrushless);

    private final MotorControllerGroup motors = new MotorControllerGroup(new MotorController[] {
        motorLeft,
        motorRight,
        motorCenter
    });
    
    public Shwooper() 
    {
        motorLeft.setInverted(Robot.Shwooper.LEFT_INVERSION);
        motorRight.setInverted(Robot.Shwooper.RIGHT_INVERSION);
        motorCenter.setInverted(Robot.Shwooper.CENTER_INVERSION);
    }

    /**
     * Intake starts sucking
     */
    public void suck() 
    {
        motors.set(Robot.Shwooper.SPEED);
    }

    /**
     * Expel game pieces from intake
     */
    public void spit() 
    {
        motors.set(-Robot.Shwooper.SPEED);
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
