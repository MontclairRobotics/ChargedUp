package frc.robot.structure.swerve;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import com.swervedrivespecialties.swervelib.SwerveModule;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper;
import com.swervedrivespecialties.swervelib.Mk4iSwerveModuleHelper.GearRatio;

public class SwerveModuleMk4iSpec extends SwerveModuleSpec
{
    public final GearRatio ratio;

    public SwerveModuleMk4iSpec(GearRatio ratio, int driverPort, int steerPort, int steerEncoderPort, double steerOffsetDegrees)
    {
        super(driverPort, steerPort, steerEncoderPort, steerOffsetDegrees);

        this.ratio = ratio;
    }

    @Override
    public SwerveModule createNeo() 
    {
        return Mk4iSwerveModuleHelper.createNeo(ratio, driverPort, steerPort, steerEncoderPort, steerOffsetRadians);
    }
    
    @Override
    public SwerveModule createNeo(ShuffleboardLayout container) 
    {
        return Mk4iSwerveModuleHelper.createNeo(container, ratio, driverPort, steerPort, steerEncoderPort, steerOffsetRadians);
    }

    @Override
    public SwerveModule createFalconDriveNeoTurn() 
    {
        return Mk4iSwerveModuleHelper.createFalcon500Neo(ratio, driverPort, steerPort, steerEncoderPort, steerOffsetRadians);
    }
    
    @Override
    public SwerveModule createFalconDriveNeoTurn(ShuffleboardLayout container) 
    {
        return Mk4iSwerveModuleHelper.createFalcon500Neo(container, ratio, driverPort, steerPort, steerEncoderPort, steerOffsetRadians);
    }
}
