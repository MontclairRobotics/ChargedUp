package frc.robot.subsystems;

import org.team555.frc.command.commandrobot.ManagerSubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.constants.Constants.Robot;
import frc.robot.structure.PIDMechanism;

public class Stinger extends ManagerSubsystemBase{
    public final CANSparkMax stingerMotor = new CANSparkMax (Robot.STINGER_PORT, MotorType.kBrushless);
    PIDMechanism StingerPID = new PIDMechanism(Robot.stingerInOut());
    RelativeEncoder StingerEncoder = stingerMotor.getEncoder();

    public Stinger()
    {
        StingerEncoder.setPositionConversionFactor(Robot.STINGER_IN_OUT_CONVERSION_FACTOR);
    }
    /**
     * Extends Stinger to a desired length
     * @param length (double) the desired length 
     */
    private void extendToLength(double length)
    {
        if (length > Robot.STINGER_EXTENSION_LENGTH)
            length = Robot.STINGER_EXTENSION_LENGTH;
        StingerPID.setTarget(length);
    }
    public void toHigh()
    {
        extendToLength(Robot.STINGER_HIGH_LENGTH);
    }
    public void toMid()
    {
        extendToLength(Robot.STINGER_MID_LENGTH);
    }
    /**
     *  Fully Retracts Stinger
     */
    public void fullyRetract()
    {
        StingerPID.setTarget(0);
    }
    /**
     * Manually extends Stinger at {@link Robot#STINGER_SPEED the Stinger Speed} 
     */
    public void startExtending()
    {
        StingerPID.setSpeed(Robot.STINGER_SPEED);
    }
    /**
     * Manually retracts Stinger at the negative of {@link Robot#STINGER_SPEED the Stinger Speed}
     */
    public void startRetracting()
    {
        StingerPID.setSpeed(-Robot.STINGER_SPEED);
    }
    /**
     * Stops the Stinger
     */
    public void stop()
    {
        StingerPID.setSpeed(0);
    }

    public void setSpeed(double speed) {
        StingerPID.setSpeed(speed);
    }

    public boolean isPIDFree()
    {
        return !StingerPID.active();
    }
    public void stopPIDing()
    {
        StingerPID.cancel();
    }
    @Override
    public void always() {
        StingerPID.setMeasurement(StingerEncoder.getPosition());
        StingerPID.update();
        stingerMotor.set(StingerPID.getSpeed());
    }
}
