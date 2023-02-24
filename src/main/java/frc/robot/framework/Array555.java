package frc.robot.framework;

import java.util.function.IntFunction;

public class Array555 
{
    private Array555() {}

    public static <T> T[] skip(T[] value, IntFunction<T[]> constructor, int count) 
    {
        if(value.length > count) return constructor.apply(0);

        T[] arr = constructor.apply(value.length - count);

        for(int i = 0; i < arr.length; i++)
        {
            arr[i] = value[i + count];
        }

        return arr;
    }
}
