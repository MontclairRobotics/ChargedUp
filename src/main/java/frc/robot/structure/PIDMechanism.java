package frc.robot.structure;

import edu.wpi.first.math.controller.PIDController;

/*
TODO: 1/17
class PIDMechanism 
{
    PIDMechanism(PIDController) - constructor
    PIDController controller()  - get the controller

    void target(double) - target measurement
    void cancel()       - cancel pidding

    void setMeasurement(double) - set measurement for pid
    void setSpeed(double)       - set speed directly. ignore if pidding

    void update() - update speed

    double get()     - get resultant speed
    boolean active() - is pidding
    
    TODO: 1/18
    void setTimeout(double) - set the maximum time which pidding will occur
}

*/

public class PIDMechanism
{
    private PIDController pidController;
    private boolean usingPID;
    private double target;
    private double measurement;
    private double speed;

    public PIDMechanism(PIDController pidController)
    {
        this.pidController = pidController;
    }

    public PIDController getPIDController()
    {
        return pidController;
    }

    public void setTarget(double target)
    {
        this.target = target;
        usingPID = true;
    }

    public void cancel()
    {
        usingPID = false;
    }

    public void setMeasurement(double measurement)
    {
        this.measurement = measurement;
    }

    public void setSpeed(double speed) { //duck you fylan
        this.speed = speed;
    }

    public void update()
    {
        speed = usingPID ? pidController.calculate(measurement, target) : speed;
        //value = boolean ? if true set to this : else set to this
    }

    public double getSpeed() 
    {
        return speed;
    }

    public boolean active()
    {
        return usingPID;
    }
}

/* 










TODO: 1/18
class MultiPIDMechanism
{w
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
