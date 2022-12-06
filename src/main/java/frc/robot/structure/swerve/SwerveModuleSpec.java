package frc.robot.structure.swerve;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import com.swervedrivespecialties.swervelib.SwerveModule;

public abstract class SwerveModuleSpec 
{
    public final int driverPort;
    public final int steerPort;
    public final int steerEncoderPort;
    public final double steerOffsetRadians;

    public SwerveModuleSpec(int driverPort, int steerPort, int steerEncoderPort, double steerOffsetDegrees)
    {
        this.driverPort = driverPort;
        this.steerPort = steerPort;
        this.steerEncoderPort = steerEncoderPort;
        this.steerOffsetRadians = -Math.toRadians(steerOffsetDegrees);
    }

    public abstract SwerveModule createNeo();
    public abstract SwerveModule createNeo(ShuffleboardLayout container);
}
