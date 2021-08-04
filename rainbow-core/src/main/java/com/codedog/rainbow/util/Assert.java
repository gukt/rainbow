/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Assert class
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
public final class Assert {

    /** Prevents to construct an instance. */
    private Assert() {
        throw new AssertionError("No Assert instances for you!");
    }

    // state
    public static void state(boolean expected) {

    }

    // Assert.notNull

    public static void notNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + ": null (expected: non-null)");
        }
    }

    public static void notNull(Object obj, Supplier<String> supplier) {
        if (obj == null) {
            throw new IllegalArgumentException(nullSafeGet(supplier));
        }
    }

    // Assert.notEmpty

    public static <E extends Map<?, ?>> void notEmpty(E map, String name) {
        Assert.notNull(map, name);
        Assert.isTrue(!map.isEmpty(), name + ".isEmpty()");
    }

    /**
     * 检查指定的 {@link Collection} 对象是否不为 null，且不为空。
     *
     * @param collection 被检测的 {@link Collection} 对象
     * @param name       被检测对象的名称，用于错误描述
     * @param <E>        被检测对象的类型
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Collection<?>> void notEmpty(E collection, String name) {
        Assert.notNull(collection, name);
        Assert.isTrue(!collection.isEmpty(), name + ".isEmpty()");
    }

    /**
     * 检查指定的 {@link Iterable} 对象是否不为 null，且不为空。
     *
     * @param iterable 被检测的 {@link Iterable} 对象
     * @param name     被检测对象的名称，用于错误描述
     * @param <E>      {@link Iterable} 元素类型
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Iterable<?>> void notEmpty(E iterable, String name) {
        Assert.notNull(iterable, name);
        Assert.isTrue(iterable.iterator().hasNext(), name + ".iterator().hasNext()");
    }

    /**
     * 检查指定的 {@link CharSequence} 对象是否不为 null，且不为空。当然也包含对 {@link String} 的检测。
     *
     * @param chars 被检测的 {@link CharSequence} 对象
     * @param name  被检测对象的名称，用于错误描述
     * @param <E>   被检测对象的类型
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends CharSequence> void notEmpty(E chars, String name) {
        Assert.notNull(chars, name);
        Assert.positive(chars.length(), name + ".length()");
    }

    /**
     * 检查指定的对象数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的对象数组
     * @param name 被检测对象数组的名称，用于错误描述
     * @param <E>  被检测对象数组的元素类型
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E> void notEmpty(E[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(byte[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(short[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(int[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(long[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(float[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(double[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(boolean[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @throws IllegalArgumentException 如果被检测对象为 null 或为 empty
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static void notEmpty(char[] arr, String name) {
        Assert.notNull(arr, name);
        Assert.positive(arr.length, name + ".length");
    }

    // Number

    public static <E extends Comparable<E>> void between(E i, E start, E end, String name) {
        Assert.notNull(i, "i");
        Assert.notNull(start, "start");
        Assert.notNull(end, "end");
        Assert.notNull(name, "name");
        Assert.isTrue(start.compareTo(end) <= 0, () -> "start > end (expected: start <= end)");
        Assert.isTrue(i.compareTo(start) >= 0 && i.compareTo(end) <= 0,
                "%s: %s (expected: in [%s, %s])", name, i, start, end);
    }

    public static <E extends Comparable<E>> void between(E i, E start, E end) {
        between(i, start, end, "actual");
    }

    public static <E extends Number> void positive(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() > 0, "%s: %s (expected: > 0)", name, i);
    }

    public static void positive(int i, String name) {
        Assert.isTrue(i > 0, "%s: %s (expected: > 0)", name, i);
    }

    public static void positive(long i, String name) {
        Assert.isTrue(i > 0, "%s: %s (expected: > 0)", name, i);
    }

    public static void positive(double i, String name) {
        Assert.isTrue(i > 0, "%s: %s (expected: > 0)", name, i);
    }

    public static void positive(float i, String name) {
        Assert.isTrue(i > 0, "%s: %s (expected: > 0)", name, i);
    }

    public static <E extends Number> void positive(E i) {
        positive(i, "actual");
    }

    public static void positive(int i) {
        positive(i, "actual");
    }

    public static void positive(long i) {
        positive(i, "actual");
    }

    public static void positive(double i) {
        positive(i, "actual");
    }

    public static void positive(float i) {
        positive(i, "actual");
    }

    public static <E extends Number> void notPositive(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() <= 0, "%s: %s (expected: <= 0)", name, i);
    }

    public static void notPositive(int i, String name) {
        Assert.isTrue(i <= 0, "%s: %s (expected: <= 0)", name, i);
    }

    public static void notPositive(long i, String name) {
        Assert.isTrue(i <= 0, "%s: %s (expected: <= 0)", name, i);
    }

    public static void notPositive(double i, String name) {
        Assert.isTrue(i <= 0, "%s: %s (expected: <= 0)", name, i);
    }

    public static void notPositive(float i, String name) {
        Assert.isTrue(i <= 0, "%s: %s (expected: <= 0)", name, i);
    }

    public static <E extends Number> void notPositive(E i) {
        notPositive(i, "actual");
    }

    public static void notPositive(int i) {
        notPositive(i, "actual");
    }

    public static void notPositive(long i) {
        notPositive(i, "actual");
    }

    public static void notPositive(double i) {
        notPositive(i, "actual");
    }

    public static void notPositive(float i) {
        notPositive(i, "actual");
    }

    public static <E extends Number> void negative(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() < 0, "%s: %s (expected: < 0)", name, i);
    }

    public static void negative(int i, String name) {
        Assert.isTrue(i < 0, "%s: %s (expected: < 0)", name, i);
    }

    public static void negative(long i, String name) {
        Assert.isTrue(i < 0, "%s: %s (expected: < 0)", name, i);
    }

    public static void negative(double i, String name) {
        Assert.isTrue(i < 0, "%s: %s (expected: < 0)", name, i);
    }

    public static void negative(float i, String name) {
        Assert.isTrue(i < 0, "%s: %s (expected: < 0)", name, i);
    }

    public static <E extends Number> void negative(E i) {
        negative(i, "actual");
    }

    public static void negative(int i) {
        negative(i, "actual");
    }

    public static void negative(long i) {
        negative(i, "actual");
    }

    public static void negative(double i) {
        negative(i, "actual");
    }

    public static void negative(float i) {
        negative(i, "actual");
    }

    public static <E extends Number> void notNegative(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() >= 0, "%s: %s (expected: >= 0)", name, i);
    }

    public static void notNegative(int i, String name) {
        Assert.isTrue(i >= 0, "%s: %s (expected: >= 0)", name, i);
    }

    public static void notNegative(long i, String name) {
        Assert.isTrue(i >= 0, "%s: %s (expected: >= 0)", name, i);
    }

    public static void notNegative(double i, String name) {
        Assert.isTrue(i >= 0, "%s: %s (expected: >= 0)", name, i);
    }

    public static void notNegative(float i, String name) {
        Assert.isTrue(i >= 0, "%s: %s (expected: >= 0)", name, i);
    }

    public static <E extends Number> void notNegative(E i) {
        notNegative(i, "actual");
    }

    public static void notNegative(int i) {
        notNegative(i, "actual");
    }

    public static void notNegative(long i) {
        notNegative(i, "actual");
    }

    public static void notNegative(double i) {
        notNegative(i, "actual");
    }

    public static void notNegative(float i) {
        notNegative(i, "actual");
    }

    public static <E extends Number> void zero(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() == 0, "%s: %s (expected: = 0)", name, i);
    }

    public static void zero(int i, String name) {
        Assert.isTrue(i == 0, "%s: %s (expected: = 0)", name, i);
    }

    public static void zero(long i, String name) {
        Assert.isTrue(i == 0, "%s: %s (expected: = 0)", name, i);
    }

    public static void zero(double i, String name) {
        Assert.isTrue(i == 0, "%s: %s (expected: = 0)", name, i);
    }

    public static void zero(float i, String name) {
        Assert.isTrue(i == 0, "%s: %s (expected: = 0)", name, i);
    }

    public static <E extends Number> void zero(E i) {
        zero(i, "actual");
    }

    public static void zero(int i) {
        zero(i, "actual");
    }

    public static void zero(long i) {
        zero(i, "actual");
    }

    public static void zero(double i) {
        zero(i, "actual");
    }

    public static void zero(float i) {
        zero(i, "actual");
    }

    // isTrue

    public static void isTrue(boolean expected) {
        if (!expected) {
            throw new IllegalArgumentException();
        }
    }

    public static void isTrue(boolean expected, String name) {
        if (!expected) {
            throw new IllegalArgumentException(name + ": false (expected: true)");
        }
    }

    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    public static void isTrue(boolean expected, String format, Object... args) {
        if (!expected) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    private static <V> V nullSafeGet(Supplier<V> supplier) {
        return supplier != null ? supplier.get() : null;
    }

    private static <V> V nullSafeGet(Map<?, V> map, Object key) {
        return map != null ? map.get(key) : null;
    }

    // Internal helper methods
}
