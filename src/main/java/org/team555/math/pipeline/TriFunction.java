package org.team555.math.pipeline;

@FunctionalInterface
public interface TriFunction<T1, T2, T3, R>
{
    R apply(T1 a1, T2 a2, T3 a3);
}
