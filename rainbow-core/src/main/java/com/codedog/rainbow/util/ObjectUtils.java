/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * MoreObjects class
 *
 * <p>nullToDefaultMap, nullToDefaultList, nullToDefaultConcurrentMap, nullToDefaultConcurrentList
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
public final class ObjectUtils {

    /** Prevents to construct an instance. */
    private ObjectUtils() {
        throw new AssertionError("No ObjectUtils instances for you!");
    }

    /**
     * 如果给定的对象不为 null 则返回自身；反之返回指定的默认值。
     *
     * @param obj          被检测的对象，可以为 null
     * @param defaultValue 默认值，不能为 null
     * @return 如果给定的对象不为 null 则返回自身；反之返回指定的默认值
     */
    public static <E> E nullToDefault(E obj, E defaultValue) {
        Assert.notNull(defaultValue, "defaultValue");
        return obj == null ? defaultValue : obj;
    }

    public static <T extends Iterable<E>, E> T nullToDefault(T obj, T defaultValue) {
        Assert.notNull(defaultValue, "defaultValue");
        return obj == null ? defaultValue : obj;
    }

    /**
     * 判断指定的字符串是否为 {@code null}，如果是，返回空字符串({@link String ""})；反之返回被检测对象自身。
     * @param obj 被检测的字符串对象，可以为 null
     * @return 如果被检测对象为 null，返回空字符串；反之返回被检测对象
     */
    public static String nullToEmpty(String obj) {
        return obj == null ? "" : obj;
    }

    // TODO nullToEmpty: String, List, Set, Map, LinkedMap, Object[], Array 等

    // requireNonNull

    public static <E> E requireNonNull(E obj, String name) {
        Assert.notNull(obj, name);
        return obj;
    }

    public static <E> E requireNonNull(E obj, Supplier<String> messageSupplier) {
        Assert.notNull(obj, messageSupplier);
        return obj;
    }

    // requireNonEmpty

