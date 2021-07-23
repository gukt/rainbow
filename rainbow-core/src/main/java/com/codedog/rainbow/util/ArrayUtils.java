/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.util.Arrays;

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
     * @param <T>   type of array element
     * @return an new array
     */
    public static <T> T[] concat(T[] first, T[]... rest) {
        int len = first.length;
        for (T[] arr : rest) {
            len += arr.length;
        }
        T[] result = Arrays.copyOf(first, len);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
