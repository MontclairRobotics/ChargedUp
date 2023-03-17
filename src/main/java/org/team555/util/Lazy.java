package org.team555.util;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> 
{
    private boolean hasValue = false;
    private final Supplier<T> f;
    private T value;

    public Lazy(Supplier<T> f)
    {
        this.f = f;
    }

    @Override
    public T get() 
    {
        if (hasValue) return value;

        hasValue = true;
        value = f.get();
        return value;
    }
}
