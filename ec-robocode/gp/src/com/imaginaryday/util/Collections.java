package com.imaginaryday.util;

import java.util.List;
import java.util.ArrayList;

/**
 * <b>
 * User: jlowens<br>
 * Date: Nov 10, 2006<br>
 * Time: 8:15:48 PM<br>
 * </b>
 */
public class Collections {
    @SuppressWarnings({"unchecked"})
    public static <T> T[] toArray(List<T> list) {
        T[] array = (T[])new Object[list.size()]; // f*ck the erasures! go reified generics!
        int i = 0;
        for (T el : list) array[i++] = el;
        return array;
    }

    public static <T> List<T> toList(T[] array) {
        List<T> l = new ArrayList<T>();
        for (T el : array) l.add(el);
        return l;
    }

    public static <T> List<T> asList(T ... els) {
        List<T> l = new ArrayList<T>();
        for (T el : els) l.add(el);
        return l;
    }
}