    /**
     * 检查指定的 {@link Map} 对象是否不为 null，且不为空。
     *
     * @param map  被检测的 {@link Collection} 对象
     * @param name 被检测对象的名称，用于错误描述
     * @param <E>  被检测对象的类型
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Map<?, ?>> E requireNonEmpty(E map, String name) {
        Assert.notEmpty(map, name);
        return map;
    }

    /**
     * 检查指定的 {@link Collection} 对象是否不为 null，且不为空。
     *
     * @param collection 被检测的 {@link Collection} 对象
     * @param name       被检测对象的名称，用于错误描述
     * @param <E>        被检测对象的类型
     * @return 如果检测成功，返回被检测的对象。
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Collection<?>> E requireNonEmpty(E collection, String name) {
        Assert.notEmpty(collection, name);
        return collection;
    }

    /**
     * 检查指定的 {@link Iterable} 对象是否不为 null，且不为空。
     *
     * @param iterable 被检测的 {@link Iterable} 对象
     * @param name     被检测对象的名称，用于错误描述
     * @param <E>      {@link Iterable} 元素类型
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Iterable<?>> E requireNonEmpty(E iterable, String name) {
        Assert.notEmpty(iterable, name);
        return iterable;
    }

    /**
     * 检查指定的 {@link CharSequence} 对象是否不为 null，且不为空。当然也包含对 {@link String} 的检测。
     *
     * @param chars 被检测的 {@link CharSequence} 对象
     * @param name  被检测对象的名称，用于错误描述
     * @param <E>   被检测对象的类型
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends CharSequence> E requireNonEmpty(E chars, String name) {
        Assert.notEmpty(chars, name);
        return chars;
    }

    /**
     * 检查指定的对象数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的对象数组
     * @param name 被检测对象数组的名称，用于错误描述
     * @param <E>  被检测对象数组的元素类型
     * @return 如果检测成功，返回被检测的对象数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E> E[] requireNonEmpty(E[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static byte[] requireNonEmpty(byte[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static short[] requireNonEmpty(short[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static int[] requireNonEmpty(int[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static long[] requireNonEmpty(long[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static float[] requireNonEmpty(float[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static double[] requireNonEmpty(double[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static boolean[] requireNonEmpty(boolean[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    /**
     * 检查指定的数组是否不为 null，且 length > 0。
     *
     * @param arr  被检测的数组
     * @param name 被检测数组的名称，用于错误描述
     * @return 如果检测成功，返回被检测的数组
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static char[] requireNonEmpty(char[] arr, String name) {
        Assert.notEmpty(arr, name);
        return arr;
    }

    // requireEmpty

    /**
     * 检查指定的 {@link Map} 对象是否不为 null，且 <code>isEmpty() == true</code>。
     *
     * @param map  被检测的 {@link Map} 对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Map<?, ?>> E requireEmpty(E map, String name) {
        requireNonNull(map, name);
        Assert.isTrue(map.isEmpty(), "%s.isEmpty(): false (expected: = true)", name);
        return map;
    }

    /**
     * 检查指定的 {@link Collection} 对象是否不为 null，且 <code>isEmpty() == true</code>。
     *
     * @param collection 被检测的 {@link Collection} 对象
     * @param name       被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Collection<?>> E requireEmpty(E collection, String name) {
        requireNonNull(collection, name);
        Assert.isTrue(collection.isEmpty(),
                "%s.isEmpty(): false (expected: = true)", name);
        return collection;
    }

    /**
     * 检查指定的 {@link Iterable} 对象是否不为 null，且 <code>iterator.hasNext() == true</code>。
     *
     * @param iterable 被检测的 {@link Iterable} 对象
     * @param name     被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends Iterable<?>> E requireEmpty(E iterable, String name) {
        requireNonNull(iterable, name);
        Assert.isTrue(!iterable.iterator().hasNext(),
                "%s.iterator().hasNext(): false (expected: = true)", name);
        return iterable;
    }

    /**
     * 检查指定的 {@link CharSequence} 对象是否不为 null，且 <code>length() == 0</code>。
     *
     * @param chars 被检测的 {@link CharSequence} 对象
     * @param name  被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E extends CharSequence> E requireEmpty(E chars, String name) {
        requireNonNull(chars, name);
        requireZero(chars.length(), name + ".length");
        return chars;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static <E> E[] requireEmpty(E[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static char[] requireEmpty(char[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static boolean[] requireEmpty(boolean[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static byte[] requireEmpty(byte[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static short[] requireEmpty(short[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static int[] requireEmpty(int[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static long[] requireEmpty(long[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static float[] requireEmpty(float[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    /**
     * 检查指定的数组对象是否不为 null，且 <code>arr.length == 0</code>。
     *
     * @param arr  被检测的数组对象
     * @param name 被检测对象的名称，用于错误描述
     * @return 如果检测成功，返回被检测的对象
     * @apiNote 该方法设计主要用于在方法或构造器中作参数检测用。
     */
    public static double[] requireEmpty(double[] arr, String name) {
        requireNonNull(arr, name);
        requireZero(arr.length, name + ".length");
        return arr;
    }

    // Number validations

    public static <E extends Comparable<E>> E requireBetween(E i, E start, E end, String name) {
        Assert.between(i, start, end, name);
        return i;
    }

    public static <E extends Comparable<E>> E requireBetween(E i, E start, E end) {
        return requireBetween(i, start, end, "actual");
    }

    public static <E extends Number> E requirePositive(E i, String name) {
        Assert.positive(i, name);
        return i;
    }

    public static <E extends Number> E requirePositive(E i) {
        return requirePositive(i, "actual");
    }

    public static <E extends Number> E requireNonPositive(E i, String name) {
        Assert.notPositive(i, name);
        return i;
    }

    public static <E extends Number> E requireNonPositive(E i) {
        return requireNonPositive(i, "actual");
    }

    public static <E extends Number> E requireNegative(E i, String name) {
        Assert.negative(i, name);
        return i;
    }

    public static <E extends Number> E requireNegative(E i) {
        return requireNegative(i, "actual");
    }

