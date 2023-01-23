package frc.robot.inputs;

public class JoystickAdjuster 
{
    public final double deadband;
    public final double power;

    public JoystickAdjuster(double deadband, double power)
    {
        this.deadband = deadband;
        this.power = power;
    }

    /**
     * Returns an adjusted input for the given input.
     * @param x The input value [-1, 1]
     * @return The result [-1, 1]
     */
    public double adjust(double x /*[-1, 1]*/)
    {
        // If the value is within the deadband, return 0
        if(Math.abs(x) < deadband) return 0;

        // Otherwise remap the rest of the values from [0, 1]
        double input_remap /*[0, 1]*/ = (Math.abs(x) - deadband) / (1 - deadband);

        // Raise input to a given power, to allow for more room in the lower band range
        double input_raised /*[0, 1]*/ = Math.pow(Math.abs(input_remap), power);
        
        // Re-introduce signed-ness
        return input_raised * Math.signum(x);
    }

    public void adjustMagnitude(JoystickInput joystick)
    {
        double m = adjust(joystick.getMagnitude());
        joystick.setMagnitude(m);
    }

    public void adjustX(JoystickInput joystick)
    {
        double m = adjust(joystick.getX());
        joystick.setX(m);
    }
    public void adjustY(JoystickInput joystick)
    {
        double m = adjust(joystick.getY());
        joystick.setY(m);
    }

    public void adjustPos(JoystickInput joystick)
    {
        adjustX(joystick);
        adjustY(joystick);
    }
}
