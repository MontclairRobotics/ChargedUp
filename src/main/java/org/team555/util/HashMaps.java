package org.team555.util;

import java.util.HashMap;
import java.util.Map;

public class HashMaps 
{
    /**
     * Creates an empty HashMap
     * 
     * @return Empty HashMap
     */
    public static <E,V> HashMap<E,V> of()
    {
        return new HashMap<E, V>();
    }
    /**
     * Create a new HashMap with the following key and value pair
     * @param e1 key
     * @param v1 value
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1)
    {
        var m = new HashMap<E, V>();

        m.put(e1, v1);

        return m;
    }
    /**
     * Create a new HashMap with the following key and value pairs
     * @param e1 key 1
     * @param v1 value 1
     * @param e2 key 2
     * @param v2 value 2
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);

        return m;
    }
    /**
     * Create a new HashMap with the following key and value pairs
     * @param e1 key 1
     * @param v1 value 1
     * @param e2 key 2
     * @param v2 value 2
     * @param e3 key 3
     * @param v3 value 3
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);

        return m;
    }
    
    /**
     * Create a new HashMap with the following key and value pairs
     * @param e1 key
     * @param v1 value
     * @param e2 key
     * @param v2 value
     * @param e3 key
     * @param v3 value
     * @param e4 key
     * @param v4 value
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3, E e4, V v4)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);
        m.put(e4, v4);

        return m;
    }
    /**
     * Create a new HashMap with the following key and value pairs
     * @param e1 key
     * @param v1 value
     * @param e2 key
     * @param v2 value
     * @param e3 key
     * @param v3 value
     * @param e4 key
     * @param v4 value
     * @param e5 key
     * @param v5 value
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3, E e4, V v4, E e5, V v5)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);
        m.put(e4, v4);
        m.put(e5, v5);

        return m;
    }
    /**
     * Create a new HashMap with the following key and value pairs
     * @param e1 key
     * @param v1 value
     * @param e2 key
     * @param v2 value
     * @param e3 key
     * @param v3 value
     * @param e4 kwy
     * @param v4 value
     * @param e5 key
     * @param v5 value
     * @param e6 key
     * @param v6 value
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3, E e4, V v4, E e5, V v5, E e6, V v6)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);
        m.put(e4, v4);
        m.put(e5, v5);
        m.put(e6, v6);

        return m;
    }
    /**
     * Create a new HashMap with the following key and value pairs
     * @param e1 key
     * @param v1 value
     * @param e2 key
     * @param v2 value
     * @param e3 key
     * @param v3 value
     * @param e4 key
     * @param v4 value
     * @param e5 key
     * @param v5 value
     * @param e6 key
     * @param v6 value
     * @param e7 key
     * @param v7 value
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3, E e4, V v4, E e5, V v5, E e6, V v6, E e7, V v7)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);
        m.put(e4, v4);
        m.put(e5, v5);
        m.put(e6, v6);
        m.put(e7, v7);

        return m;
    }
    /**
     * Create a new HashMap with the following key and value pairs
     * @param e1 key
     * @param v1 value
     * @param e2 key
     * @param v2 value
     * @param e3 key
     * @param v3 value
     * @param e4 key
     * @param v4 value
     * @param e5 key
     * @param v5 value
     * @param e6 key
     * @param v6 value
     * @param e7 key
     * @param v7 value
     * @param e8 key
     * @param v8 value
     * @return HashMap containing all the keys and their respective values
     */
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3, E e4, V v4, E e5, V v5, E e6, V v6, E e7, V v7, E e8, V v8)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);
        m.put(e4, v4);
        m.put(e5, v5);
        m.put(e6, v6);
        m.put(e7, v7);
        m.put(e8, v8);

        return m;
    }
    
    @SafeVarargs
    /**
     * Construct a HashMap with the inputted entries
     * @param entries Array of Entries
     * @return HashMap containing all the entries
     */
    public static <E,V> HashMap<E,V> ofEntries(Map.Entry<E,V>... entries)
    {
        var m = new HashMap<E, V>();
        
        for(var e : entries)
        {
            m.put(e.getKey(), e.getValue());
        }

        return m;
    }
    
}


