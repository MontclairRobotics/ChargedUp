package org.team555.math.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Pipeline<T> 
{
    public abstract T get();

    public final void update()
    {
        visit(Pipeline::updateSelf);
    }
    public final void update(HashSet<Pipeline<?>> alreadyVisited)
    {
        visit(alreadyVisited, Pipeline::updateSelf);
    }

    public final void reset()
    {
        visit(Pipeline::resetSelf);
    }
    public final void reset(HashSet<Pipeline<?>> alreadyVisited)
    {
        visit(alreadyVisited, Pipeline::resetSelf);
    }

    private void visit(Consumer<Pipeline<?>> r)
    {
        visit(new HashSet<>(), r);
    }
    private void visit(HashSet<Pipeline<?>> alreadyVisited, Consumer<Pipeline<?>> r)
    {
        if(alreadyVisited.contains(this)) return;
        alreadyVisited.add(this);

        for(Pipeline<?> p : dependents)
        {
            p.visit(alreadyVisited, r);
        }

        r.accept(this);
    }

    protected void updateSelf() {}
    protected void resetSelf() {}

    private final List<Pipeline<?>> dependents = new ArrayList<>();

    public void addDependent(Pipeline<?> dep)
    {
        this.dependents.add(dep);
    }

    public Pipeline<T> withDependencies(Pipeline<?>... deps)
    {
        for(Pipeline<?> dep : deps)
        {
            dep.addDependent(this);
        }
        return this;
    }

    public DoublePipeline applyDouble(Function<T, Double> fn, Runnable reset)
    {return DoublePipeline.of(apply(fn, reset));}
    public BooleanPipeline applyBool(Function<T, Boolean> fn, Runnable reset)
    {return BooleanPipeline.of(apply(fn, reset));}
    
    public DoublePipeline applyDouble(Function<T, Double> fn)
    {return DoublePipeline.of(apply(fn));}
    public BooleanPipeline applyBool(Function<T, Boolean> fn)
    {return BooleanPipeline.of(apply(fn));}
    
    public <O> DoublePipeline combineDouble(Pipeline<O> other, BiFunction<T, O, Double> fn)
    {return DoublePipeline.of(combine(other, fn));}
    public <O1, O2> DoublePipeline combineDouble(Pipeline<O1> other1, Pipeline<O2> other2, TriFunction<T, O1, O2, Double> fn)
    {return DoublePipeline.of(combine(other1, other2, fn));}
    public <O> BooleanPipeline combineBool(Pipeline<O> other, BiFunction<T, O, Boolean> fn)
    {return BooleanPipeline.of(combine(other, fn));}
    public <O1, O2> BooleanPipeline combineBool(Pipeline<O1> other1, Pipeline<O2> other2, TriFunction<T, O1, O2, Boolean> fn)
    {return BooleanPipeline.of(combine(other1, other2, fn));}

    public <K> Pipeline<K> apply(Function<T, K> fn, Runnable reset)
    {
        Pipeline<T> original = this;
        return new Pipeline<K>() 
        {
            K lastValue;

            @Override
            public K get() {return lastValue;}

            @Override
            protected void updateSelf() 
            {
                lastValue = fn.apply(original.get());
            }

            @Override
            protected void resetSelf() 
            {
                reset.run();
            }
        }
        .withDependencies(this);
    }
    public <K> Pipeline<K> apply(Function<T, K> fn) {return apply(fn, () -> {});}

    public <O, K> Pipeline<K> combine(Pipeline<O> other, BiFunction<T, O, K> combinator)
    {
        Pipeline<T> original = this;
        return new Pipeline<K>() 
        {
            K lastValue;

            @Override
            public K get() {return lastValue;}

            @Override
            protected void updateSelf() 
            {
                lastValue = combinator.apply(original.get(), other.get());
            }
        }
        .withDependencies(original, other);
    }
    public <O1, O2, K> Pipeline<K> combine(Pipeline<O1> other1, Pipeline<O2> other2, TriFunction<T, O1, O2, K> combinator)
    {
        Pipeline<T> original = this;
        return new Pipeline<K>() 
        {
            K lastValue;

            @Override
            public K get() {return lastValue;}

            @Override
            protected void updateSelf() 
            {
                lastValue = combinator.apply(original.get(), other1.get(), other2.get());
            }
        }
        .withDependencies(original, other1, other2);
    }

    public BooleanPipeline equal(Pipeline<T> other) {return combineBool(other, (x, y) -> x.equals(y));}
    public BooleanPipeline equal(T other) {return equal(constant(other));}
    
    public BooleanPipeline notEqual(Pipeline<T> other) {return equal(other).negate();}
    public BooleanPipeline notEqual(T other) {return equal(other).negate();}

    public Pipeline<T> ifElse(Pipeline<Boolean> cond, Pipeline<T> other)
    {
        return combine(cond, other, (t, c, f) -> c ? t : f);
    }

    public <K> Pipeline<K> choice(Map<T, ? extends Pipeline<K>> pipes, Pipeline<K> defaultCase)
    {
        return combine(defaultCase, (v, defaultV) -> 
        {
            if(!pipes.containsKey(v)) return defaultV;

            return pipes.get(v).get();
        })
        .withDependencies(pipes.values().toArray(Pipeline<?>[]::new));
    }
    public <K> Pipeline<K> choice(Map<T, Pipeline<K>> pipes, K defaultValue)
    {return choice(pipes, constant(defaultValue));}

    public DoublePipeline choiceDouble(Map<T, ? extends Pipeline<Double>> pipes, Pipeline<Double> defaultCase)
    {return DoublePipeline.of(choice(pipes, defaultCase));}
    public DoublePipeline choiceDouble(Map<T, ? extends Pipeline<Double>> pipes, double defaultValue)
    {return DoublePipeline.of(choice(pipes, constant(defaultValue)));}
    
    public BooleanPipeline choiceBool(Map<T, ? extends Pipeline<Boolean>> pipes, Pipeline<Boolean> defaultCase)
    {return BooleanPipeline.of(choice(pipes, defaultCase));}
    public BooleanPipeline choiceBool(Map<T, ? extends Pipeline<Boolean>> pipes, boolean defaultValue)
    {return BooleanPipeline.of(choice(pipes, constant(defaultValue)));}

    public static <T> Pipeline<T> constant(T value)
    {
        return new Pipeline<T>() 
        {
            @Override
            public T get() {return value;}
        };
    }
    public static <T> Pipeline<T> function(Supplier<T> function)
    {
        return new Pipeline<T>() 
        {
            @Override
            public T get() {return function.get();}
        };
    }
}
