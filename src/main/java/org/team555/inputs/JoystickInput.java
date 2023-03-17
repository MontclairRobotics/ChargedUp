package org.team555.inputs;

import org.team555.util.frc.GameController;
import org.team555.util.frc.GameController.Axis;

public class JoystickInput 
{
    private double x;
    private double y;

    // private double rawX;
    // private double rawY;

    private double r;

    private double theta;
    // private double rawTheta;

    // Value Updaters //

    // /**
    //  * Inverts the x if {@link #invertX} is true and
    //  * inverts the y if {@link #invertY} is true
    //  */
    // private void updateXY()
    // {
    //     x = invertX ? -rawX : rawX;
    //     y = invertY ? -rawY : rawY;
    // }
    // /**
    //  * Inverts theta based on booleans {@link #invertX} and {@link #invertY}
    //  */
    // private void updateTheta()
    // {
    //     final double PI  = Math.PI;
    //     final double TAU = Math.PI * 2;

    //     theta = rawTheta % TAU;

    //     theta = invertX ? PI  - (theta % TAU) : theta;
    //     theta = invertY ? TAU - (theta % TAU) : theta;

    //     theta %= TAU;
    // }

    /**
     * Updates the raw cartesian values of the joystick 
     * to represent the raw polar values of the joystick
     */
    private void updateCartesianFromPolar()
    {
        x = r*Math.cos(theta);
        y = r*Math.sin(theta);
        
        // updateXY();
    }

    /**
     * Updates the raw polar values of the joystick 
     * to represent the raw cartesian values of the joystick
     */
    private void updatePolarFromCartesian()
    {
        r = Math.sqrt(x*x + y*y);
        theta = Math.atan2(y, x);

        // updateTheta();
    }

    private JoystickInput(double x, double y, boolean invertX, boolean invertY)
    {
        setXY(
            invertX ? -x : x, 
            invertY ? -y : y
        );
    }

    // Getters //
    public double getX() {return x;}
    public double getY() {return y;}
    
    public double getMagnitude() {return r;}
    public double getTheta() {return theta;}

    // public double getRawX() {return rawX;}
    // public double getRawY() {return rawY;}
    // public double getRawTheta() {return rawTheta;}

    // Setters //

    /**
     * Sets the raw x and y values of the joystick 
     * and updates the x, y, and polar values based on the new values
     * @param x the x value
     * @param y the y value
     */
    public void setXY(double x, double y)
    {
        this.x = x;
        this.y = y;

        updatePolarFromCartesian();
    }

    /**
     * Sets the raw radius and theta values of the joystick 
     * and updates the radius, theta, and cartesian values based on the new values
     * @param r the radius
     * @param theta the angle (radians)
     */
    public void setPolar(double r, double theta)
    {
        this.r = r;
        this.theta = theta;

        updateCartesianFromPolar();
    }

    /**
     * Sets the raw x value of the joystick 
     * and updates the x and polar values based on the new x
     * @param x the x value
     */
    public void setX(double x)
    {
        this.x = x;

        // updateXY();
        updatePolarFromCartesian();
    }

    /**
     * Sets the raw y value of the joystick 
     * and updates the y and polar values based on the new y
     * @param x the y value
     */
    public void setY(double y)
    {
        this.y = y;
        
        // updateXY();
        updatePolarFromCartesian();
    }

    /**
     * Sets the raw radius of the joystick 
     * and updates the radius and cartesian values based on the new radius
     * @param magnatude the magnatude of the radius
     */
    public void setMagnitude(double magnitude)
    {
        r = magnitude;
        updateCartesianFromPolar();
    }

    /**
     * Sets the raw theta of the joystick 
     * and updates the theta and cartesian values based on the new theta
     * @param theta the angle (radians)
     */
    public void setTheta(double theta)
    {
        this.theta = theta;

        // updateTheta();
        updateCartesianFromPolar();
    }

    // Static Constructors //
    public static JoystickInput getInput(GameController controller, Axis x, Axis y, boolean invertX, boolean invertY)
    {
        return new JoystickInput(
            controller.getAxisValue(x),
            controller.getAxisValue(y),
            invertX, 
            invertY
        );
    }

    public static JoystickInput getLeft(GameController controller, boolean invertX, boolean invertY)
    {
        return getInput(controller, Axis.LEFT_X, Axis.LEFT_Y, invertX, invertY);
    }
    public static JoystickInput getRight(GameController controller, boolean invertX, boolean invertY)
    {
        return getInput(controller, Axis.RIGHT_X, Axis.RIGHT_Y, invertX, invertY);
    }
}
