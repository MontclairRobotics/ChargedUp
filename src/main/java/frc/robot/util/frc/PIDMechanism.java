package frc.robot.util.frc;

import java.util.Set;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.math.Math555;


public class PIDMechanism implements Sendable
{
    private PIDController pidController;
    private boolean usingPID;
    private double measurement;
    private double speed;
    private double directSpeed;
    
    private boolean clampOutput = true;

    public PIDMechanism(PIDController pidController)
    {
        this.pidController = pidController;
    }

    /**
     * Get the PIDController for this mechanism
     * 
     * @return PIDController for this mechanism
     */
    public PIDController getPIDController()
    {
        return pidController;
    }

    public void disableOutputClamping()
    {
        clampOutput = false;
    }

    /**
     * Set the target value (setPoint) that the mechanism moves to
     * 
     * @param target
     * @param shouldReset Whether or not the integrator and previous setpoint data should be erased when calling
     */
    public void setTarget(double target, boolean shouldReset)
    {
        if(shouldReset) pidController.reset();

        pidController.setSetpoint(target);
        usingPID = !pidController.atSetpoint();
    }
    public void setTarget(double target) {setTarget(target, false /*TODO: this should actually be true, but for testing this is false*/);}

    /**
     * Stop using PID to determine speed
     */
    public void cancel()
    {
        usingPID = false;
    }

    /**
     * Set the measurement of where it currently is
     * @param measurement
     */
    public void setMeasurement(double measurement)
    {
        this.measurement = measurement;
    }

    public double getMeasurement()
    {
        return measurement;
    }

    /**
     * Set the speed manually (without PID)
     * @param speed
     */
    public void setSpeed(double speed) //duck you fylan
    {
        this.directSpeed = speed;
    }
    /**
     * Updates the calculated speed
     * <p>
     * <ul>
     * <li>if we are currently using PID, calculate using PID
     * <li>otherwise, use {@link PIDMechanism#setSpeed(double) setSpeed()} to set the speed manually
     * </ul>
     */
    public void update()
    {
        // if(pidController.atSetpoint())
        // {
        //     cancel();
        // }

        if(usingPID)
        {
            double calulation = pidController.calculate(measurement);

            if(clampOutput)
            {
                speed = Math555.clamp1(calulation);
            }
            else 
            {
                speed = calulation;
            }
        }
        else 
        {
            speed = directSpeed;
        }

        if(pidController.atSetpoint() && usingPID)
        {
            cancel();
        }
    }
    /**
     * get the speed determined by the {@link PIDMechanism#update() update method}
     * @return speed
     */
    public double getSpeed() 
    {
        return speed;
    }

    /**
     * Get whether or not PID is used to calculate
     * <ul>
     *  <li> <code>true</code> if using PID
     *  <li> <code>false</code> if <b>not</b> using PID
     * </ul>
     * @return boolean
     */
    public boolean active()
    {
        return usingPID;
    }
    public boolean free()
    {
        return !usingPID;
    }

    @Override
    public void initSendable(SendableBuilder builder) 
    {
        builder.addDoubleProperty ("P", pidController::getP, pidController::setP);
        builder.addDoubleProperty ("I", pidController::getI, pidController::setI);
        builder.addDoubleProperty ("D", pidController::getD, pidController::setD);
        builder.addDoubleProperty ("Setpoint", pidController::getSetpoint, pidController::setSetpoint);
        builder.addBooleanProperty("At Setpoint", pidController::atSetpoint, x -> usingPID = !x);
        builder.addDoubleProperty ("Speed", () -> speed, x -> speed = x);

        builder.setSmartDashboardType("ShuffleboardLayout");
    }

    /**
     * Get a command which targets the given value with this PID mechanism,
     * requiring the given subsystem(s), if any.
     */
    public Command goTo(DoubleSupplier value, Subsystem... requirements)
    {
        return new Command() 
        {
            private boolean updatedAtLeastOnce = false;

            public Set<Subsystem> getRequirements() {return Set.of(requirements);}

            @Override
            public void execute() 
            {
                setTarget(value.getAsDouble(), !updatedAtLeastOnce);
                updatedAtLeastOnce = true;
            }

            @Override
            public boolean isFinished() {return updatedAtLeastOnce && free();}

            @Override
            public void end(boolean interrupted) {cancel();}
        };
    }

    /**
     * Get a command which targets the given value with this PID mechanism,
     * requiring the given subsystem(s), if any.
     */
    public Command goTo(double value, Subsystem... requirements) {return goTo(() -> value, requirements);}
}

/* 

TODO: 1/18
class MultiPIDMechanism
{
    PIDMechanism(Map<String, PIDController>) - constructor
    PIDController controller(String name)    - get the controller with the specified name

    void add(String name, PIDController) - add a new controller to this mechanism

    void target(String name, double) - target measurement for controller
    void cancel()                    - cancel all pidding

    void setMeasurement(String name, double) - set measurement for the named pid controller
    void setSpeed(double)                    - set speed directly. ignore if pidding

    void update() - update speed

    double get()     - get resultant speed
    boolean active() - get if any pidding is occurring

    String current() - get the name of the current controller, or 'null' if no pidding is ocurring
}
*/
