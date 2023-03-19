package org.team555.math.pipeline;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.team555.util.frc.EdgeDetectFilter;
import org.team555.util.frc.EdgeDetectFilter.EdgeType;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;

public abstract class BooleanPipeline extends Pipeline<Boolean>
{
    public final boolean getAsBoolean() {return get();}

    @Override
    public BooleanPipeline withDependencies(Pipeline<?>... deps) 
    {
        super.withDependencies(deps);
        return this;
    }

    public static BooleanPipeline constant(boolean value) {return of(constant((Boolean)value));}
    public static BooleanPipeline function(BooleanSupplier value) {return of(function((Supplier<Boolean>)(() -> value.getAsBoolean())));}
    public static BooleanPipeline of(Pipeline<Boolean> pipe)
    {
        return new BooleanPipeline() 
        {
            @Override
            public Boolean get() {return pipe.get();}
            @Override
            protected void updateSelf() {pipe.updateSelf();}
        }
        .withDependencies(pipe);
    }

    public BooleanPipeline negate() {return applyBool(x -> !x);}

    public BooleanPipeline and(Pipeline<Boolean> other) {return combineBool(other, (x, y) -> x && y);}
    public BooleanPipeline or (Pipeline<Boolean> other) {return combineBool(other, (x, y) -> x || y);}
    public BooleanPipeline xor(Pipeline<Boolean> other) {return combineBool(other, (x, y) -> x ^  y);}

    public BooleanPipeline debounce(Debouncer debouncer)
    {
        return applyBool(debouncer::calculate, () -> debouncer.calculate(false));
    }
    public BooleanPipeline debounce(double debounceTime, DebounceType debounceType)
    {
        return debounce(new Debouncer(debounceTime, debounceType));
    }
    public BooleanPipeline debounce(double debounceTime)
    {
        return debounce(new Debouncer(debounceTime));
    }

    public BooleanPipeline edgeDetectFilter(EdgeDetectFilter filter) 
    {
        return applyBool(filter::calculate, filter::reset);
    }
    public BooleanPipeline risingEdge()
    {
        return edgeDetectFilter(new EdgeDetectFilter(EdgeType.RISING));
    }
    public BooleanPipeline fallingEdge()
    {
        return edgeDetectFilter(new EdgeDetectFilter(EdgeType.FALLING));
    }
    public BooleanPipeline edge()
    {
        return edgeDetectFilter(new EdgeDetectFilter(EdgeType.EITHER));
    }

    @Override
    public BooleanPipeline ifElse(Pipeline<Boolean> cond, Pipeline<Boolean> other) 
    {
        return of(super.ifElse(cond, other));
    }

    public <T> Pipeline<T> condition(Pipeline<T> onTrue, Pipeline<T> onFalse)
    {
        return combine(onTrue, onFalse, (c, t, f) -> c ? t : f);
    }
    public DoublePipeline conditionDouble(Pipeline<Double> onTrue, Pipeline<Double> onFalse)
    {
        return combineDouble(onTrue, onFalse, (c, t, f) -> c ? t : f);
    }
    public BooleanPipeline conditionBool(Pipeline<Boolean> onTrue, Pipeline<Boolean> onFalse)
    {
        return combineBool(onTrue, onFalse, (c, t, f) -> c ? t : f);
    }

    public <T> Pipeline<T> condition(T onTrue, T onFalse)
    {return condition(constant(onTrue), constant(onFalse));}
    public DoublePipeline conditionDouble(double onTrue, double onFalse)
    {return conditionDouble(constant(onTrue), constant(onFalse));}
    public BooleanPipeline conditionBools(boolean onTrue, boolean onFalse)
    {return conditionBool(constant(onTrue), constant(onFalse));}
}
