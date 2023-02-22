package frc.robot.subsystems.managers;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Constants.Robot;
import frc.robot.framework.commandrobot.ManagerBase;

public class ColorSensor extends ManagerBase
{
    ColorSensorV3 colorSensor = new ColorSensorV3(Robot.Grabber.COLOR_SENSOR_PORT);
    ColorMatch colorMatch = new ColorMatch();

    boolean previousCubeSeen;
    boolean previousConeSeen;

    boolean currentCubeSeen;
    boolean currentConeSeen;

    public ColorSensor() 
    {
        colorMatch.addColorMatch(Robot.ColorSensing.CONE_COLOR);
        colorMatch.addColorMatch(Robot.ColorSensing.CUBE_COLOR);

        colorMatch.setConfidenceThreshold(Robot.ColorSensing.COLOR_CONFIDENCE);
    }

    public Color color() {return colorSensor.getColor();}

    public ColorMatchResult colorResult()
    {
        return colorMatch.matchClosestColor(color());
    }

    public Color closestColor()
    {
        return colorResult().color;
    }
    public double closestConfidence()
    {
        return colorResult().confidence;
    }

    /**
     * @return does it see a cube?
     */
    public boolean seesCube()
    {
        return closestColor().equals(Robot.ColorSensing.CUBE_COLOR);
    }

    /**
     * @return does it see a cone?
     */
    public boolean seesCone()
    {
        return closestColor().equals(Robot.ColorSensing.CONE_COLOR);
    }

    public boolean coneEnteredView() {return currentConeSeen && !previousConeSeen;}
    public boolean cubeEnteredView() {return currentCubeSeen && !previousCubeSeen;}
    public boolean objectEnteredView() {return coneEnteredView() || cubeEnteredView();}
    
    public boolean coneExitedView() {return !currentConeSeen && previousConeSeen;}
    public boolean cubeExitedView() {return !currentCubeSeen && previousCubeSeen;}
    public boolean objectExitedView() {return coneExitedView() || cubeExitedView();}

    @Override
    public void always() 
    {
        previousCubeSeen = currentCubeSeen;
        currentCubeSeen = seesCube();

        previousConeSeen = currentConeSeen;
        currentConeSeen = seesCone();
    }
}