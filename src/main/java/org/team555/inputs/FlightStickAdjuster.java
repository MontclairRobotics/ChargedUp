package org.team555.inputs;

import edu.wpi.first.wpilibj.Joystick;

public class FlightStickAdjuster {
    

    private double deadband;
    private double power;
    private boolean invertX;
    private boolean invertY;

    public FlightStickAdjuster(double deadband, double power, boolean invertX, boolean invertY) {
        this.deadband = Math.abs(deadband);
        this.power = power;
        this.invertX = invertX;
        this.invertY = invertY;
    }

    public FlightStickAdjuster(double deadband, boolean invertX, boolean invertY) {
        this(deadband, 1.0, invertX, invertY);
    }

    public double adjustXInput(double input) {

        if (Math.abs(input) <= deadband) {
            return 0;
        }

        input /*[0, 1]*/ = (Math.abs(input) - deadband) / (1 - deadband);

        double invertedInput = invertX ? -input : input; //TODO make an enum to select between x and y inputs

        return Math.abs(Math.pow(invertedInput, power)) * Math.signum(invertedInput);
    }

    public double adjustYInput(double input) {

        if (Math.abs(input) <= deadband) {
            return 0;
        }

        input /*[0, 1]*/ = (Math.abs(input) - deadband) / (1 - deadband);

        double invertedInput = invertY ? -input : input; //TODO make an enum to select between x and y inputs

        return Math.abs(Math.pow(invertedInput, power)) * Math.signum(invertedInput);
    }
}
