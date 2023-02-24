package frc.robot.util.frc;

import edu.wpi.first.math.controller.PIDController;


public class PIDMechanism
{
    private PIDController pidController;
    private boolean usingPID;
    private double measurement;
    private double speed;

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

    /**
     * Set the target value (setPoint) that the mechanism moves to
     * 
     * @param target
     */
    public void setTarget(double target)
    {
        pidController.setSetpoint(target);
        usingPID = true;
    }

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
    /**
     * Set the speed manually (without PID)
     * @param speed
     */
    public void setSpeed(double speed) //duck you fylan
    {
        this.speed = speed;
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
        //value = boolean ? if true set to this : else set to this
        speed = usingPID ? pidController.calculate(measurement) : speed;

        if(pidController.atSetpoint())
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
