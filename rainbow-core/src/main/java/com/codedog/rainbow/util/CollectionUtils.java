/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

/**
 * Collection Utils
 *
 * @author https://github.com/gukt
 */
public final class CollectionUtils {

    /** Prevents to construct an instance. */
    private CollectionUtils() {
        throw new AssertionError("No CollectionUtils instances for you.");
    }

//    public static boolean isNullOrEmpty(@Nullable Map<?, ?> map) {
//        return ObjectUtils.isNullOrEmpty(map);
//    }

//    @SuppressWarnings("unchecked")
//    public static <K, V> Map<K, V> fromPairs(Object[]... pairs) {
//        Map<K, V> map = new HashMap<>();
//        for (Object[] pair : pairs) {
//            if (pairs.length != 2) {
//                // TODO 修改提示文本
//                throw new IllegalArgumentException("参数 pairs 的个数必须是偶数, got:" + pairs.length);
//            }
//            map.put((K) pair[0], (V) pair[1]);
//        }
//        return map;
//    }
//
//    public static Map<String, Object> fromProperties(Object obj, String... properties) {
//        Map<String, Object> map = new HashMap<>();
//        // TODO 读取指定的属性值
//        return map;
//    }

//    @SuppressWarnings("unchecked")
//    public static <E> Map<String, E> of(@Nullable Object... pairs) {
//        Objects.requireNonNull(pairs);
//        if (pairs.length % 2 != 0) {
//            throw new IllegalArgumentException("参数 pairs 的个数必须是偶数, got:" + pairs.length);
//        }
//
//        Object key, value;
//        Map<String, E> retMap = new HashMap<>();
//        for (int i = 0; i < pairs.length; i += 2) {
//            key = pairs[i];
//            value = pairs[i + 1];
//            String k = key instanceof String ? (String) key : key.toString();
//            retMap.put(k, (E) value);
//        }
//        return retMap;
//    }
}
