/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.codedog.rainbow.lang.TypeMismatchException;

import java.util.Arrays;
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

    // Assert.isTrue

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

    // Assert.state

    public static void state(boolean expected) {
        Assert.state(expected, "actual");
    }

    public static void state(boolean expected, String name) {
        if (!expected) {
            throw new IllegalStateException(name + ": false (expected: true)");
        }
    }

    public static void state(Object obj, Supplier<String> supplier) {
        if (obj == null) {
            throw new IllegalStateException(nullSafeGet(supplier));
        }
    }

    public static void state(boolean expected, String format, Object... args) {
        if (!expected) {
            throw new IllegalStateException(String.format(format, args));
        }
    }

    // Assert.notNull

    public static void notNull(Object obj) {
        Assert.notNull(obj, "actual");
    }

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

    public static void notNull(boolean expected, String format, Object... args) {
        if (!expected) {
            throw new IllegalStateException(String.format(format, args));
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

    // Number - Assert.positive

    public static <E extends Number> void positive(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() > 0, "%s: %s (expected: > 0)", name, i);
    }

    public static void positive(byte i, String name) {
        Assert.isTrue(i > 0, "%s: %s (expected: > 0)", name, i);
    }

    public static void positive(short i, String name) {
        Assert.isTrue(i > 0, "%s: %s (expected: > 0)", name, i);
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

    public static void positive(byte i) {
        positive(i, "actual");
    }

    public static void positive(short i) {
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

    // Number - Assert.notPositive

    public static <E extends Number> void notPositive(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() <= 0, "%s: %s (expected: <= 0)", name, i);
    }

    public static void notPositive(byte i, String name) {
        Assert.isTrue(i <= 0, "%s: %s (expected: <= 0)", name, i);
    }

    public static void notPositive(short i, String name) {
        Assert.isTrue(i <= 0, "%s: %s (expected: <= 0)", name, i);
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

    public static void notPositive(byte i) {
        notPositive(i, "actual");
    }

    public static void notPositive(short i) {
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

    // Number - Assert.negative

    public static <E extends Number> void negative(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() < 0, "%s: %s (expected: < 0)", name, i);
    }

    public static void negative(byte i, String name) {
        Assert.isTrue(i < 0, "%s: %s (expected: < 0)", name, i);
    }

    public static void negative(short i, String name) {
        Assert.isTrue(i < 0, "%s: %s (expected: < 0)", name, i);
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

    public static void negative(byte i) {
        negative(i, "actual");
    }

    public static void negative(short i) {
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

    // Number - Assert.notNegative

    public static <E extends Number> void notNegative(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() >= 0, "%s: %s (expected: >= 0)", name, i);
    }

    public static void notNegative(byte i, String name) {
        Assert.isTrue(i >= 0, "%s: %s (expected: >= 0)", name, i);
    }

    public static void notNegative(short i, String name) {
        Assert.isTrue(i >= 0, "%s: %s (expected: >= 0)", name, i);
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

    public static void notNegative(byte i) {
        notNegative(i, "actual");
    }

    public static void notNegative(short i) {
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

    // Number - Assert.zero

    public static <E extends Number> void zero(E i, String name) {
        Assert.notNull(i, "i");
        Assert.isTrue(i.intValue() == 0, "%s: %s (expected: = 0)", name, i);
    }

    public static void zero(byte i, String name) {
        Assert.isTrue(i == 0, "%s: %s (expected: = 0)", name, i);
    }

    public static void zero(short i, String name) {
        Assert.isTrue(i == 0, "%s: %s (expected: = 0)", name, i);
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

    public static void zero(byte i) {
        zero(i, "actual");
    }

    public static void zero(short i) {
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

    // Number - Assert.between

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

    // Types - isInstanceOfAny / isAssignableFrom*

    /**
     * 判断指定的对象是否是“任意一个“指定类型的实例
     *
     * @param obj           被检测的对象，不能为 null
     * @param expectedTypes 父类（或接口）类型数组，至少有一个元素
     * @see #isAssignableFrom(Class, Class[]) isAssignableFrom
     * @see #isAssignableFromAny(Class, Class[]) isAssignableFromAny
     */
    public static void isInstanceOfAny(Object obj, Class<?>... expectedTypes) {
        Assert.notNull(obj, "obj");
        Assert.isAssignableFromAny(obj.getClass(), expectedTypes);
    }

    /**
     * 判断“指定的类型”是否实现了指定的“所有”父类（或接口）类型。如果要判断“任意一个”接口，请使用 {@link #isAssignableFromAny(Class, Class[])} 方法。
     *
     * @param type       被检测的类型，不可为 null
     * @param superTypes 父类（或接口）类型数组，至少有一个元素
     * @see #isAssignableFromAny(Class, Class[]) isAssignableFromAny
     * @see #isInstanceOfAny(Object, Class[]) isInstanceOfAny
     */
    public static void isAssignableFrom(Class<?> type, Class<?>... superTypes) {
        Assert.notNull(type, "type");
        Assert.notEmpty(superTypes, "superTypes");
        boolean matched = Arrays.stream(superTypes).allMatch(superType -> superType.isAssignableFrom(type));
        if (!matched) {
            throw new TypeMismatchException(type, superTypes);
        }
    }

    /**
     * 判断“指定的类型”是否实现了指定的"任意一个"父类（或接口）类型。如果要判断“所有”类型，请使用 {@link #isAssignableFrom(Class, Class[])} 方法。
     *
     * @param type       被检测的类型，不可为 null
     * @param superTypes 父类（或接口）类型数组，至少有一个元素
     * @see #isAssignableFrom(Class, Class[]) isAssignableFrom
     * @see #isInstanceOfAny(Object, Class[]) isInstanceOfAny
     */
    public static void isAssignableFromAny(Class<?> type, Class<?>... superTypes) {
        Assert.notNull(type, "type");
        Assert.notEmpty(superTypes, "superTypes");
        boolean matched = Arrays.stream(superTypes).anyMatch(superType -> superType.isAssignableFrom(type));
        if (!matched) {
            throw new TypeMismatchException(type, superTypes);
        }
    }

    // Internal helper methods

    private static <V> V nullSafeGet(Supplier<V> supplier) {
        return supplier != null ? supplier.get() : null;
    }

    private static <V> V nullSafeGet(Map<?, V> map, Object key) {
        return map != null ? map.get(key) : null;
    }
}
