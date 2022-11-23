package frc.robot;

public class Input 
{
    /**
     * Returns an adjusted input for the given input.
     * @param x The input value [-1, 1]
     * @param deadband The deadband value [0, 1)
     * @param power The power to apply to the input [-inf, inf]
     * @return The result [-1, 1]
     */
    public static double adjust(double x /*[-1, 1]*/, double deadband, double power)
    {
        // If the value is within the deadband, return 0
        if(Math.abs(x) < deadband) return 0;

        // Otherwise remap the rest of the values from [0, 1]
        var input_remap /*[0, 1]*/ = (Math.abs(x) - deadband) / (1 - deadband);

        // Raise input to a given power, to allow for more room in the lower band range
        var input_raised /*[0, 1]*/ = Math.pow(Math.abs(input_remap), power);
        
        // Re-introduce signed-ness
        return input_raised * Math.signum(x);
    }
}
