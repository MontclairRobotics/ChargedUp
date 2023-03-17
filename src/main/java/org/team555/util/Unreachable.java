package org.team555.util;

public class Unreachable extends Error 
{
    public static <T> T here()
    {
        throw new Unreachable();
    }
}