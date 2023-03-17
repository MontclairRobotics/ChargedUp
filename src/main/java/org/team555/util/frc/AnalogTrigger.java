package org.team555.util.frc;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import org.team555.util.function.DoubleDualPredicate;
import org.team555.util.function.DoubleTriPredicate;

public class AnalogTrigger
{
    private final DoubleSupplier axis;

    public DoubleSupplier getAxis()
    {
        return axis;
    }

    //////////////////////////////////
    // CONSTRUCTION
    //////////////////////////////////
    public AnalogTrigger(DoubleSupplier axis)
    {
        this.axis = axis;
    }

    public static AnalogTrigger from(DoubleSupplier axis)
    {
        return new AnalogTrigger(axis);
    }

    //////////////////////////////////
    // OPERATORS
    //////////////////////////////////
    public AnalogTrigger plus(AnalogTrigger other)
    {
        return against(other, (a, b) -> a + b);
    }
    public AnalogTrigger plus(DoubleSupplier other)
    {
        return against(other, (a, b) -> a + b);
    }
    public AnalogTrigger plus(double other)
    {
        return against(other, (a, b) -> a + b);
    }

    public AnalogTrigger minus(AnalogTrigger other)
    {
        return against(other, (a, b) -> a - b);
    }
    public AnalogTrigger minus(DoubleSupplier other)
    {
        return against(other, (a, b) -> a - b);
    }
    public AnalogTrigger minus(double other)
    {
        return against(other, (a, b) -> a - b);
    }

    public AnalogTrigger times(AnalogTrigger other)
    {
        return against(other, (a, b) -> a * b);
    }
    public AnalogTrigger times(DoubleSupplier other)
    {
        return against(other, (a, b) -> a * b);
    }
    public AnalogTrigger times(double other)
    {
        return against(other, (a, b) -> a * b);
    }

    public AnalogTrigger over(AnalogTrigger other)
    {
        return against(other, (a, b) -> a / b);
    }
    public AnalogTrigger over(DoubleSupplier other)
    {
        return against(other, (a, b) -> a / b);
    }
    public AnalogTrigger over(double other)
    {
        return against(other, (a, b) -> a / b);
    }

    public AnalogTrigger raisedTo(AnalogTrigger other)
    {
        return against(other, Math::pow);
    }
    public AnalogTrigger raisedTo(DoubleSupplier other)
    {
        return against(other, Math::pow);
    }
    public AnalogTrigger raisedTo(double other)
    {
        return against(other, Math::pow);
    }

    public AnalogTrigger modulo(AnalogTrigger other)
    {
        return against(other, (a, b) -> a % b);
    }
    public AnalogTrigger modulo(DoubleSupplier other)
    {
        return against(other, (a, b) -> a % b);
    }
    public AnalogTrigger modulo(double other)
    {
        return against(other, (a, b) -> a % b);
    }

    public AnalogTrigger min(AnalogTrigger other)
    {
        return against(other, Math::min);
    }
    public AnalogTrigger min(DoubleSupplier other)
    {
        return against(other, Math::min);
    }
    public AnalogTrigger min(double other)
    {
        return against(other, Math::min);
    }

    public AnalogTrigger max(AnalogTrigger other)
    {
        return against(other, Math::max);
    }
    public AnalogTrigger max(DoubleSupplier other)
    {
        return against(other, Math::max);
    }
    public AnalogTrigger max(double other)
    {
        return against(other, Math::max);
    }

    public AnalogTrigger against(AnalogTrigger other, DoubleBinaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble(), other.axis.getAsDouble()));
    }
    public AnalogTrigger against(DoubleSupplier other, DoubleBinaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble(), other.getAsDouble()));
    }
    public AnalogTrigger against(double other, DoubleBinaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble(), other));
    }

    public AnalogTrigger squared()
    {
        return applied(x -> x * x);
    }
    public AnalogTrigger sqrt()
    {
        return applied(Math::sqrt);
    }
    public AnalogTrigger inversed()
    {
        return applied(a -> 1 / a);
    }
    public AnalogTrigger negated()
    {
        return applied(a -> -a);
    }
    public AnalogTrigger abs()
    {
        return applied(Math::abs);
    }

    public AnalogTrigger applied(DoubleUnaryOperator operator)
    {
        return new AnalogTrigger(() -> operator.applyAsDouble(this.axis.getAsDouble()));
    }
    //////////////////////////////////
    // TRIGGER GETTERS
    //////////////////////////////////
    public Trigger when(DoublePredicate predicate)
    {
        return new Trigger(() -> predicate.test(axis.getAsDouble()));
    }

    public Trigger when(DoubleDualPredicate predicate, double other)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), other));
    }
    public Trigger when(DoubleDualPredicate predicate, DoubleSupplier other)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), other.getAsDouble()));
    }
    public Trigger when(DoubleDualPredicate predicate, AnalogTrigger other)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), other.axis.getAsDouble()));
    }

    public Trigger when(DoubleTriPredicate predicate, double otherA, double otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA, otherB));
    }
    public Trigger when(DoubleTriPredicate predicate, double otherA, DoubleSupplier otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA, otherB.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, double otherA, AnalogTrigger otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA, otherB.axis.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, DoubleSupplier otherA, double otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.getAsDouble(), otherB));
    }
    public Trigger when(DoubleTriPredicate predicate, DoubleSupplier otherA, DoubleSupplier otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.getAsDouble(), otherB.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, DoubleSupplier otherA, AnalogTrigger otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.getAsDouble(), otherB.axis.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogTrigger otherA, double otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogTrigger otherA, DoubleSupplier otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB.getAsDouble()));
    }
    public Trigger when(DoubleTriPredicate predicate, AnalogTrigger otherA, AnalogTrigger otherB)
    {
        return new Trigger(() -> predicate.evaluate(axis.getAsDouble(), otherA.axis.getAsDouble(), otherB.axis.getAsDouble()));
    }

    public Trigger whenGreaterThan(double value)
    {
        return when(x -> x > value);
    }
    public Trigger whenGreaterThanOrEqualTo(double value)
    {
        return when(x -> x >= value);
    }
    public Trigger whenLessThan(double value)
    {
        return when(x -> x < value);
    }
    public Trigger whenLessThanOrEqualTo(double value)
    {
        return when(x -> x <= value);
    }
    public Trigger whenEqualTo(double value)
    {
        return when(x -> x == value);
    }
    public Trigger whenUnequalTo(double value)
    {
        return when(x -> x != value);
    }
}