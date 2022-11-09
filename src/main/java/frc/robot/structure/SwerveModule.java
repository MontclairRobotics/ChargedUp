package frc.robot.structure;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.team555.math.MathUtils;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class SwerveModule 
{
    private final CANSparkMax rotator;
    private final CANSparkMax driver;

    private final RelativeEncoder rotatorEncoder;
    private final RelativeEncoder driverEncoder;

    private final PIDController anglePID;

    public SwerveModule(
        int rotatorID, MotorType rotatorType, 
        int driverID, MotorType driverType,
        double kAngleP, double kAngleI, double kAngleD
    )
    {
        this.rotator = new CANSparkMax(rotatorID, rotatorType);
        this.driver  = new CANSparkMax(driverID,  driverType );

        rotatorEncoder = rotator.getEncoder();
        driverEncoder  = driver.getEncoder();

        anglePID = new PIDController(kAngleP, kAngleI, kAngleD);
        anglePID.enableContinuousInput(0, 1);
    }

    public void setState(SwerveModuleState state)
    {
        // Calculate rotator value
        var rotatorSpeed = anglePID.calculate(rotatorEncoder.getPosition(), state.angle.getDegrees() / 360);
        rotator.set(rotatorSpeed);

        // Calculate driver value
        driver.set(MathUtils.clamp(//TODO: finish));
    }
}
