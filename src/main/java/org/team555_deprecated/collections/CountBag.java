package org.team555_deprecated.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

import org.team555_deprecated.collections.impl.DirectEntry;
import org.team555_deprecated.collections.impl.HashCountBag;

/**
 * A collection which stores counts of duplicate elements.
 * Does not implement any standard collection interfaces, as the general 
 * structure of a {@link CountBag} is not analogous to most other collections.
 * 
 * <p>
 * Generally, implementations will use {@link Object#hashCode()} in order to 
 * decipher duplicates, and will use {@link Object#equals(Object)} as a fallback
 * in the event that a hash collision is found. If this is not the case, the
 * implementing class will specify otherwise, typically with the language
 * {@code This class does not use hashCode with equals as a fallback in order to
 * determine duplicate elements}.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public interface CountBag<T> extends Iterable<CountBag.Entry<T>>
{
    /**
     * Represents a single entry in a {@link CountBag}.
     * Stores both an element and a count.
     * 
     * @author Team 555 (Dylan Rafael)
     * @version 1.0
     * @since 1.0
     */
    interface Entry<T>
    {
        T element();
        int count();
    }

    /**
     * Add the given element the given number of times to this bag.
     * Does nothing if {@code count} is less than or equal to zero.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * <p>
     * It is implied that performing the operation
     * <pre>
     * bag.add(elem, n);
     *bag.sub(elem, n);
     * </pre>
     * ... is equivalent to doing nothing.
     * 
     * @param elem the element
     * @param count the number of times to add the element to this bag
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    boolean add(T elem, int count);
    /**
     * Remove the given element the given number of times to this bag.
     * Does nothing if {@code count} is less than or equal to zero.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * <p>
     * It is implied that performing the operation
     * <pre>
     * bag.add(elem, n);
     *bag.sub(elem, n);
     * </pre>
     * ... is equivalent to doing nothing.
     * 
     * @param elem the element
     * @param count the number of times to add the element to this bag
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    boolean sub(T elem, int count);

    /**
     * Add the given element to the bag.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * <p>
     * This is always strictly equivalent to
     * <pre>
     * bag.add(elem, 1);
     * </pre>
     * 
     * @param elem the element
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean add(T elem)
    {
        return add(elem, 1);
    }
    /**
     * Remove the given element to the bag.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * <p>
     * This is always strictly equivalent to
     * <pre>
     * bag.sub(elem, 1);
     * </pre>
     * 
     * @param elem the element
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean sub(T elem)
    {
        return sub(elem, 1);
    }

    /**
     * Adds every given element to this bag the specified amount of times.
     * Does nothing if {@code count} is less than or equal to zero.
     * 
     * <p>
     * Note that duplicate elements in the provided elements collection
     * will not be treated specially; they will be added another {@code count}
     * times per duplicate present.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * @param elems the elements
     * @param count the number of times to add the elements to this bag
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean addAll(Collection<? extends T> elems, int count)
    {
        boolean changed = false;
        for(T elem : elems)
        {
            changed |= add(elem, count);
        }
        return changed;
    }
    /**
     * Removes every given element to this bag the specified amount of times.
     * Does nothing if {@code count} is less than or equal to zero.
     * 
     * <p>
     * Note that duplicate elements in the provided elements collection
     * will not be treated specially; they will be added another {@code count}
     * times per duplicate present.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * @param elems the elements
     * @param count the number of times to add the elements to this bag
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean subAll(Collection<? extends T> elems, int count)
    {
        boolean changed = false;
        for(T elem : elems)
        {
            changed |= sub(elem, count);
        }
        return changed;
    }

    /**
     * Adds every given element to this bag.
     * 
     * <p>
     * Note that duplicate elements in the provided elements collection
     * will not be treated specially; they will be added another {@code count}
     * times per duplicate present.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * <p>
     * This is always strictly equivalent to 
     * <pre>
     * bag.addAll(elems, 1);
     * </pre>
     * 
     * @param elems the elements
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean addAll(Collection<? extends T> elems)
    {
        return addAll(elems, 1);
    }
    /**
     * Removes every given element to this bag.
     * 
     * <p>
     * Note that duplicate elements in the provided elements collection
     * will not be treated specially; they will be added another {@code count}
     * times per duplicate present.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * <p>
     * This is always strictly equivalent to 
     * <pre>
     * bag.addAll(elems, 1);
     * </pre>
     * 
     * @param elems the elements
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean subAll(Collection<? extends T> elems)
    {
        return subAll(elems, 1);
    }

    /**
     * Adds every element in the given bag to this instance the number of times it appears in the given bag.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * @param elems the other bag
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean add(CountBag<T> elems)
    {
        boolean changed = false;
        for(var elem : elems)
        {
            changed |= add(elem.element(), elem.count());
        }
        return changed;
    }
    /**
     * Adds every element in the given bag to this instance the number of times it appears in the given bag.
     * 
     * <p>
     * Returns whether or not the bag was changed as a result of this operation.
     * 
     * @param elems the other bag
     * @return {@code true} if this bag was changed as a result of this call, {@code false} otherwise
     */
    default boolean sub(CountBag<T> elems)
    {
        boolean changed = false;
        for(var elem : elems)
        {
            changed |= sub(elem.element(), elem.count());
        }
        return changed;
    }

    /**
     * Get the number of duplicates of the given element.
     * Returns {@code 0} if this bag does not contain the element.
     * 
     * <p>
     * It is implied that {@code b.contains(e) == (b.countOf(e) != 0)}
     * holds true for all implementations of this interface.
     * 
     * @param elem the element
     * @return the number of duplicates of the given element.
     */
    int countOf(T elem);

    /**
     * Remove all duplicates of the given element from this bag.
     * 
     * @param elem the element to remove
     */
    void remove(T elem);

    /**
     * Remove all duplicates of the given elements from this bag.
     * 
     * @param elem the elements to remove.
     */
    default void removeAll(Collection<? extends T> elem)
    {
        for(T e : elem)
        {
            remove(e);
        }
    }

    /**
     * Returns the number of elements in this bag.
     * 
     * @return the number of elements in this bag as an {@code int}
     */
    int size();

    /**
     * Return a collection which represents all of the elements of this bag, 
     * with only one item present for all sets of duplicates.
     * 
     * <p>
     * <em>NOTE:</em> Modifying this collection results in undefined behaviour.
     * Do <b>not</b> modify the result of this function call.
     * 
     * @return a collection which represents all non-duplicate elements of this bag
     */
    Collection<T> elements();
    Iterator<Entry<T>> iterator();

    /**
     * Return a collection which represents all of the elements of this bag,
     * including duplicates.
     * 
     * <p>
     * <em>NOTE:</em> Modifying this collection results in undefined behaviour.
     * Do <b>not</b> modify the result of this function call.
     * 
     * @return a collection which represents all elements of this bag
     */
    default Collection<T> fullElements()
    {
        var al = new ArrayList<T>();
        for(var e : this)
        {
            for(int i = 0; i < e.count(); i++)
            {
                al.add(e.element());
            }
        }
        return al;
    }

    /**
     * Determines whether or not this bag contains the given element.
     * 
     * @param elem the element
     * @return {@code true} if this bag contains the element, {@code false} otherwise
     */
    default boolean contains(T elem)
    {
        return countOf(elem) > 0;
    }
    /**
     * Determines whether ot not this bag contains all of the given elements.
     * 
     * @param elems the elements
     * @return {@code true} if this bag contains all given elements, {@code false} otherwise
     */
    default boolean containsAll(Collection<? extends T> elems)
    {
        for(T elem : elems)
        {
            if(!contains(elem)) return false;
        }
        return true;
    }

    /**
     * Determines whether or not this bag currently has no elements.
     * @return {@code true} is this bag has no elements, {@code false} otherwise.
     */
    default boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Clears this bag from all elements.
     */
    void clear();

    /**
     * Returns the total count of all elements stored in this bag, including duplicates.
     * @return the total count of all elements stored in this bag, including duplicates.
     */
    default int totalCount()
    {
        int count = 0;
        for(Entry<T> entry : this)
        {
            count += entry.count();
        }
        return count;
    }

    /**
     * Create a new count bag which represents the merging of the two given bags.
     * 
     * <p>
     * The merging of two bags is the bag which contains every element in both bags
     * as many times as the sum of their appearances in both bags.
     * 
     * <p>
     * In other terms, the merging of two bags is equivalent to
     * <pre>
     * CountBag{@literal <T>} merged = CountBag.defaultConstructor();
     *merged.addAll(a);
     *merged.addAll(b);
     * </pre>
     * 
     * @param <T> the element type of the bags
     * @param first the first bag
     * @param second the second bag
     * @param bagSupplier the function, usually a constructor, which will return the new bag to populate
     * @return the new bag
     */
    public static <T> CountBag<T> merge(CountBag<T> first, CountBag<T> second, Supplier<CountBag<T>> bagSupplier)
    {
        var result = bagSupplier.get();
        result.add(first);
        result.add(second);
        return result;
    }

    
    public static <T> CountBag<T> of()
    {
        return new HashCountBag<T>();
    }
    public static <T> CountBag<T> of(T t1, int count1)
    {
        var result = new HashCountBag<T>();
        result.add(t1, count1);
        return result;
    }
    public static <T> CountBag<T> of(T t1, int count1, T t2, int count2)
    {
        var result = new HashCountBag<T>();
        result.add(t1, count1);
        result.add(t2, count2);
        return result;
    }
    public static <T> CountBag<T> of(T t1, int count1, T t2, int count2, T t3, int count3)
    {
        var result = new HashCountBag<T>();
        result.add(t1, count1);
        result.add(t2, count2);
        result.add(t3, count3);
        return result;
    }
    public static <T> CountBag<T> of(T t1, int count1, T t2, int count2, T t3, int count3, T t4, int count4)
    {
        var result = new HashCountBag<T>();
        result.add(t1, count1);
        result.add(t2, count2);
        result.add(t3, count3);
        result.add(t4, count4);
        return result;
    }
    public static <T> CountBag<T> of(T t1, int count1, T t2, int count2, T t3, int count3, T t4, int count4, T t5, int count5)
    {
        var result = new HashCountBag<T>();
        result.add(t1, count1);
        result.add(t2, count2);
        result.add(t3, count3);
        result.add(t4, count4);
        result.add(t5, count5);
        return result;
    }
    public static <T> CountBag<T> of(T t1, int count1, T t2, int count2, T t3, int count3, T t4, int count4, T t5, int count5, T t6, int count6)
    {
        var result = new HashCountBag<T>();
        result.add(t1, count1);
        result.add(t2, count2);
        result.add(t3, count3);
        result.add(t4, count4);
        result.add(t5, count5);
        result.add(t6, count6);
        return result;
    }

    @SafeVarargs
    public static <T> CountBag<T> of(Entry<T>... entries)
    {
        var result = new HashCountBag<T>();

        for(var entry : entries)
        {
            result.add(entry.element(), entry.count());
        }

        return result;
    }

    public static <T> Entry<T> entry(T elem, int count)
    {
        return new DirectEntry<T>(elem, count);
    }

    /**
     * Create a new {@link CountBag} which is guaranteed to be empty and modifiable.
     * 
     * @param <T> the element type of the resulting bag
     * @return a new {@link CountBag}
     */
    public static <T> CountBag<T> defaultConstructor()
    {
        return new HashCountBag<T>();
    }
}
