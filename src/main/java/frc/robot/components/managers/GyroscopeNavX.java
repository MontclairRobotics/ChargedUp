package frc.robot.components.managers;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.util.frc.Logging;

public class GyroscopeNavX
{
    final AHRS navx = new AHRS();
    double zeroOffset = 0;
    double addition   = 0;

    public void calibrate()
    {
        navx.calibrate();
        while(navx.isCalibrating())
        {
            Timer.delay(0.2);
        }
    }

    public void setNorth()
    {
        zeroOffset = navx.getAngle();
        addition   = 0;

        Logging.info("Set gyroscope to the north direction!");
    }
    public void setAddition(Rotation2d ofs)
    {
        addition = ofs.getDegrees();
        Logging.info("Set gyroscope addition to " + addition + " degrees!");
    }

    public Rotation2d getRotation2d()
    {
        return Rotation2d.fromDegrees(360 - (navx.getAngle() - zeroOffset) + addition); //TODO: this is dumb
    }

    public double getPitch() {return navx.getPitch();}
    public double getRoll()  {return navx.getRoll();}
}
