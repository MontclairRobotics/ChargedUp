package org.team555_deprecated.util;

public final class SysUtils 
{
    private SysUtils() {}

    public static double timeSeconds()
    {
        return System.currentTimeMillis() / 1000.0;
    }
}