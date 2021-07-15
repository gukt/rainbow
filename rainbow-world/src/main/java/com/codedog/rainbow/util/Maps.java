package com.codedog.rainbow.util;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Maps class
 *
 * @author https://github.com/gukt
 */
public final class Maps {

    /** Prevents to construct an instance. */
    private Maps() {
    }

    public static boolean isNullOrEmpty(@Nullable Map<?, ?> map) {
        return MoreObjects.isNullOrEmpty(map);
    }

    //  public static Map<String, Object> of(@Nullable Object... pairs) {
    //    Objects.requireNonNull(pairs);
    //
    //    if (pairs.length % 2 != 0) {
    //      throw new IllegalArgumentException("参数 pairs 的个数必须是偶数, got:" + pairs.length);
    //    }
    //
    //    Object key, value;
    //    Map<String, Object> retMap = new HashMap<>();
    //    for (int i = 0; i < pairs.length; i += 2) {
    //      key = pairs[i];
    //      value = pairs[i + 1];
    //      String k = key instanceof String ? (String) key : key.toString();
    //      retMap.put(k, value);
    //    }
    //    return retMap;
    //  }

    @SuppressWarnings("unchecked")
    public static <E> Map<String, E> of(@Nullable Object... pairs) {
        Objects.requireNonNull(pairs);
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("参数 pairs 的个数必须是偶数, got:" + pairs.length);
        }

        Object key, value;
        Map<String, E> retMap = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            key = pairs[i];
            value = pairs[i + 1];
            String k = key instanceof String ? (String) key : key.toString();
            retMap.put(k, (E) value);
        }
        return retMap;
    }

    // TODO 定时清理 Map，指定 key, value, ttl, map 指定全局 ttl， key 指定局部 ttl，
}
