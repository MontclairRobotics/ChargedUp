package org.team555.collections.impl;

import java.util.Map;

import org.team555.collections.CountBag;

/**
 * Represents an entry which stores its values as a {@link Map.Entry}.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class MapEntryWrapper<T> implements CountBag.Entry<T>
{
    private final Map.Entry<T, Integer> entry;

    public T element() {return entry.getKey();}
    public int count() {return entry.getValue();}

    public MapEntryWrapper(Map.Entry<T, Integer> entry)
    {
        this.entry = entry;
    }
}