    public static <E extends Number> E requireNonNegative(E i, String name) {
        Assert.notNegative(i, name);
        return i;
    }

    public static <E extends Number> E requireNonNegative(E i) {
        return requireNonNegative(i, "actual");
    }

    public static <E extends Number> E requireZero(E i, String name) {
        Assert.zero(i, name);
        return i;
    }

    public static <E extends Number> E requireZero(E i) {
        return requireZero(i, "actual");
    }

    // Method references for lambda

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    // isEmpty

    public static <E extends Collection<?>> boolean isEmpty(E collection) {
        return collection == null || collection.isEmpty();
    }

    public static <E extends Iterable<?>> boolean isEmpty(E iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    public static <E extends Map<?, ?>> boolean isEmpty(E map) {
        return map == null || map.isEmpty();
    }

    public static <E extends CharSequence> boolean isEmpty(E chars) {
        return chars == null || chars.length() == 0;
    }

    public static <E> boolean isEmpty(E[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(char[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(byte[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(short[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(int[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(long[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(float[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(double[] arr) {
        return arr == null || arr.length == 0;
    }

    public static boolean isEmpty(boolean[] arr) {
        return arr == null || arr.length == 0;
    }

    // isNotEmpty

    public static <E extends Collection<?>> boolean isNotEmpty(E collection) {
        return !isEmpty(collection);
    }

    public static <E extends Iterable<?>> boolean isNotEmpty(E iterable) {
        return !isEmpty(iterable);
    }

    public static <E extends Map<?, ?>> boolean isNotEmpty(E map) {
        return !isEmpty(map);
    }

    public static <E extends CharSequence> boolean isNotEmpty(E chars) {
        return !isEmpty(chars);
    }

    public static <E> boolean isNotEmpty(E[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(char[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(byte[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(short[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(int[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(long[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(float[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(double[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(boolean[] arr) {
        return !isEmpty(arr);
    }

    public static <E extends Number> boolean isPositive(E i) {
        return i.intValue() > 0;
    }

    public static <E extends Number> boolean isNegative(E i) {
        return !isPositive(i);
    }

    public static <E extends Number> boolean nonPositive(E i) {
        return i.intValue() <= 0;
    }

    public static <E extends Number> boolean nonNegative(E i) {
        requireNonNull(i, "i");
        return i.intValue() >= 0;
    }

    public static <E extends Number> boolean isZero(E i) {
        requireNonNull(i, "i");
        return i.intValue() == 0;
    }

    public static <E extends Number> boolean nonZero(E i) {
        requireNonNull(i, "i");
        return i.intValue() != 0;
    }

    public static <E extends Comparable<E>> boolean isBetween(E i, E start, E end) {
        requireNonNull(i, "i");
        requireNonNull(start, "start");
        requireNonNull(end, "end");
        return i.compareTo(start) >= 0 && i.compareTo(end) <= 0;
    }

    public static <E extends Comparable<E>> boolean isNotBetween(E i, E start, E end) {
        return !isBetween(i, start, end);
    }


    // Misc

    public static <V extends Annotation> V requireAnnotation(Class<?> type, Class<V> annotationType) {
        Assert.isAnnotationPresent(type, annotationType);
        return type.getAnnotation(annotationType);
    }

    public static int safeGetSize(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static int safeGetSize(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    // TODO 重命名为 getRequired(map, key)
//    @SuppressWarnings("unchecked")
//    public static <V> V safeGetValue(Map<String, Object> map, String key) {
//        Object value = map.get(key);
//        checkNotNull(value, ERR_BAD_PARAMETER.error("The field '" + key + "' is not present"));
//        return (V) value;
//    }

    /**
     * 将一个多值（数组类型）的 {@link Map} 对象转换为字符串，常用于对 HttpServletRequest 中所有参数的输出。
     *
     * @param map value 为数组的 map
     * @param <E> value 数组的类型
     * @return 字符串
     */
    public static <E> String toString(Map<String, E[]> map) {
        requireNonNull(map, "map");
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

    // Internal Helper Methods
}
