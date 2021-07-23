/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Map utils
 *
 * TODO 定时清理 Map，指定 key, value, ttl, map 指定全局 ttl， key 指定局部 ttl，
 *
 * @author https://github.com/gukt
 */
public final class MapUtils {

    /** Prevents to construct an instance. */
    private MapUtils() {
        throw new AssertionError("No MapUtils instances for you.");
    }


//    public static boolean isNullOrEmpty(@Nullable Map<?, ?> map) {
//        return ObjectUtils.isNullOrEmpty(map);
//    }

    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> newHashMap(K key, V value) {
        Map<K, V> map = newHashMap();
        map.put(key, value);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> newHashMap(Object[]... pairs) {
        Map<K, V> map = newHashMap();
        for (Object[] pair : pairs) {
            if (pair.length != 2) {
                throw new IllegalArgumentException("Length of pair must be even. actual: "
                        + pair.length + " (expected: 2)" + ", element: " + Arrays.toString(pair));
            }
            map.put((K) pair[0], (V) pair[1]);
        }
        return map;
    }

    public static Map<String, Object> newHashMap(Object obj, String... fields) {
        Objects.requireNonNull(obj);
        Map<String, Object> map = newHashMap();
        Arrays.stream(obj.getClass().getDeclaredFields())
                .forEach(field -> Arrays.stream(fields)
                        .filter(key -> Objects.equals(field.getName(), key))
                        .forEach(key -> {
                            try {
                                field.setAccessible(true);
                                map.put(key, field.get(obj));
                            } catch (IllegalAccessException e) {
                                System.out.println("Cannot read field value: field=" + field);
                            } finally {
                                field.setAccessible(false);
                            }
                        }));
        return map;
    }
}
