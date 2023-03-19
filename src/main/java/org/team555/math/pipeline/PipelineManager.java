package org.team555.math.pipeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class PipelineManager 
{
    public void add(Pipeline<?> pipe)
    {
        pipelines.add(pipe);
    }

    public <T> Pipeline<T> constant(T value)
    {
        Pipeline<T> p = Pipeline.constant(value);
        add(p);
        return p;
    }
    public <T> Pipeline<T> function(Supplier<T> value)
    {
        Pipeline<T> p = Pipeline.function(value);
        add(p);
        return p;
    }

    public DoublePipeline constant(double value)
    {
        DoublePipeline p = DoublePipeline.constant(value);
        add(p);
        return p;
    }
    public DoublePipeline function(DoubleSupplier value)
    {
        DoublePipeline p = DoublePipeline.function(value);
        add(p);
        return p;
    }

    public BooleanPipeline constant(boolean value)
    {
        BooleanPipeline p = BooleanPipeline.constant(value);
        add(p);
        return p;
    }
    public BooleanPipeline function(BooleanSupplier value)
    {
        BooleanPipeline p = BooleanPipeline.function(value);
        add(p);
        return p;
    }

    private List<Pipeline<?>> pipelines = new ArrayList<>();

    public void update()
    {
        HashSet<Pipeline<?>> alreadyVisited = new HashSet<>();
        for(Pipeline<?> pipe : pipelines)
        {
            pipe.update(alreadyVisited);
        }
    }
    public void reset()
    {
        HashSet<Pipeline<?>> alreadyVisited = new HashSet<>();
        for(Pipeline<?> pipe : pipelines)
        {
            pipe.reset(alreadyVisited);
        }
    }
}
