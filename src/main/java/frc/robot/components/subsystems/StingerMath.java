package frc.robot.components.subsystems;

import edu.wpi.first.math.system.plant.DCMotor;

public class StingerMath 
{
    private StingerMath() {}

    private static final double W = 0.232; // Length of one segment
    private static final double S = 8.5;   // Number of segments
    
    public static final double SEGMENT_LENGTH = W;
    public static final double SEGMENT_COUNT  = S;

    private static final double Vf // Free speed of motor
        = DCMotor.getNEO(1).freeSpeedRadPerSec / (2 * Math.PI);
    private static final double Ct // Conversion factor from lead dist to motor rot
        // = 15748.031496063;
        = 98947.8001131;

    /**
     * Convert from Lead distance traveled (meters) to Stinger distance (meters)
     */
    public static double leadToStinger(double x)
    {
        return S * Math.sqrt(W*W - x*x);
    }

    /**
     * Convert from Stinger distance traveled (meters) to Lead distance traveled (meters)
     */
    public static double stingerToLead(double y)
    {
        return Math.sqrt(W*W - (y*y)/(S*S));
    }
    
    /**
     * Conversion Function
     * <p>
     * Multiply Conversion Factor by Lead Screw Velocity to get Stinger Velocity
     * <p>
     * Alternatively, 
     * Divide Stinger Velocity by Conversion Factor to get Lead Screw Velocity
     * 
     * @param x lead screw distance
     */
    private static double C(double x)
    {
        return -S*x / Math.sqrt(W*W - x*x);
    }
    
    /**
     * Convert Lead Screw Velocity (m/s) to Stinger Velocity (m/s)
     * 
     * @param x Lead Screw Distance Traveled
     * @param dxdt Lead Screw Velocity
     */
    public static double leadVelToStingerVel(double x, double dxdt)
    {
        return dxdt*C(x);
    }
    /**
     * Convert Stinger Velocity (m/s) to Lead Screw Velocity (m/s)
     * 
     * @param x Lead Screw Distance Traveled
     * @param dydt Stinger Velocity
     */
    public static double stingerVelToLeadVel(double x, double dydt)
    {
        return dydt/C(x);
    }
    
    /**
     * Get Motor rotations from Lead Screw Distance (meters)
     * 
     * @param x Lead Screw Distance Traveled
     */
    public static double leadToMotor(double x)
    {
        return x*Ct;
    }

    /**
     * Get Lead Screw Distance (meters) from Motor Rotations
     * 
     * @param theta (rotations)
     */
    public static double motorToLead(double theta)
    {
        return theta/Ct;
    }
    
    /**
     * Get Normalized Motor Velocity [-1, 1] from Lead Screw Distance traveled (meters)
     * 
     * @param x Lead Screw Distance Traveled
     */
    public static double leadToMotorNorm(double x)
    {
        return leadToMotor(x) / Vf;
    }
    /**
     * Get Lead Screw Distance (meters) from Normalized Motor Velocity [-1, 1]
     * 
     * @param norm Normalized Motor Velocity [-1, 1]
     */
    public static double motorNormToLead(double norm)
    {
        return motorToLead(norm * Vf);
    }
}
