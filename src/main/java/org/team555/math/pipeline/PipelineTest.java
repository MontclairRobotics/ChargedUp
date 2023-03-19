package org.team555.math.pipeline;

import java.util.Map;

import org.team555.util.frc.commandrobot.ManagerBase;

import edu.wpi.first.math.controller.PIDController;

public class PipelineTest extends ManagerBase
{
    public double someData() {return 0;}
    public boolean dataWasSquared() {return false;}

    public PipelineManager pipelines = new PipelineManager();

    public DoublePipeline input = pipelines.function(this::someData); // Get data

    public DoublePipeline output = 
            input.ifElse(
                pipelines.function(this::dataWasSquared).debounce(0.1), 
                input.pow(0.5)
            )
            .rate()                                       
            .rateLimit(0.1)                          
            .pid(new PIDController(0, 0, 0))
            .choiceDouble(
                Map.of(
                    0.0, pipelines.constant(1),
                    1.0, pipelines.constant(2),
                    2.0, pipelines.constant(4), 
                    3.0, pipelines.constant(5)
                ), 
                input.times(10)
            )
            .abs()
            .greaterThan(10)
            .risingEdge()
            .conditionDouble(1, 0);

    public void always()
    {
        pipelines.update();

        double out = output.getAsDouble();
    }

    public void reset()
    {
        pipelines.reset();
    }
}
