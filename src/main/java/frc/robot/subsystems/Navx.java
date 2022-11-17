package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.kauailabs.navx.frc.AHRS;

import org.team555.frc.command.commandrobot.CommandRobot;
import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
import org.team555.frc.command.commandrobot.ManagerBase;

public class Navx extends ManagerBase
{
    private AHRS ahrs;

    public Navx(AHRS ahrs)
    {
        this.ahrs = ahrs;

        prevAngle = getAngle();
        angularVelocity = 0;
    }

    public Navx()
    {
        this(new AHRS());
    }

    private double angleZero = 0;
    private double prevAngle;
    private double angularVelocity;

    @Override
    public void reset() 
    {
        calibrate();
    }

    public void calibrate() 
    {
        ahrs.calibrate();
        prevAngle = getAngleUnzeroed();
    }    
    
    public double getAngleUnzeroed()
    {
        return ahrs.getAngle();
    }
    public double getAngle()
    {
        return getAngleUnzeroed() - angleZero;
    }

    public void zeroYaw() 
    {
        angleZero = getAngleUnzeroed();
        prevAngle = angleZero;

        System.out.println("!!! RESETTING NAVX YAW !!!");
    }

    public double getAngularVelocity()
    {
        return angularVelocity;
    }

    @Override
    public void always()
    {
        var angle = getAngleUnzeroed();

        angularVelocity = (angle - prevAngle) / CommandRobot.deltaTime();
        prevAngle = angle;
    }
}
