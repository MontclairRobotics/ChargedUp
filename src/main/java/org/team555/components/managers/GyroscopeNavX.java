package org.team555.components.managers;

import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import org.team555.util.frc.Logging;
import org.team555.util.frc.commandrobot.ManagerBase;

public class GyroscopeNavX extends ManagerBase
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

    public void setSouth()
    {
        setNorth();
        setAddition(Rotation2d.fromDegrees(180));
    }

    public Rotation2d getRotation2d()
    {
        return Rotation2d.fromDegrees(360 - (navx.getAngle() - zeroOffset) + addition); //TODO: this is dumb
    }
    public Rotation2d getRotationInCircle()
    {
        return Rotation2d.fromDegrees(getRotation2d().getDegrees() % 360); //TODO: this is dumb
    }

    public double getPitch() {return navx.getPitch() - 1.7;}
    public double getRoll()  {return navx.getRoll() - 2;}

    LinearFilter avg = LinearFilter.movingAverage(20);
    double rollRate = 0;

    double prevRoll = 0;
    double currRoll = 0;

    public double getRollRate()
    {
        return rollRate;
    }

    @Override
    public void always() 
    {
        prevRoll = currRoll;
        currRoll = getRoll();

        double change = 180 - Math.abs(Math.abs(currRoll%360 - prevRoll%360) - 180);

        rollRate = avg.calculate(change / 0.02);
    }

    
}
