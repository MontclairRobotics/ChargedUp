package org.team555.util;

public class Unimplemented extends Error 
{
    /**
     * Throw an error. Use for unimplemented methods
     * @param <T>
     * @return
     */
    public static <T> T here()
    {
        throw new Unimplemented();
    }
}