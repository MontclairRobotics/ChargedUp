package frc.robot.structure;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team555.math.MathUtils;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import frc.robot.constants.Drivebase;

import static org.team555.units.StandardUnits.*;

public class SwerveModule 
{
    private final CANSparkMax rotator;
    private final CANSparkMax driver;

    private final RelativeEncoder rotatorEncoder;
    private final RelativeEncoder driverEncoder;

    private final PIDController anglePID;

    public SwerveModule(int rid, MotorType rtype, int did, MotorType dtype)
    {
        // Setup motors
        this.rotator = new CANSparkMax(rid, rtype);
        this.driver  = new CANSparkMax(did, dtype);


        // Setup encoder
        rotatorEncoder = rotator.getEncoder();
        driverEncoder  = driver.getEncoder();

        driverEncoder.setPositionConversionFactor(Drivebase.CONVERSION_RATE.value(meter));


        // Setup PID
        anglePID = new PIDController(
            Drivebase.ANGLE_P, 
            Drivebase.ANGLE_I, 
            Drivebase.ANGLE_D
        );

        anglePID.enableContinuousInput(0, 1);
    }

    public void setState(SwerveModuleState state)
    {
        // Calculate rotator value
        var rotatorSpeed = anglePID.calculate(rotatorEncoder.getPosition(), state.angle.getDegrees() / 360);
        rotator.set(rotatorSpeed);

        // Calculate driver value
        driver.set(MathUtil.clamp(
            state.speedMetersPerSecond / Drivebase.CONVERSION_RATE_FROM_ROTATION.value * Drivebase.MOTOR_RPM.value, 
            -1, 1
        ));
        // TODO: make sure this does not commit war crimes

    }
}
