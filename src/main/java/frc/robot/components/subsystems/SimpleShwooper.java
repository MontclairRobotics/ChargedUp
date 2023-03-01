package frc.robot.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import static frc.robot.constants.ShwooperConstants.*;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;



public class SimpleShwooper extends ManagerSubsystemBase implements Shwooper
{
    private final CANSparkMax motorTop = new CANSparkMax(LEFT_MOTOR_PORT, MotorType.kBrushless);
    private final CANSparkMax motorBottom = new CANSparkMax(RIGHT_MOTOR_PORT, MotorType.kBrushless);

    private final MotorControllerGroup motors = new MotorControllerGroup(new MotorController[] {
        motorTop,
        motorBottom,
    });
    
    public SimpleShwooper() 
    {
        motorTop   .setInverted(TOP_OR_LEFT_INVERSION);
        motorBottom.setInverted(BOTTOM_OR_RIGHT_INVERSION);
    }

    /**
     * Intake starts sucking
     */
    public void suck() 
    {
        //This could be opposite
        motorTop.set(SPEED);
        motorBottom.set(SPEED);
    }

    /**
     * Expel game pieces from intake
     */
    public void spit() 
    {
        motorTop.set(SPEED);
        motorBottom.set(SPEED);
    }

    /**
     * Stops intake motors
     */
    public void stop() 
    {
        motors.set(0);
    }

    public void extendShwooper(){};
    public void retractShwooper(){};
    public void toggleShwooper(){};
    
    public boolean isShwooperOut(){return true;};
 
}
