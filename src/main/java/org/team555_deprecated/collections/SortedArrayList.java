package org.team555_deprecated.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.ArrayList;

/**
 * An array list which automatically sorts added elements according to 
 * a specified comparator.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class SortedArrayList<T> implements Collection<T>
{
    /**
     * Create a new sorted array list from the given comparator and elements.
     * 
     * @param <T> the element type of the resulting list
     * @param comparator the comparator to pass to the comstructor
     * @param elements the elements to be contained in the returned list
     * @return a new instance of {@link SortedArrayList} which contains the provided elements.
     * 
     * @implNote the provided elements need not be sorted, as they are resorted upon the creation 
     * of the new list instance.
     */
    @SafeVarargs
    public static <T> SortedArrayList<T> of(Comparator<T> comparator, T... elements)
    {
        var ret = new SortedArrayList<>(comparator);
        
        for(var e : elements)
        {
            ret.add(e);
        }

        return ret;
    }

    /**
     * Create a new sorted array list from a list directly.
     * 
     * <p>
     * <em>NOTE:</em> It is important to know that instances created with this method, unlike other instances,
     * are <b>not</b> guaranteed to be sorted. This may cause major issues if not accounted for.
     * 
     * @param <T> the element type of the resulting list and the provided list
     * @param list the list which will be wrapped
     * @param comparator the comparator to pass to the constructor
     * @return a new instance of {@link SortedArrayList} which wraps the given list.
     */
    public static <T> SortedArrayList<T> fromSorted(ArrayList<T> list, Comparator<T> comparator)
    {
        var ret = new SortedArrayList<>(comparator);
        ret.inner = list;
        return ret;
    }

    /**
     * Create a new sorted array list, which will use the given comparator to sort its elements.
     * @param comparator the comparator to use when sorting.
     */
    public SortedArrayList(Comparator<T> comparator)
    {
        this.comparator = comparator;

        inner = new ArrayList<>();
    }

    private ArrayList<T> inner;
    private Comparator<T> comparator;

    public T get(int index) 
    {
        return inner.get(index);
    }

    public boolean contains(Object e)
    {
        return inner.contains(e);
    }

    public int size() 
    {
        return inner.size();
    }

    /**
     * In addition to all of the guarantees and functionality provided by 
     * {@link Collection#add(Object)}, this function gaurantees that the list remains sorted.
     * 
     * If an item compares identically to another item in the list,
     * starting from index zero and going upto the last possible index, it is added before it.
     * 
     * {@inheritDoc}
     * 
     * @param e {@inheritDoc}
     */
    public boolean add(T e) 
    {
        /***************************
         * logic test case:
         * [5, 6, 7].add(8)
         * 
         * 8 > 5 -> advance
         * 8 > 6 -> advance
         * 8 > 7 -> advance
         * (end loop)
         * add to end
         ***************************
         * logic test case:
         * [5, 7, 9].add(8)
         * 
         * 8 > 5 -> advance
         * 8 > 7 -> advance
         * 8 < 9 -> add where '9' is
         **************************/

        for(var i = 0; i < inner.size(); i++)
        {
            if(comparator.compare(e, inner.get(i)) <= 0)
            {
                inner.add(i, e);
                return true;
            }
        }

        return inner.add(e);
    }

    public void remove(int index)
    {
        inner.remove(index);
    }
    public boolean remove(Object item)
    {
        return this.remove(item);
    }

    public int indexOf(T e)
    {
        return inner.indexOf(e);
    }
    public int lastIndexOf(T e)
    {
        return inner.lastIndexOf(e);
    }

    public Iterator<T> iterator() {
        return inner.iterator();
    }

    public Object[] toArray() 
    {
        return inner.toArray();
    }

    public <T> T[] toArray(T[] a) 
    {
        return inner.toArray(a);
    }
    public <T> T[] toArray(IntFunction<T[]> arrCon) 
    {
        return inner.toArray(arrCon);
    }

    public boolean addAll(Collection<? extends T> c) 
    {
        return c.stream().allMatch(e -> add(e));
    }

    public void clear() 
    {
        inner.clear();
    }

    public boolean containsAll(Collection<?> c) 
    {
        return inner.containsAll(c);
    }

    public boolean isEmpty() 
    {
        return inner.isEmpty();
    }

    public boolean removeAll(Collection<?> c) 
    {
        return inner.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) 
    {
        return inner.retainAll(c);
    }
}
