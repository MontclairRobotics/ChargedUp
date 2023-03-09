package frc.robot.components.subsystems.stinger;

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
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.ChargedUp;
import frc.robot.Commands2023;
import frc.robot.constants.StingerConstants;
import frc.robot.constants.Ports;
import frc.robot.constants.SimulationConstants;
import frc.robot.math.Math555;
import frc.robot.util.frc.LimitSwitch;
import frc.robot.util.frc.Logging;
import frc.robot.util.frc.PIDMechanism;
import frc.robot.util.frc.SimulationUtility;
import frc.robot.util.frc.commandrobot.ManagerSubsystemBase;

import static frc.robot.constants.StingerConstants.*;

public class MotorStinger extends ManagerSubsystemBase implements Stinger
{
    private boolean shouldStop; 

    private CANSparkMax motor;
    RelativeEncoder encoder;

    private LimitSwitch outerlimitSwitch = new LimitSwitch(OUTER_LIMIT_SWITCH, true);
    private LimitSwitch innerlimitSwitch = new LimitSwitch(INNER_LIMIT_SWITCH, true);
    
    public final PIDMechanism PID = new PIDMechanism(inout());

    MechanismLigament2d widthLigament;
    MechanismLigament2d[][] ligaments;

    public MotorStinger()
    {
        motor = new CANSparkMax(Ports.STINGER_MOTOR_PORT, MotorType.kBrushless);

        PID.disableOutputClamping();

        if(RobotBase.isSimulation())
        {
            REVPhysicsSim.getInstance().addSparkMax(motor, DCMotor.getNEO(1));
        }

        encoder = motor.getEncoder();
        encoder.setPosition(StingerMath.leadToMotor(StingerMath.stingerToLead(MIN_LENGTH)));


        ligaments = new MechanismLigament2d[9][2];

        MechanismObject2d root = ChargedUp.elevator.getMechanismObject().append(new MechanismLigament2d("Stinger::root", 0, -90));
        
        widthLigament = root.append(new MechanismLigament2d("Stinger::width", 0, 90));
        widthLigament.setColor(new Color8Bit(SimulationConstants.STINGER_COLOR));

        MechanismLigament2d lastObjectUp   = root         .append(new MechanismLigament2d("Stinger::up::segment <root>",   0, 0));
        MechanismLigament2d lastObjectDown = widthLigament.append(new MechanismLigament2d("Stinger::down::segment <root>", 0, -90));

        double mechAngle = 0;
        
        for(int i = 0; i < ligaments.length; i++)
        {
            double[] angle = {0, 0};
            if(i == 0)          {angle[0] = +mechAngle;     angle[1] = -mechAngle;}
            else if(i % 2 == 0) {angle[0] = +mechAngle * 2; angle[1] = -mechAngle * 2;}
            else                {angle[0] = -mechAngle * 2; angle[1] = +mechAngle * 2;}

            lastObjectUp   = lastObjectUp  .append(new MechanismLigament2d("Stinger::up::segment " + i,   StingerMath.SEGMENT_LENGTH, angle[0]));
            lastObjectDown = lastObjectDown.append(new MechanismLigament2d("Stinger::down::segment " + i, StingerMath.SEGMENT_LENGTH, angle[1]));
            
            ligaments[i][0] = lastObjectUp;
            ligaments[i][1] = lastObjectDown;

            ligaments[i][0].setLineWeight(2);
            ligaments[i][1].setLineWeight(2);
            
            ligaments[i][0].setColor(new Color8Bit(SimulationConstants.STINGER_COLOR));
            ligaments[i][1].setColor(new Color8Bit(SimulationConstants.STINGER_COLOR));
        }
    }

    /**
     * Extends Stinger to a desired length
     * @param length (double) the desired length 
     */
    private void extendToLength(double length)
    {
        length = Math555.clamp(length, MIN_LENGTH, MAX_LENGTH);

        PID.setTarget(length);
    }

