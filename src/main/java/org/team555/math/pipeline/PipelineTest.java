package org.team555.math.pipeline;

import org.team555.util.frc.commandrobot.ManagerBase;

import edu.wpi.first.math.controller.PIDController;

public class PipelineTest extends ManagerBase
{
    public double someData() {return 0;}
    public boolean dataWasSquared() {return false;}

    public PipelineManager pipelines = new PipelineManager();

    public DoublePipeline input = pipelines.function(this::someData); // Get data

    public DoublePipeline output = 
        pipelines.function(this::dataWasSquared)          // Get if data was squared
            .debounce(0.1)                   // Require it to have been squared for 0.1 seconds
            .choiceDouble(input, input.pow(0.5))    // Take the square root if it was 
            .rate()                                       // Get the rate of change of the modified data
            .rateLimit(0.1)                          // Prevent this rate from changing more than 0.1 units per second
            .pid(new PIDController(0, 0, 0));    // Use this value as the setpoint for some PID loop

    public void always()
    {
        pipelines.update();

        double out = output.get();
    }

    public void reset()
    {
        pipelines.reset();
    }
}
