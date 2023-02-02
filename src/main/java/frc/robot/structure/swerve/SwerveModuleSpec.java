package frc.robot.structure.swerve;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

import com.swervedrivespecialties.swervelib.MechanicalConfiguration;
import com.swervedrivespecialties.swervelib.MkSwerveModuleBuilder;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SwerveModule;

public class SwerveModuleSpec 
{
    public final int driverPort;
    public final int steerPort;
    public final int steerEncoderPort;
    public final double steerOffsetRadians;

    public final MotorType driverType;
    public final MotorType steerType;

    public final MechanicalConfiguration config;

    public SwerveModuleSpec(MechanicalConfiguration config, MotorType driverType, int driverPort, MotorType steerType, int steerPort, int steerEncoderPort, double steerOffsetDegrees)
    {
        this.driverPort = driverPort;
        this.steerPort = steerPort;
        this.steerEncoderPort = steerEncoderPort;
        this.steerOffsetRadians = -Math.toRadians(steerOffsetDegrees);

        this.driverType = driverType;
        this.steerType = steerType;

        this.config = config;
    }

    /**
     * Get the builder object which contains all information about the swerve module.
     */
    public MkSwerveModuleBuilder builder()
    {
        return new MkSwerveModuleBuilder()
            .withDriveMotor(driverType, driverPort)
            .withSteerMotor(steerType, driverPort)
            .withSteerEncoderPort(steerEncoderPort)
            .withSteerOffset(steerOffsetRadians)
            .withGearRatio(config);
    }

    public SwerveModule build()
    {
        return builder().build();
    }

    public SwerveModule build(ShuffleboardLayout layout)
    {
        return builder().withLayout(layout).build();
    }
}
