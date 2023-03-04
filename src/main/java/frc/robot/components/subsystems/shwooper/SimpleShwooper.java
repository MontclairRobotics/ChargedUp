package frc.robot.components.subsystems.shwooper;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import static frc.robot.constants.ShwooperConstants.*;

import frc.robot.constants.Ports;
import frc.robot.constants.ShwooperConstants;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;



public class SimpleShwooper extends ManagerSubsystemBase implements Shwooper
{
    private final CANSparkMax motorTop = new CANSparkMax(Ports.SHWOOPER_LEFT_MOTOR_PORT, MotorType.kBrushless);
    // private final CANSparkMax motorBottom = new CANSparkMax(Robot.Shwooper.RIGHT_MOTOR_PORT, MotorType.kBrushless);

    // private final MotorControllerGroup motors = new MotorControllerGroup(new MotorController[] {
    //     motorTop,
    //     motorBottom,
    // });
    
    public SimpleShwooper() 
    {
        motorTop.setInverted(TOP_OR_LEFT_INVERSION);
        // motorBottom.setInverted(Robot.Shwooper.RIGHT_INVERSION);
    }

    /**
     * Intake starts sucking
     */
    public void suck() 
    {
        //This could be opposite
        motorTop.set(INVERT_SIMPLE_SCHWOOPER * SPEED);
        // motorBottom.set(Robot.Shwooper.INVERT_SIMPLE_SCHWOOPER * -Robot.Shwooper.SPEED);
    }

    /**
     * Expel game pieces from intake
     */
    public void spit() 
    {
        motorTop.set(INVERT_SIMPLE_SCHWOOPER * -SPEED);
        // motorBottom.set(Robot.Shwooper.INVERT_SIMPLE_SCHWOOPER * Robot.Shwooper.SPEED);
    }

    /**
     * Stops intake motors
     */
    public void stop() 
    {
        motorTop.set(0);
    }

    public void extendShwooper(){};
    public void retractShwooper(){};
    public void toggleShwooper(){};
    
    public boolean isShwooperOut(){return true;}
 
}
