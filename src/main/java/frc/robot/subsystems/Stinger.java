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
    public final CANSparkMax stingerMotor = new CANSparkMax (Robot.Stinger.MOTOR_PORT, MotorType.kBrushless);
    PIDMechanism StingerPID = new PIDMechanism(Robot.Stinger.inout());
    RelativeEncoder StingerEncoder = stingerMotor.getEncoder();

    public Stinger()
    {
        StingerEncoder.setPositionConversionFactor(Robot.Stinger.IN_OUT_CONVERSION_FACTOR);
    }
    /**
     * Extends Stinger to a desired length
     * @param length (double) the desired length 
     */
    private void extendToLength(double length)
    {
        if (length > Robot.Stinger.EXT_LENGTH)
            length = Robot.Stinger.EXT_LENGTH;
        StingerPID.setTarget(length);
    }
    /**
     * Extend the stinger to the {@link Robot#HIGH_LENGTH_MUL the high Length}
     */
    public void toHigh()
    {
        extendToLength(Robot.Stinger.HIGH_LENGTH_MUL);
    }
    /**
     * Extend the stinger to the {@link Robot#MID_LENGTH_MUL the middle Length}
     */
    public void toMid()
    {
        extendToLength(Robot.Stinger.MID_LENGTH_MUL);
    }
    /**
     *  Fully Retracts Stinger
     */
    public void fullyRetract()
    {
        StingerPID.setTarget(0);
    }
    /**
     * Manually extends Stinger at {@link Robot#UP_DOWN_SPEED the Stinger Speed} 
     */
    public void startExtending()
    {
        StingerPID.setSpeed(Robot.Stinger.SPEED);
    }
    /**
     * Manually retracts Stinger at the negative of {@link Robot#UP_DOWN_SPEED the Stinger Speed}
     */
    public void startRetracting()
    {
        StingerPID.setSpeed(-Robot.Stinger.SPEED);
    }
    /**
     * Stops the Stinger
     */
    public void stop()
    {
        StingerPID.setSpeed(0);
    }

    /**
     * Set the speed of the Stinger
     * @param speed desired speed
     */
    public void setSpeed(double speed) 
    {
        StingerPID.setSpeed(speed);
    }
    
    /**
     * is currently not pidding?
     * @return <code>true</code> if not pidding, <code>false</code> if pidding to a value
     */
    public boolean isPIDFree()
    {
        return !StingerPID.active();
    }

    /**
     * Cancel the PID
     */
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
