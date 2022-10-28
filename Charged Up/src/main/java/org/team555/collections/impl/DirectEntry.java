package org.team555.collections.impl;

import org.team555.collections.CountBag;
import org.team555.collections.CountBag.Entry;

/**
 * Represents an entry which stores its own values as fields.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class DirectEntry<T> implements CountBag.Entry<T>
{
    private T element;
    private int count;

    public T element() { return element; }
    public int count() { return count; }

    void setElem(T elem) {this.element = elem;}
    void setCount(int count) {this.count = count;}

    public DirectEntry(T element, int count)
    {
        this.element = element;
        this.count = count;
    }

    public int hashCode()
    {
        return element.hashCode() + count;
    }

    public boolean equals(Object other)
    {
        if(this == other) return true;
        if(other == null) return false;
        if(!(other instanceof DirectEntry<?>)) return false;
        var otherEntry = (DirectEntry<?>)other;
        return element.equals(otherEntry.element) && count == otherEntry.count;
    }
}
