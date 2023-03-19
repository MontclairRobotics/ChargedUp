package org.team555.math.pipeline;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.team555.math.Math555;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.filter.SlewRateLimiter;

public abstract class DoublePipeline extends Pipeline<Double>
{
    public final double getAsDouble() {return get();}

    @Override
    public DoublePipeline withDependencies(Pipeline<?>... dep) 
    {
        super.withDependencies(dep);
        return this;
    }

    public static DoublePipeline constant(double value) {return of(constant((Double)value));}
    public static DoublePipeline function(DoubleSupplier value) {return of(function((Supplier<Double>)(() -> value.getAsDouble())));}
    public static DoublePipeline of(Pipeline<Double> pipe)
    {
        return new DoublePipeline() 
        {
            @Override
            public Double get() {return pipe.get();}
            @Override
            protected void updateSelf() {pipe.updateSelf();}
        }
        .withDependencies(pipe);
    }

    public DoublePipeline negate() {return this.applyDouble(x -> -x);}
    public DoublePipeline abs   () {return this.applyDouble(Math::abs);}

    public DoublePipeline plus (Pipeline<Double> other) {return this.combineDouble(other, (x, y) -> x + y);}
    public DoublePipeline minus(Pipeline<Double> other) {return this.combineDouble(other, (x, y) -> x - y);}
    public DoublePipeline times(Pipeline<Double> other) {return this.combineDouble(other, (x, y) -> x * y);}
    public DoublePipeline over (Pipeline<Double> other) {return this.combineDouble(other, (x, y) -> x / y);}
    public DoublePipeline pow  (Pipeline<Double> other) {return this.combineDouble(other, Math::pow);}
    public DoublePipeline min  (Pipeline<Double> other) {return this.combineDouble(other, Math::min);}
    public DoublePipeline max  (Pipeline<Double> other) {return this.combineDouble(other, Math::max);}
    
    public DoublePipeline plus (double other) {return plus(constant(other));}
    public DoublePipeline minus(double other) {return minus(constant(other));}
    public DoublePipeline times(double other) {return times(constant(other));}
    public DoublePipeline over (double other) {return over(constant(other));}
    public DoublePipeline pow  (double other) {return pow(constant(other));}
    public DoublePipeline min  (double other) {return min(constant(other));}
    public DoublePipeline max  (double other) {return max(constant(other));}
    
    public DoublePipeline clamp(DoubleSupplier min, DoubleSupplier max) 
    {
        return this.applyDouble(x -> Math555.clamp(x, min.getAsDouble(), max.getAsDouble()));
    }
    public DoublePipeline clamp(double min, double max) {return clamp(() -> min, () -> max);}

    public BooleanPipeline positive() {return applyBool(x -> x > 0);}
    public BooleanPipeline negative() {return applyBool(x -> x < 0);}
    public BooleanPipeline nonzero()  {return applyBool(x -> x != 0);}
    public BooleanPipeline zero()     {return applyBool(x -> x == 0);}

    public BooleanPipeline greaterThan(Pipeline<Double> other) {return combineBool(other, (x, y) -> x > y);}
    public BooleanPipeline greaterThanOrEqual(Pipeline<Double> other) {return combineBool(other, (x, y) -> x >= y);}
    public BooleanPipeline greaterThan(double other) {return greaterThan(constant(other));}
    public BooleanPipeline greaterThanOrEqual(double other) {return greaterThanOrEqual(constant(other));}
    
    public BooleanPipeline lessThan(Pipeline<Double> other) {return combineBool(other, (x, y) -> x < y);}
    public BooleanPipeline lessThanOrEqual(Pipeline<Double> other) {return combineBool(other, (x, y) -> x <= y);}
    public BooleanPipeline lessThan(double other) {return lessThan(constant(other));}
    public BooleanPipeline lessThanOrEqual(double other) {return lessThanOrEqual(constant(other));}

    public BooleanPipeline between(Pipeline<Double> min, Pipeline<Double> max) 
    {return combineBool(min, max, (x, min1, max1) -> x > min1 && x < max1);}
    public BooleanPipeline betweenOrEqual(Pipeline<Double> min, Pipeline<Double> max) 
    {return combineBool(min, max, (x, min1, max1) -> x >= min1 && x <= max1);}

    public DoublePipeline filter(LinearFilter filter)
    {
        return applyDouble(filter::calculate, filter::reset);
    }

    public DoublePipeline rollingAverage(int taps)
    {
        LinearFilter filter = LinearFilter.movingAverage(taps);
        return applyDouble(filter::calculate, filter::reset);
    }
    public DoublePipeline rate(int samples)
    {
        LinearFilter filter = LinearFilter.backwardFiniteDifference(1, samples, 0.02);
        return applyDouble(filter::calculate, filter::reset);
    }
    public DoublePipeline rate() {return rate(5);}

    public DoublePipeline rateLimit(SlewRateLimiter limiter, double resetValue)
    {
        return applyDouble(limiter::calculate, () -> limiter.reset(resetValue));
    }
    public DoublePipeline rateLimit(double positiveRate, double negativeRate, double initialValue)
    {
        return rateLimit(new SlewRateLimiter(positiveRate, negativeRate, initialValue), initialValue);
    }
    public DoublePipeline rateLimit(double rate, double initalValue)
    {
        return rateLimit(new SlewRateLimiter(rate, rate, initalValue), initalValue);
    }
    public DoublePipeline rateLimit(double rate)
    {
        return rateLimit(new SlewRateLimiter(rate), 0);
    }

    public DoublePipeline pid(PIDController controller)
    {
        return applyDouble(controller::calculate, controller::reset);
    }
    public DoublePipeline pid(ProfiledPIDController controller, DoubleSupplier resetPosition)
    {
        return applyDouble(controller::calculate, () -> controller.reset(resetPosition.getAsDouble()));
    }

    @Override
    public DoublePipeline ifElse(Pipeline<Boolean> cond, Pipeline<Double> other) 
    {
        return of(super.ifElse(cond, other));
    }
}
