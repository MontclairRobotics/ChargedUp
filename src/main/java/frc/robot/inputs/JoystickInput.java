package frc.robot.inputs;

import org.team555.frc.controllers.GameController;
import org.team555.frc.controllers.GameController.Axis;

public class JoystickInput 
{
    private final boolean invertX;
    private final boolean invertY;

    private double x;
    private double y;

    private double rawX;
    private double rawY;

    private double r;

    private double theta;
    private double rawTheta;

    // Value Updaters //
    private void updateXY()
    {
        x = invertX ? -rawX : rawX;
        y = invertY ? -rawY : rawY;
    }
    private void updateTheta()
    {
        theta = invertX ? 180 - rawTheta : rawTheta;
        theta = invertY ? -theta         : theta;
    }

    private void updateCartesianFromPolar()
    {
        rawX = r*Math.cos(rawTheta);
        rawY = r*Math.sin(rawTheta);
        
        updateXY();
    }
    private void updatePolarFromCartesian()
    {
        r = Math.sqrt(rawX*rawX + rawY*rawY);
        rawTheta = Math.atan2(rawY, rawX);

        updateTheta();
    }

    private JoystickInput(double x, double y, boolean invertX, boolean invertY)
    {
        this.invertX = invertX;
        this.invertY = invertY;

        setXY(x, y);
    }

    // Getters //
    public double getX() {return x;}
    public double getY() {return y;}
    
    public double getMagnitude() {return r;}
    public double getTheta() {return theta;}

    public double getRawX() {return x;}
    public double getRawY() {return y;}
    public double getRawTheta() {return rawTheta;}

    // Setters //
    public void setXY(double x, double y)
    {
        rawX = x;
        rawY = y;

        updateXY();
        updatePolarFromCartesian();
    }
    public void setPolar(double r, double theta)
    {
        this.r = r;
        this.theta = theta;

        updateTheta();
        updateCartesianFromPolar();
    }

    public void setX(double x)
    {
        rawX = x;

        updateXY();
        updatePolarFromCartesian();
    }
    public void setY(double y)
    {
        rawY = y;
        
        updateXY();
        updatePolarFromCartesian();
    }

    public void setMagnitude(double magnitude)
    {
        r = magnitude;
        updateCartesianFromPolar();
    }

    public void setTheta(double theta)
    {
        rawTheta = theta;

        updateTheta();
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
