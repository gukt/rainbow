/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

/**
 * ArrayUtils class
 *
 * @author https://github.com/gukt
 */
public final class ArrayUtils {

    /** Prevents to construct an instance. */
    private ArrayUtils() {
        throw new AssertionError("No ArrayUtils instances for you.");
    }

    /**
     * 连接多个数组
     * TODO 看看这里是为什么 Possible heap pollution from parameterized vararg type
     *
     * @param first first array
     * @param rest  other arrays
     * @param <E>   type of array element
     * @return an new array
     */
    public static <E> E[] concat(E[] first, E[]... rest) {
        int len = first.length;
        for (E[] arr : rest) {
            len += arr.length;
        }
        E[] result = Arrays.copyOf(first, len);
        int offset = first.length;
        for (E[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    // @SuppressWarnings("unchecked")
    public static <E> E[] toArray(Collection<?> collection, Class<E> elementType) {
        E[] arr = (E[]) Array.newInstance(elementType, collection.size());
        return collection.toArray(arr);
    }
}
