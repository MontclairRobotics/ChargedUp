package org.team555.util;

public final class SysUtils 
{
    private SysUtils() {}

    public static double timeSeconds()
    {
        return System.currentTimeMillis() / 1000.0;
    }
}