package frc.robot.structure.factories;

import java.util.HashMap;
import java.util.Map;

public class HashMaps 
{
    public static <E,V> HashMap<E,V> of()
    {
        return new HashMap<E, V>();
    }
    public static <E,V> HashMap<E,V> of(E e1, V v1)
    {
        var m = new HashMap<E, V>();

        m.put(e1, v1);

        return m;
    }
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);

        return m;
    }
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);

        return m;
    }
    
    public static <E,V> HashMap<E,V> of(E e1, V v1, E e2, V v2, E e3, V v3, E e4, V v4)
    {
        var m = new HashMap<E, V>();
        
        m.put(e1, v1);
        m.put(e2, v2);
        m.put(e3, v3);
        m.put(e4, v4);

        return m;
    }
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
