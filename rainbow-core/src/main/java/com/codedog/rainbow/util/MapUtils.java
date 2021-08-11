/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * Map utilities
 *
 * @author https://github.com/gukt
 */
public final class MapUtils {

    /** Prevents to construct an instance. */
    private MapUtils() {
        throw new AssertionError("No MapUtils instances for you.");
    }

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
                throw new IllegalArgumentException("Length of pairs must be even. actual: "
                        + pair.length + " (expected: 2)" + ", element: " + Arrays.toString(pair));
            }
            map.put((K) pair[0], (V) pair[1]);
        }
        return map;
    }

    public static <K, V> Map<K, V> include(Map<K, V> source, String... fields) {
        Set<String> fieldSet = Sets.newHashSet(fields);
        Map<K, V> retMap = new HashMap<>();
        source.forEach(
                ((k, v) -> {
                    if (fieldSet.contains(k.toString())) {
                        retMap.put(k, v);
                    }
                }));
        return retMap;
    }

    public static String toString(Map<?, ?> map, final String delimiter, boolean reverse) {
        // Assert.notNull(map, "map");
        // StringBuilder sb = new StringBuilder();
        // map.entrySet().forEach((Consumer<Entry<?, ?>>) entry -> sb.append(entry.getKey()).append(delimiter).append(entry.getValue()));
        // return new StringJoiner(", ", "{" + sb.toString(), "}").toString();
        return null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MapUtils.class.getSimpleName() + "[", "]")
                .toString();
    }
}
