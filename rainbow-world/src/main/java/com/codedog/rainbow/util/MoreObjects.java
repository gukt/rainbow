/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * MoreObjects class
 *
 * <p>nullToDefaultMap, nullToDefaultList, nullToDefaultConcurrentMap, nullToDefaultConcurrentList
 *
 * @author https://github.com/gukt
 */
public class MoreObjects {

    /** Prevents to construct an instance. */
    private MoreObjects() {
        throw new AssertionError("No MoreObjects instances for you!");
    }

    /**
     * Returns the {@code obj} itself if not null, or specified default value if null.
     *
     * @param obj          the object to test，may be null
     * @param defaultValue the default value，must not be null
     * @return 如果被测试的对象不为 null 则返回自身，反之，返回指定的默认值
     */
    public static <E> E nullToDefault(@Nullable E obj, E defaultValue) {
        return obj == null ? defaultValue : obj;
    }

    // requireNonEmpty

    /**
     * Checks that the specified {@link Map} object is not null and not empty. this method is designed
     * primarily for argument checking in methods and constructors.
     *
     * @param obj  the object to test, may be null
     * @param name the name of {@code obj}
     * @param <E>  the type of {@code obj}
     * @return {@code obj} if not null and not empty
     */
    public static <E extends Map<?, ?>> E requireNonEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        checkArgument(!obj.isEmpty(), "%s.isEmpty(): true (expected: false)", name);
        return obj;
    }

    /**
     * Checks that the specified {@link Collection} object is not null and not empty. this method is
     * designed primarily for argument checking in methods and constructors.
     *
     * @param obj  the object to test, may be null
     * @param name the name of {@code obj}
     * @param <E>  the type of {@code obj}
     * @return {@code obj} if not null and not empty
     */
    public static <E extends Collection<?>> E requireNonEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        checkArgument(!obj.isEmpty(), "%s.isEmpty(): true (expected: false)", name);
        return obj;
    }

    public static <E extends Iterable<?>> E requireNonEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        checkArgument(
                obj.iterator().hasNext(), "%s.iterator().hasNext(): false (expected: true)", name);
        return obj;
    }

    public static <E extends CharSequence> E requireNonEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        requirePositive(obj.length(), name + ".length");
        return obj;
    }

    public static <E> E[] requireNonEmpty(@Nullable E[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> byte[] requireNonEmpty(@Nullable byte[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> short[] requireNonEmpty(@Nullable short[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> int[] requireNonEmpty(@Nullable int[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> long[] requireNonEmpty(@Nullable long[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> float[] requireNonEmpty(@Nullable float[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> double[] requireNonEmpty(@Nullable double[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> boolean[] requireNonEmpty(@Nullable boolean[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    public static <E> char[] requireNonEmpty(@Nullable char[] arr, String name) {
        requireNonNull(arr, name);
        requirePositive(arr.length, name + ".length");
        return arr;
    }

    // requireEmpty

    public static <E extends Map<?, ?>> E requireEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        checkArgument(obj.isEmpty(), "%s.isEmpty(): false (expected: = true)", name);
        return obj;
    }

    public static <E extends Collection<?>> E requireEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        checkArgument(obj.isEmpty(), "%s.isEmpty(): false (expected: = true)", name);
        return obj;
    }

    public static <E extends Iterable<?>> E requireEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        checkArgument(
                !obj.iterator().hasNext(), "%s.iterator().hasNext(): false (expected: = true)", name);
        return obj;
    }

    public static <E extends CharSequence> E requireEmpty(@Nullable E obj, String name) {
        requireNonNull(obj, name);
        requireZero(obj.length(), name + ".length");
        return obj;
    }

    public static <E> E[] requireEmpty(@Nullable E[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static char[] requireEmpty(@Nullable char[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static boolean[] requireEmpty(@Nullable boolean[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static byte[] requireEmpty(@Nullable byte[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static short[] requireEmpty(@Nullable short[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static int[] requireEmpty(@Nullable int[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static long[] requireEmpty(@Nullable long[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static float[] requireEmpty(@Nullable float[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    public static double[] requireEmpty(@Nullable double[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    // isNullOrEmpty

    public static <E extends Collection<?>> boolean isNullOrEmpty(@Nullable E obj) {
        return obj == null || obj.isEmpty();
    }

    public static <E extends Iterable<?>> boolean isNullOrEmpty(@Nullable E obj) {
        return obj == null || !obj.iterator().hasNext();
    }

    public static <E extends Map<?, ?>> boolean isNullOrEmpty(@Nullable E obj) {
        return obj == null || obj.isEmpty();
    }

    public static <E extends CharSequence> boolean isNullOrEmpty(@Nullable E obj) {
        return obj == null || obj.length() == 0;
    }

    public static <E> boolean isNullOrEmpty(@Nullable E[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable char[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable byte[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable short[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable int[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable long[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable float[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable double[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable boolean[] obj) {
        return obj == null || obj.length == 0;
    }

    // Number validations

    public static <E extends Comparable<E>> E requireBetween(E i, E start, E end, String name) {
        requireNonNull(i, "i");
        requireNonNull(start, "start");
        requireNonNull(end, "end");
        checkArgument(start.compareTo(end) <= 0, "start must be less than or equal to end");

        checkArgument(
                i.compareTo(start) >= 0 && i.compareTo(end) <= 0,
                "%s: %s (expected: in [%s, %s])",
                name,
                i,
                start,
                end);
        return i;
    }

    public static <E extends Comparable<E>> E requireBetween(E i, E start, E end) {
        return requireBetween(i, start, end, "actual");
    }

    public static <E extends Comparable<E>> boolean isBetween(E i, E start, E end) {
        return i.compareTo(start) >= 0 && i.compareTo(end) <= 0;
    }

    /**
     * Checks that the specified {@code i} is positive (> 0). this method is designed primarily for
     * argument checking in methods and constructors.
     *
     * @param i    the number to test
     * @param name the name of {@code i}
     * @return {@code i}
     */
    public static <E extends Number> E requirePositive(@Nullable E i, String name) {
        requireNonNull(i);
        checkArgument(i.intValue() > 0, "%s: %s (expected: > 0)", name, i);
        return i;
    }

    public static <E extends Number> E requirePositive(@Nullable E i) {
        return requirePositive(i, "actual");
    }

    /**
     * Checks that the specified {@code i} is non positive (<= 0). this method is designed primarily
     * for argument checking in methods and constructors.
     *
     * @param i    the number to test
     * @param name the name of {@code i}
     * @return {@code i}
     */
    public static <E extends Number> E requireNonPositive(@Nullable E i, String name) {
        requireNonNull(i);
        checkArgument(i.intValue() <= 0, "%s: %s (expected: <= 0)", name, i);
        return i;
    }

    public static <E extends Number> E requireNonPositive(@Nullable E i) {
        return requireNonPositive(i, "actual");
    }

    /**
     * Checks that the specified {@code i} is negative (< 0). this method is designed primarily for
     * argument checking in methods and constructors.
     *
     * @param i    the number to test
     * @param name the name of {@code i}
     * @return {@code i}
     */
    public static <E extends Number> E requireNegative(@Nullable E i, String name) {
        requireNonNull(i);
        checkArgument(i.intValue() < 0, "%s: %s (expected: < 0)", name, i);
        return i;
    }

    public static <E extends Number> E requireNegative(@Nullable E i) {
        return requireNegative(i, "actual");
    }

    /**
     * Checks that the specified {@code i} is non negative (>= 0). this method is designed primarily
     * for argument checking in methods and constructors.
     *
     * @param i    the number to test
     * @param name the name of {@code i}
     * @return {@code i}
     */
    public static <E extends Number> E requireNonNegative(@Nullable E i, String name) {
        requireNonNull(i);
        checkArgument(i.intValue() >= 0, "%s: %s (expected: >= 0)", name, i);
        return i;
    }

    public static <E extends Number> E requireNonNegative(@Nullable E i) {
        return requireNonNegative(i, "actual");
    }

    public static <E extends Number> E requireZero(@Nullable E i, String name) {
        requireNonNull(i);
        checkArgument(i.intValue() == 0, "%s: %s (expected: = 0)", name, i);
        return i;
    }

    public static <E extends Number> E requireZero(@Nullable E i) {
        return requireZero(i, "actual");
    }

    // Method references for Predicate

    public static <E extends Number> boolean isPositive(E i) {
        return i.intValue() > 0;
    }

    /**
     * Returns {@code true} if the provided number is non positive (<=0) otherwise {@code false}
     *
     * @param i the number to test
     * @return {@code true} if the provided number is non positive (<=0) otherwise {@code false}
     * @apiNote This method exists to be used as a {@link java.util.function.Predicate}, {@code
     * filter(Numbers::nonPositive)}
     * @see java.util.function.Predicate
     * @since 1.8
     */
    public static <E extends Number> boolean nonPositive(E i) {
        return i.intValue() <= 0;
    }

    public static <E extends Number> boolean isNegative(E i) {
        return i.intValue() < 0;
    }

    /**
     * Returns {@code true} if the provided number is non negative (>=0) otherwise {@code false}
     *
     * @param i the number to test
     * @return {@code true} if the provided number is non negative (>=0) otherwise {@code false}
     * @apiNote This method exists to be used as a {@link java.util.function.Predicate}, {@code
     * filter(Numbers::nonNegative)}
     * @see java.util.function.Predicate
     * @since 1.8
     */
    public static <E extends Number> boolean nonNegative(E i) {
        return i.intValue() >= 0;
    }

    public static <E extends Number> boolean isZero(E i) {
        return i.intValue() == 0;
    }

    public static <E extends Number> boolean nonZero(E i) {
        return i.intValue() != 0;
    }

    // checkArgument

    public static void checkArgument(boolean expected, String errMessage) {
        if (!expected) {
            throw new IllegalArgumentException(errMessage);
        }
    }

    public static void checkArgument(boolean expected) {
        if (!expected) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expected, String format, Object... args) {
        if (!expected) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    // Misc

    public static <E> String toString(Map<String, E[]> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        map.forEach(
                (k, v) -> {
                    sb.append(k).append("=");
                    if (v.length == 1) {
                        sb.append(v[0]);
                    } else {
                        sb.append(Arrays.toString(v));
                    }
                    sb.append(", ");
                });
        sb.append("}");
        return sb.toString().replace(", }", "}");
    }

    // Internal helper methods
}
