package frc.robot.inputs;

import org.team555.frc.controllers.GameController;
import org.team555.frc.controllers.GameController.Axis;

public class JoystickInput 
{
    private double x;
    private double y;
    private double r;
    private double theta;

    // Value Updaters //
    private void updateFromCartesian()
    {
        x = r*Math.cos(theta);
        y = r*Math.sin(theta);
    }
    private void updateFromPolar()
    {
        r = Math.sqrt(x*x + y*y);
        theta = Math.atan2(y, x);
    }

    private JoystickInput(double x, double y)
    {
        setXY(x, y);
    }

    // Getters //
    public double getX() {return x;}
    public double getY() {return y;}
    
    public double getMagnitude() {return r;}
    public double getTheta() {return theta;}

    // Setters //
    public void setXY(double x, double y)
    {
        this.x = x;
        this.y = y;

        updateFromCartesian();
    }
    public void setPolar(double r, double theta)
    {
        this.r = r;
        this.theta = theta;

        updateFromPolar();
    }

    public void setX(double x)
    {
        this.x = x;
        updateFromCartesian();
    }
    public void setY(double y)
    {
        this.y = y;
        updateFromCartesian();
    }

    public void setMagnitude(double magnitude)
    {
        r = magnitude;
        updateFromPolar();
    }
    public void setTheta(double theta)
    {
        this.theta = theta;
        updateFromPolar();
    }

    public void rotate(double angleRadians)
    {
        theta += angleRadians;
        updateFromPolar();
    }

    // Static Constructors //
    public static JoystickInput getInput(GameController controller, Axis x, Axis y)
    {
        return new JoystickInput(
            controller.getAxisValue(x),
            controller.getAxisValue(y)
        );
    }

    public static JoystickInput getLeft(GameController controller)
    {
        return getInput(controller, Axis.LEFT_X, Axis.LEFT_Y);
    }
    public static JoystickInput getRight(GameController controller)
    {
        return getInput(controller, Axis.RIGHT_X, Axis.RIGHT_Y);
    }
}
