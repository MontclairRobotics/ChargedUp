package org.team555.inputs;

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

    /**
     * Adjusts the magnatude of the joystick using {@link #adjust(double) adjust}
     */
    public void adjustMagnitude(JoystickInput joystick)
    {
        double m = adjust(joystick.getMagnitude());
        joystick.setMagnitude(m);
    }

    /**
     * Adjusts the x of the joystick using {@link #adjust(double) adjust}
     */
    public void adjustX(JoystickInput joystick)
    {
        double m = adjust(joystick.getX());
        joystick.setX(m);
    }

    /**
     * Adjusts the y of the joystick using {@link #adjust(double) adjust}
     */
    public void adjustY(JoystickInput joystick)
    {
        double m = adjust(joystick.getY());
        joystick.setY(m);
    }

    /**
     * Adjusts the x and y of the joystick 
     * using {@link #adjustX(double) adjustX}
     *   and {@link #adjustY(double) adjustY}
     */
    public void adjustPos(JoystickInput joystick)
    {
        adjustX(joystick);
        adjustY(joystick);
    }
}
