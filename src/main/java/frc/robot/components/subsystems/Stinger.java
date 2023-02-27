package frc.robot.components.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVPhysicsSim;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
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
import frc.robot.util.frc.LimitSwitch;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.SimulationUtility;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

public class Stinger extends ManagerSubsystemBase
{
    private boolean shouldStop; 

    private CANSparkMax motor;
    RelativeEncoder encoder;

    private LimitSwitch outerlimitSwitch = new LimitSwitch(Robot.Stinger.OUTER_LIMIT_SWITCH);
    private LimitSwitch innerlimitSwitch = new LimitSwitch(Robot.Stinger.INNER_LIMIT_SWITCH);
    
    public final PIDMechanism PID = new PIDMechanism(Robot.Stinger.inout());

    MechanismLigament2d widthLigament;
    MechanismLigament2d[][] ligaments;

    public Stinger()
    {
        motor = new CANSparkMax(Robot.Stinger.MOTOR_PORT, MotorType.kBrushless);

        if(RobotBase.isSimulation())
        {
            REVPhysicsSim.getInstance().addSparkMax(motor, DCMotor.getNEO(1));
        }

        encoder = motor.getEncoder();
        encoder.setPositionConversionFactor(Robot.Stinger.LEAD_SCREW_FACTOR);
        encoder.setPosition(Robot.Stinger.stngDistToLeadDist(Robot.Stinger.MIN_LENGTH));


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

    /**
     * Extends Stinger to a desired length
     * @param length (double) the desired length 
     */
    private void extendToLength(double length)
    {
        length = Math555.clamp(length, Robot.Stinger.MIN_LENGTH, Robot.Stinger.MAX_LENGTH);

        PID.setTarget(length);
    }

    /**
     * Extend the stinger to the {@link Robot#HIGH_LENGTH_MUL the high Length}
     */
    public void toHigh()
    {
        extendToLength(Robot.Stinger.MIN_LENGTH + Robot.Stinger.HIGH_LENGTH_MUL*Robot.Stinger.EXT_LENGTH);
    }

    /**
     * Extend the stinger to the {@link Robot#MID_LENGTH_MUL the middle Length}
     */
    public void toMid()
    {
        extendToLength(Robot.Stinger.MIN_LENGTH + Robot.Stinger.MID_LENGTH_MUL*Robot.Stinger.EXT_LENGTH);
    }

    /**
     *  Fully Retracts Stinger
     */
    public void toRetract()
    {
        extendToLength(Robot.Stinger.MIN_LENGTH);
    }

    /**
     * Manually extends Stinger at {@link Robot#UP_DOWN_SPEED the Stinger Speed} 
     */
    public void startExtending()
    {
        PID.setSpeed(Robot.Stinger.SPEED);
    }

    /**
     * Manually retracts Stinger at the negative of {@link Robot#UP_DOWN_SPEED the Stinger Speed}
     */
    public void startRetracting()
    {
        PID.setSpeed(-Robot.Stinger.SPEED);
    }

    /**
     * Stops the Stinger
     */
    public void stop()
    {
        PID.setSpeed(0);
    }

    /**
     * Set the speed of the Stinger
     * @param speed desired speed
     */
    public void setSpeed(double speed) 
    {
        PID.setSpeed(speed);
    }
    
    /**
     * is currently not pidding?
     * @return <code>true</code> if not pidding, <code>false</code> if pidding to a value
     */
    public boolean isPIDFree()
    {
        return !PID.active();
    }

    /**
     * Cancel the {@link Stinger#PID stingerPID}
     */
    public void stopPIDing()
    {
        PID.cancel();
    }

    /**
     * Get the extension of the stinger
     */
    public double getExtension()
    {
        return Robot.Stinger.leadDistToStngDist(encoder.getPosition());
    }
    
    /**
     * Set the motor based on the inputed Stinger extension velocity
     */
    public void setMotor(double stingerVel)
    {
        double speed;

        if(encoder.getPosition() > Robot.Stinger.SEGMENT_LENGTH - 0.01)
        {
            //this number has origins from within my rectal cavity
            speed = -Math.signum(stingerVel) * 0.05;
        }
        else 
        {
            speed = Robot.Stinger.stingerVelToMotorVel(encoder.getPosition(), stingerVel);

            // make sure the thing isn't doing what we don't want it to do
            // set the motor speed to a really tiny number when the desired speed is a really small ass value
            if(Math.abs(speed) < 0.05 && Math.abs(stingerVel) > 0.01)
            {
                speed = -Math.signum(stingerVel) * 0.05;
            }
        }

        speed = Math555.clamp1(speed);

        motor.set(speed);
    }

    @Override
    public void always() 
    {
        // UPDATE REAL //
        shouldStop = false;

        if (PID.getSpeed() > 0) 
        {
            if (outerlimitSwitch.get()) 
            {
                shouldStop = true;
                PID.setSpeed(0);
            }
        } 
        else 
        {
            if (innerlimitSwitch.get()) 
            {
                shouldStop = true;
                PID.setSpeed(0);
            }
        }

        PID.setMeasurement(getExtension());
        PID.update();
        
        if(shouldStop) motor.set(0);
        else setMotor(PID.getSpeed());
        // Logging.info("" + PID.getSpeed());

        // Logging.info("--------------------------------------------------");
        // Logging.info("speed = " + PID.getSpeed());
        // Logging.info("pos   = " + getExtension());
        // Logging.info("rapos = " + encoder.getPosition());
        


        if(RobotBase.isSimulation())
        {
            SimulationUtility.simulateNEO(motor, encoder);
            encoder.setPosition(Math555.clamp(encoder.getPosition(), 0.001, Robot.Stinger.SEGMENT_LENGTH-0.001));
            // Logging.info("positione: " + encoder.getPosition());

        }

        // UPDATE SIMULATION IMAGES //
        double currentPosition = encoder.getPosition();

        widthLigament.setLength(currentPosition);
        double mechAngle = Math.toDegrees(Math.asin(currentPosition / Robot.Stinger.SEGMENT_LENGTH));

        for(int i = 0; i < ligaments.length; i++)
        {
            double[] angle = {0, 0};
            if(i == 0)          {angle[0] = +mechAngle;     angle[1] = -mechAngle;    }
            else if(i % 2 == 0) {angle[0] = +mechAngle * 2; angle[1] = -mechAngle * 2;}
            else                {angle[0] = -mechAngle * 2; angle[1] = +mechAngle * 2;}

            ligaments[i][0].setAngle(angle[0]);
            ligaments[i][1].setAngle(angle[1]);
        }
    }
}
