package org.team555.util;

/**
 * Signify that the code which throws this error 
 * should not be reached.
 */
public class Unreachable extends Error 
{
    /**
     * Use {@code return Unreachable.here();} or just {@code Unreachable.here();}
     * in order to throw an instance of {@link Unreachable}
     */
    public static <T> T here()
    {
        throw new Unreachable();
    }
}