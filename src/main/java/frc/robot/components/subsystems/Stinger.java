package frc.robot.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismObject2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.ChargedUp;
import frc.robot.Constants.Robot;
import frc.robot.Constants.Simulation;
import frc.robot.math.Math555;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

public class Stinger extends ManagerSubsystemBase
{
    private boolean exists;

    private boolean shouldStop; 

    private CANSparkMax motor;
    RelativeEncoder encoder;

    private DigitalInput outerlimitSwitch = new DigitalInput(Robot.Stinger.OUTER_LIMIT_SWITCH);
    private DigitalInput innerlimitSwitch = new DigitalInput(Robot.Stinger.INNER_LIMIT_SWITCH);
    
    PIDMechanism stingerPID = new PIDMechanism(Robot.Stinger.inout());

    double mechX;
    MechanismLigament2d widthLigament;
    MechanismLigament2d[][] ligaments;

    public Stinger(boolean exists)
    {
        this.exists = exists;
        if(!exists) return;

        motor = new CANSparkMax(Robot.Stinger.MOTOR_PORT, MotorType.kBrushless);

        encoder = motor.getEncoder();
        encoder.setPositionConversionFactor(Robot.Stinger.LEAD_SCREW_FACTOR);

        ligaments = new MechanismLigament2d[9][2];

        MechanismObject2d root = ChargedUp.elevator.getMechanismObject().append(new MechanismLigament2d("Stinger::root", 0, -90));
        
        widthLigament = root.append(new MechanismLigament2d("Stinger::width", 0, 90));
        widthLigament.setColor(new Color8Bit(Simulation.STINGER_COLOR));

        MechanismLigament2d lastObjectUp   = root         .append(new MechanismLigament2d("Stinger::up::segment <root>",   0, 0));
        MechanismLigament2d lastObjectDown = widthLigament.append(new MechanismLigament2d("Stinger::down::segment <root>", 0, -90));

        double mechAngle = 0;
        
        for(int i = 0; i < ligaments.length; i++)
        {
            double[] angle = {0, 0};
            if(i == 0)          {angle[0] = +mechAngle;     angle[1] = -mechAngle;}
            else if(i % 2 == 0) {angle[0] = +mechAngle * 2; angle[1] = -mechAngle * 2;}
            else                {angle[0] = -mechAngle * 2; angle[1] = +mechAngle * 2;}

            lastObjectUp   = lastObjectUp  .append(new MechanismLigament2d("Stinger::up::segment " + i,   Robot.Stinger.SEGMENT_LENGTH, angle[0]));
            lastObjectDown = lastObjectDown.append(new MechanismLigament2d("Stinger::down::segment " + i, Robot.Stinger.SEGMENT_LENGTH, angle[1]));
            
            ligaments[i][0] = lastObjectUp;
            ligaments[i][1] = lastObjectDown;

            ligaments[i][0].setLineWeight(2);
            ligaments[i][1].setLineWeight(2);
            
            ligaments[i][0].setColor(new Color8Bit(Simulation.STINGER_COLOR));
            ligaments[i][1].setColor(new Color8Bit(Simulation.STINGER_COLOR));
        }
    }
    public Stinger() {this(true);}

    /**
     * Extends Stinger to a desired length
     * @param length (double) the desired length 
     */
    private void extendToLength(double length)
    {
        length = Math555.clamp(length, Robot.Stinger.MIN_LENGTH, Robot.Stinger.EXT_LENGTH);

        stingerPID.setTarget(Robot.Stinger.stngDistToLeadDist(length));
    }

    /**
     * Get the current distance outward which the stinger is extended.
     */
    public double getLength()
    {
        return Robot.Stinger.leadDistToStngDist(encoder.getPosition());
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
        stingerPID.setTarget(0);
    }

    /**
     * Manually extends Stinger at {@link Robot#UP_DOWN_SPEED the Stinger Speed} 
     */
    public void startExtending()
    {
        stingerPID.setSpeed(Robot.Stinger.SPEED);
    }

    /**
     * Manually retracts Stinger at the negative of {@link Robot#UP_DOWN_SPEED the Stinger Speed}
     */
    public void startRetracting()
    {
        stingerPID.setSpeed(-Robot.Stinger.SPEED);
    }

    /**
     * Stops the Stinger
     */
    public void stop()
    {
        stingerPID.setSpeed(0);
    }

    /**
     * Set the speed of the Stinger
     * @param speed desired speed
     */
    public void setSpeed(double speed) 
    {
        stingerPID.setSpeed(speed);
    }
    
    /**
     * is currently not pidding?
     * @return <code>true</code> if not pidding, <code>false</code> if pidding to a value
     */
    public boolean isPIDFree()
    {
        return !stingerPID.active();
    }

    /**
     * Cancel the {@link Stinger#stingerPID stingerPID}
     */
    public void stopPIDing()
    {
        stingerPID.cancel();
    }

    @Override
    public void always() 
    {
        shouldStop = false;

        if(!exists) return;

        if (stingerPID.getSpeed() > 0) 
        {
            if (outerlimitSwitch.get()) 
            {
                shouldStop = true;
                stingerPID.setSpeed(0);
            }
        } 
        else 
        {
            if (innerlimitSwitch.get()) 
            {
                shouldStop = true;
                stingerPID.setSpeed(0);
            }
        }

        stingerPID.setMeasurement(encoder.getPosition());
        stingerPID.update();
        
        if(shouldStop) motor.set(0);
        else motor.set(stingerPID.getSpeed());

        widthLigament.setLength(mechX);
        double mechAngle = Math.toDegrees(Math.asin(mechX / Robot.Stinger.SEGMENT_LENGTH));

        for(int i = 0; i < ligaments.length; i++)
        {
            double[] angle = {0, 0};
            if(i == 0)          {angle[0] = +mechAngle;     angle[1] = -mechAngle;}
            else if(i % 2 == 0) {angle[0] = +mechAngle * 2; angle[1] = -mechAngle * 2;}
            else                {angle[0] = -mechAngle * 2; angle[1] = +mechAngle * 2;}

            ligaments[i][0].setAngle(angle[0]);
            ligaments[i][1].setAngle(angle[1]);
        }

        mechX += 0.002;
        if(mechX > Robot.Stinger.SEGMENT_LENGTH) mechX = 0;
    }
}
