package org.team555_deprecated.collections.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.team555_deprecated.collections.CountBag;

/**
 * A collection which counts the number of each element added to it.
 * This collection only supports getting count, adding, and removing elements.
 * You cannot iterate over this collection directly, but can instead iterate over a set of its entries
 * and of its keys.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class HashCountBag<T> implements CountBag<T>
{
    private final ArrayList<Integer> counts;
    private final HashMap<T, Integer> indexMap;

    public HashCountBag()
    {
        counts = new ArrayList<>();
        indexMap = new HashMap<>();
    }

    public boolean add(T item, int count)
    {
        if(count <= 0) return false;
        if(indexMap.containsKey(item))
        {
            int index = indexMap.get(item);
            counts.set(index, counts.get(index) + count);
            return true;
        }
        else
        {
            counts.add(count);
            indexMap.put(item, counts.size() - 1);
            return true;
        }
    }
    public boolean add(T item)
    {
        return add(item, 1);
    }

    public boolean sub(T item, int count)
    {
        if(count <= 0 || !indexMap.containsKey(item)) return false;
        
        int index = indexMap.get(item);
        int currentCount = counts.get(index);
        if(currentCount - count <= 0)
        {
            counts.remove(index);
            indexMap.remove(item);
        }
        else
        {
            counts.set(index, currentCount - count);
        }
        return true;
    }
    public boolean sub(T item)
    {
        return sub(item, 1);
    }

    public int countOf(T item)
    {
        if(indexMap.containsKey(item))
        {
            return counts.get(indexMap.get(item));
        }
        return 0;
    }

    public Collection<T> elements() { return indexMap.keySet(); }
    public Iterator<Entry<T>> iterator() 
    {
        return indexMap.entrySet().stream().map(e -> (Entry<T>)new MapEntryWrapper<>(e)).iterator();
    }

    public int size()
    {
        return counts.size();
    }

    public void remove(T item)
    {
        if(indexMap.containsKey(item))
        {
            int index = indexMap.get(item);
            counts.remove(index);
            indexMap.remove(item);
        }
    }

    @Override
    public void clear() 
    {
        counts.clear();
        indexMap.clear();
    }
}
