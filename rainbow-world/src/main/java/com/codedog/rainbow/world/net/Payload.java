/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author https://github.com/gukt
 */
public class Payload extends HashMap<String, Object> {

//    /**
//     * 私有构造函数，目的是阻止使用new来构造对象实例
//     */
//    @SuppressWarnings("unused")
//    private Payload() {
//        throw new AssertionError("No Payload instances for you.");
//    }

    public Payload() {
        super(8);
    }

    public Payload(int capacity) {
        super(capacity);
    }

    /**
     * 拷贝指定的map中的映射到实例的内部数据中
     *
     * @throws NullPointerException 如果指定的map为null
     */
    public Payload(@NonNull Map<String, Object> map) {
        super(map);
    }

    public static Payload of(@NonNull String key, Object value) {
        return new Payload().put(key, value);
    }

    /**
     * An convenience method of new Payload()
     *
     * @return A payload object with empty content
     */
    public static Payload empty() {
        return new Payload();
    }

    public static Payload of(Object... kvs) {
        if (kvs == null) {
            throw new IllegalArgumentException("v should not be null.");
        }
        int even = 2;
        int len = kvs.length;
        if (len % even != 0) {
            throw new IllegalArgumentException("The length of kvs is not even");
        }
        Payload payload = new Payload();
        Object key, value;
        for (int i = 0; i < len; i += even) {
            key = kvs[i];
            value = kvs[i + 1];
            String k = key instanceof String ? (String) key : key.toString();
            payload.put(k, value);
        }
        return payload;
    }

    @Override
    public Payload put(@NonNull String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
     * key.
     */
    @SuppressWarnings("unchecked")
    public <V> V get(@NonNull String key) {
        return (V) super.get(key);
    }

    @SuppressWarnings("unchecked")
    public <V> V getOrDefault(@NonNull String key, @NonNull V defaultValue) {
        return (V) super.getOrDefault(key, defaultValue);
    }
}
