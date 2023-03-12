package frc.robot.components.managers;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;

public class GyroscopeNavX
{
    final AHRS navx = new AHRS();
    double zeroOffset = 0;
    double addition   = 0;

    public void setNorth()
    {
        zeroOffset = navx.getAngle();
    }
    public void setAddition(Rotation2d ofs)
    {
        addition = ofs.getDegrees();
    }

    public Rotation2d getRotation2d()
    {
        return Rotation2d.fromDegrees(navx.getAngle() - zeroOffset + addition);
    }

    public double getPitch() {return navx.getPitch();}
    public double getRoll()  {return navx.getRoll();}
}
