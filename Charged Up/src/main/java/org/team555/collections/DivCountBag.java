package org.team555.collections;

import java.util.function.Function;

/**
 * A class which represents two sets of data, one representing 
 * the numerator of a mathematical expression and the other representing 
 * the denominator of that expression. 
 * 
 * <p>
 * Holds two {@link CountBag}s.
 * 
 * <p>
 * Use the {@link DivCountBag#simplify()} method to reduce the counts of elements common to the 
 * numerator and denominator maps respectively.
 * 
 * @author Team 555 (Dylan Rafael)
 * @version 1.0
 * @since 1.0
 */
public class DivCountBag<T>
{
    public final CountBag<T> top;
    public final CountBag<T> bottom;

    public DivCountBag(CountBag<T> top, CountBag<T> bottom)
    {
        this.top = top;
        this.bottom = bottom;
    }
    public DivCountBag()
    {
        top = CountBag.of();
        bottom = CountBag.of();
    }

    /**
     * Simplify the counts of all elements in this bag.
     * 
     * <p>
     * The process of simplification is defined as the removal
     * of all duplicates which have a corresponding value in the 
     * opposing {@link CountBag} of this instance.
     * 
     * <p>
     * For example, 
     * <pre>
     * var db = DivCountBag.of(CountBag.of("Hi!", 1), CountBag.of("Hi!", 1));
     *db.simplify();
     * </pre>
     * results in an empty instance. 
     */
    public void simplify()
    {
        for(var key : top.elements())
        {
            if(bottom.contains(key))
            {
                var topCount = top.countOf(key);
                var bottomCount = bottom.countOf(key);
                
                var min = Math.min(topCount, bottomCount);

                top.sub(key, min);
                bottom.sub(key, min);
            }
        }
    }

    /**
     * Create a new {@link DivCountBag} which holds the result of a merging
     * of this instance's {@link DivCountBag#top top} and {@link DivCountBag#bottom bottom}
     * fields with the provided instance's.
     * 
     * @param other the other provided bag
     * @return a new instance which holds the result of a dual merge
     */
    public DivCountBag<T> mul(DivCountBag<T> other)
    {
        var top    = CountBag.merge(this.top, other.top, CountBag::defaultConstructor);
        var bottom = CountBag.merge(this.bottom, other.bottom, CountBag::defaultConstructor);

        var result = new DivCountBag<T>(top, bottom);

        result.simplify();

        return result;
    }
    /**
     * Create a new {@link DivCountBag} which holds the result of a merging
     * of this instance's {@link DivCountBag#top top} and {@link DivCountBag#bottom bottom}
     * fields with the provided instance's {@link DivCountBag#bottom bottom} and {@link DivCountBag#top top}
     * respectively.
     * 
     * @param other the other provided bag
     * @return a new instance which holds the result of a dual inverse merge
     */
    public DivCountBag<T> div(DivCountBag<T> other)
    {
        var top    = CountBag.merge(this.top, other.bottom, CountBag::defaultConstructor);
        var bottom = CountBag.merge(this.bottom, other.top, CountBag::defaultConstructor);

        var result = new DivCountBag<T>(top, bottom);

        result.simplify();

        return result;
    }
    /**
     * Create a new {@link DivCountBag} which holds the result of {@code n} repeated calls to
     * {@link DivCountBag#mul(DivCountBag) this.mul(this)}.
     * 
     * @param n the amount of repeated multiplications
     * @return a new instance which holds the result of repeated multiplication
     */
    public DivCountBag<T> pow(int n)
    {
        var result = this;
        for(var i = 1; i < n; i++)
        {
            result = result.mul(this);
        }
        return result;
    }

    public boolean equals(Object other) 
    {
        if(this == other) return true;
        if(other == null) return false;
        if(!(other instanceof DivCountBag<?>)) return false;

        var otherD = (DivCountBag<?>)other;
        return top.equals(otherD.top) && bottom.equals(otherD.bottom);
    }
    public int hashCode() {return top.hashCode() ^ bottom.hashCode();}

    /**
     * Return a represntation of this instance as a series of the elements of {@link DivCountBag#top top}
     * combined with a " / " and the elements of {@link DivCountBag#bottom bottom}.
     * 
     * <p>
     * Uses the provided functions to turn the elements into {@link String}s, and uses {@code defaultString}
     * to determine how to represent {@link DivCountBag#top top} if it is empty.
     * 
     * <p>
     * If {@link DivCountBag#bottom bottom} is empty, the " / " is left out and no information about the bottom
     * is appended to the result.
     * 
     * @param topSelector the function used to convert top elements to strings
     * @param bottomSelector the function used to convert bottom elements to strings
     * @param defaultString the default string
     * @return the string representation
     */
    public String asDivString(Function<T, String> topSelector, Function<T, String> bottomSelector, String defaultString)
    {
        var ret = "";

        if(!top.isEmpty())
        {
            for(var entry : top)
            {
                ret += topSelector.apply(entry.element());
                if(entry.count() > 1)
                {
                    ret += "^" + entry.count();
                }
                ret += " ";
            }

            ret = ret.substring(0, ret.length() - 1);
        }
        else 
        {
            ret += defaultString;
        }

        if(!bottom.isEmpty())
        {
            ret += " / ";
            
            for(var entry : bottom)
            {
                ret += bottomSelector.apply(entry.element());
                if(entry.count() > 1)
                {
                    ret += "^" + entry.count();
                }
                ret += " ";
            }

            ret = ret.substring(0, ret.length() - 1);
        }

        return ret;
    }
    /**
     * Return a represntation of this instance as a series of the elements of {@link DivCountBag#top top}
     * combined with a " / " and the elements of {@link DivCountBag#bottom bottom}.
     * 
     * <p>
     * Uses the provided functions to turn the elements into {@link String}s, and uses {@code defaultString}
     * to determine how to represent {@link DivCountBag#top top} if it is empty.
     * 
     * <p>
     * If {@link DivCountBag#bottom bottom} is empty, the " / " is left out and no information about the bottom
     * is appended to the result.
     * 
     * @param selector the function used to convert elements to strings
     * @param defaultString the default string
     * @return the string representation
     */
    public String asDivString(Function<T, String> selector, String defaultString)
    {
        return asDivString(selector, selector, defaultString);
    }

    public static <T> DivCountBag<T> of()
    {
        return new DivCountBag<T>();
    }
    public static <T> DivCountBag<T> of(CountBag<T> top, CountBag<T> bottom)
    {
        return new DivCountBag<T>(top, bottom);
    }
}