    /**
     * Extend the stinger to the {@link Robot#HIGH_LENGTH_MUL the high Length}
     */
    public void toHigh()
    {
        extendToLength(MIN_LENGTH + HIGH_LENGTH_MUL*EXT_LENGTH);
    }

    /**
     * Extend the stinger to the {@link Robot#MID_LENGTH_MUL the middle Length}
     */
    public void toMid()
    {
        extendToLength(MIN_LENGTH + MID_LENGTH_MUL*EXT_LENGTH);
    }
    
    /**
     *  Fully Retracts Stinger
     */
    public void toRetract()
    {
        extendToLength(MIN_LENGTH);
    }

    /**
     * Manually extends Stinger at {@link Robot#UP_DOWN_SPEED the Stinger Speed} 
     */
    public void startExtending()
    {
        PID.setSpeed(SPEED);
    }

    /**
     * Manually retracts Stinger at the negative of {@link Robot#UP_DOWN_SPEED the Stinger Speed}
     */
    public void startRetracting()
    {
        PID.setSpeed(-SPEED);
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
        return PID.free();
    }

    /**
     * Cancel the {@link MotorStinger#PID stingerPID}
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
        return StingerMath.leadToStinger(getLeadScrewPosition());
    }

    /**
     * Get the position of the lead screw, where (x == 0) is close and (x > 0) is far
     */
    public double getLeadScrewPosition()
    {
        return StingerMath.motorToLead(encoder.getPosition());
    }

    /**
     * Get the speed at which the lead screw is moving
     */
    public double getLeadScrewSpeed()
    {
        return StingerMath.motorNormToLead(motor.get());
    }

    /**
     * Get the speed at which the stinger is moving
     */
    public double getStingerSpeed()
    {
        return StingerMath.leadVelToStingerVel(getLeadScrewPosition(), getLeadScrewSpeed());
    }
    
    /**
     * Set the motor based on the inputed Stinger extension velocity
     */
    public void setMotor(double stingerVel)
    {
        double speed;

        if(getLeadScrewPosition() >= StingerMath.SEGMENT_LENGTH)
        {
            speed = Math.signum(stingerVel) * -MAX_STINGER_VEL * 0.5;
        }
        else 
        {
            speed = StingerMath.stingerVelToLeadVel(getLeadScrewPosition(), stingerVel);
            speed = StingerMath.leadToMotorNorm(speed);

            // make sure the thing isn't doing what we don't want it to do
            // set the motor speed to a really tiny number when the desired speed is a really small value
            if(Math.abs(speed) < 0.01 && Math.abs(stingerVel) > 0.01)
            {
                speed = Math.signum(stingerVel) * -0.01;
            }
        }
        
        // Convert from lead screw speed to motor speed
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
        else setMotor(Math555.clamp(PID.getSpeed(), -MAX_STINGER_VEL, MAX_STINGER_VEL));
    
        // Logging.info("--------------------------------------------------");
        // Logging.info("speed = " + PID.getSpeed());
        // Logging.info("pos   = " + getExtension());
        // Logging.info("rapos = " + encoder.getPosition());

        if(RobotBase.isSimulation())
        {
            SimulationUtility.simulateNEO(motor, encoder);
        }

        // UPDATE SIMULATION IMAGES //
        double currentPosition = getLeadScrewPosition();

        widthLigament.setLength(currentPosition);
        double mechAngle = Math.toDegrees(Math.asin(currentPosition / StingerMath.SEGMENT_LENGTH));

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

    @Override
    public Command in() 
    {
        return PID.goToSetpoint(MIN_LENGTH, this);
    }

    @Override
    public Command outMid() 
    {
        return PID.goToSetpoint(MID_LENGTH, this);
    }

    @Override
    public Command outHigh() 
    {
        return PID.goToSetpoint(HIGH_LENGTH, this);
    }

    @Override
    public boolean isOut() {return getExtension() > MIN_LENGTH + 0.1;}

    @Override
    public boolean requiresDriveOffset() {return false;}

    @Override
    public Command outLow() 
    {
        return Commands2023.log("AAAAAAAK Motor Stinger cannot go to Low");
    }
}
