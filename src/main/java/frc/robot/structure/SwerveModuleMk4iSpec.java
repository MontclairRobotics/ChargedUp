package frc.robot.structure;
import frc.swervelib.SwerveModule;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.swervelib.Mk4ModuleConfiguration;
import frc.swervelib.Mk4iSwerveModuleHelper;
import frc.swervelib.Mk4iSwerveModuleHelper.GearRatio;

public class SwerveModuleMk4iSpec extends SwerveModuleSpec
{
    public final GearRatio ratio;
    public final Mk4ModuleConfiguration configuration;

    public SwerveModuleMk4iSpec(GearRatio ratio, int driverPort, int steerPort, int steerEncoderPort, double steerOffsetDegrees, String namePrefix)
    {
        super(driverPort, steerPort, steerEncoderPort, steerOffsetDegrees, namePrefix);

        this.ratio = ratio;
        this.configuration = new Mk4ModuleConfiguration();
    }

    @Override
    public SwerveModule createNeo() 
    {
        return Mk4iSwerveModuleHelper.createNeo(configuration, ratio, driverPort, steerPort, steerEncoderPort, steerOffsetRadians, namePrefix);
    }
    
    @Override
    public SwerveModule createNeo(ShuffleboardLayout container) 
    {
        return Mk4iSwerveModuleHelper.createNeo(container, configuration, ratio, driverPort, steerPort, steerEncoderPort, steerOffsetRadians, namePrefix);
    }
}
