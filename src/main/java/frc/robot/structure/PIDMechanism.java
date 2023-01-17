package frc.robot.structure;

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
