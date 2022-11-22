package frc.robot.structure;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.swervelib.SwerveModule;

public abstract class SwerveModuleSpec 
{
    public final int driverPort;
    public final int steerPort;
    public final int steerEncoderPort;
    public final double steerOffsetRadians;
    public final String namePrefix;

    public SwerveModuleSpec(int driverPort, int steerPort, int steerEncoderPort, double steerOffsetDegrees, String namePrefix)
    {
        this.driverPort = driverPort;
        this.steerPort = steerPort;
        this.steerEncoderPort = steerEncoderPort;
        this.steerOffsetRadians = -Math.toRadians(steerOffsetDegrees);
        this.namePrefix = namePrefix;
    }

    public abstract SwerveModule createNeo();
    public abstract SwerveModule createNeo(ShuffleboardLayout container);
}
