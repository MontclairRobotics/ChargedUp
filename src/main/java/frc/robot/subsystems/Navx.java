package frc.robot.subsystems;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.hal.simulation.SimDeviceDataJNI;
import edu.wpi.first.hal.simulation.SimulatorJNI;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.structure.Logging;

import com.kauailabs.navx.frc.AHRS;

import org.team555.frc.command.commandrobot.CommandRobot;
import org.team555.frc.command.commandrobot.ManagerSubsystemBase;
import org.team555.frc.command.commandrobot.ManagerBase;

public class Navx extends ManagerBase
{
    private AHRS ahrs;

    public AHRS getAhrs() {return ahrs;}

    private final int simulatorDev = SimDeviceDataJNI.getSimDeviceHandle("navX-Sensor[0]");
    private final SimDouble angleSimulator = new SimDouble(SimDeviceDataJNI.getSimValueHandle(simulatorDev, "Yaw"));

    public Navx(AHRS ahrs)
    {
        this.ahrs = ahrs;

        prevAngle = getAngleRaw();
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
        prevAngle = getAngleRawUnzeroed();
    }
    
    public double getAngleRawUnzeroed()
    {
        return ahrs.getAngle();
    }
    public double getAngleRaw()
    {
        return getAngleRawUnzeroed() - angleZero;
    }

    public Rotation2d getAngle()
    {
        return Rotation2d.fromDegrees(-getAngleRaw());
    }
    public Rotation2d getAngleUnzeroed()
    {
        return Rotation2d.fromDegrees(-getAngleRawUnzeroed());
    }

    public void zeroYaw() 
    {
        angleZero = getAngleRawUnzeroed();
        prevAngle = angleZero;

        Logging.Info("Resetting yaw to zero!");
    }

    public double getAngularVelocityRaw()
    {
        return angularVelocity;
    }
    public Rotation2d getAngularVelocity()
    {
        return Rotation2d.fromDegrees(-angularVelocity);
    }

    public double setAngleRaw(double value)
    {
        angleSimulator.set(value);
        return value;
    }
    public Rotation2d setAngle(Rotation2d value)
    {
        angleSimulator.set(-value.getDegrees());
        return value;
    }

    @Override
    public void always()
    {
        var angle = getAngleRawUnzeroed();

        angularVelocity = (angle - prevAngle) / CommandRobot.deltaTime();
        prevAngle = angle;

        SmartDashboard.putString("omega", getAngularVelocity().toString());
    }
}
