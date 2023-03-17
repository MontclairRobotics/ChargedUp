package org.team555.util.frc;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

import com.swervedrivespecialties.swervelib.MechanicalConfiguration;
import com.swervedrivespecialties.swervelib.MkModuleConfiguration;
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

    public final boolean driveInvert;
    public final boolean steerInvert;

    public final MechanicalConfiguration config;

    public SwerveModuleSpec(
        MechanicalConfiguration config, 
        MotorType driverType, int driverPort, boolean driveInvert, 
        MotorType steerType, int steerPort, boolean steerInvert, 
        int steerEncoderPort, double steerOffsetDegrees
    )
    {
        this.driverPort = driverPort;
        this.steerPort = steerPort;
        this.steerEncoderPort = steerEncoderPort;
        this.steerOffsetRadians = -Math.toRadians(steerOffsetDegrees);

        this.driverType = driverType;
        this.steerType = steerType;

        this.config = config;

        this.driveInvert = driveInvert;
        this.steerInvert = steerInvert;
    }

    /**
     * Get the builder object which contains all information about the swerve module.
     */
    private MkSwerveModuleBuilder builder()
    {
        MkModuleConfiguration modConfig = MkModuleConfiguration.getDefaultSteerNEO();
        modConfig.setDriveCurrentLimit(40.0);
        modConfig.setSteerCurrentLimit(30.0);

        MechanicalConfiguration newConfig = new MechanicalConfiguration(
            config.getWheelDiameter(), 
            config.getDriveReduction(), 
            config.isDriveInverted() ^ driveInvert, 
            config.getSteerReduction(), 
            config.isSteerInverted() ^ steerInvert);
        
        return new MkSwerveModuleBuilder(modConfig)
            .withGearRatio(newConfig)
            .withDriveMotor(driverType, driverPort)
            .withSteerMotor(steerType, steerPort)
            .withSteerEncoderPort(steerEncoderPort)
            .withSteerOffset(RobotBase.isReal() ? steerOffsetRadians : 0);
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
