package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerBase;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.ChargedUp;

public class AngularVelocityManager extends ManagerBase
{
    private double lastAHRSYawValue;
    private double currentAHRSYawValue;

    private double angularVelocity;

    public double getAngularVelocityNavx()
    {
        return angularVelocity;
    }
    public Rotation2d getAngularVelocity()
    {
        return Rotation2d.fromDegrees(-angularVelocity);
    }

    @Override
    public void always() 
    {
        if(RobotBase.isSimulation())
        {
            // Handle faulty NAVX simulation code //
            lastAHRSYawValue = currentAHRSYawValue;
            currentAHRSYawValue = ChargedUp.gyroscope.getAngle();
            
            angularVelocity = (currentAHRSYawValue - lastAHRSYawValue) / TimedRobot.kDefaultPeriod;
        }
        else
        {
            angularVelocity = ChargedUp.gyroscope.getRate();
        }

        SmartDashboard.putNumber("Angular Velocity", getAngularVelocity().getDegrees());
    }
}
