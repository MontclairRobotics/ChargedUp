package org.team555.util;

public final class ArrConstruct 
{
    private ArrConstruct() {}

    @SafeVarargs
    public static <T> T[] arrg(T... arr)
    {
        return arr;
    }
    public static double[] arr(double... arr)
    {
        return arr;
    }
    public static int[] arr(int... arr)
    {
        return arr;
    }
    public static boolean[] arr(boolean... arr)
    {
        return arr;
    }
}
