/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.util.MapUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author https://github.com/gukt
 */
public final class Payload extends HashMap<String, Object> {

    public static final Payload EMPTY = new Payload();

    private Payload() {
        super(8);
    }

    private Payload(int capacity) {
        super(capacity);
    }

    private Payload(Map<String, ?> map) {
        super(map);
    }

    /**
     * 创建一个 Payload 实例，并指定初始 <code>capacity</code>。
     *
     * @param capacity 初始容量
     * @return Payload 实例
     */
    public static Payload of(int capacity) {
        return new Payload(capacity);
    }

    /**
     * 创建一个 Payload 实例，并使用指定的 {@link Map map} 初始化数据。
     *
     * @param map map 对象
     * @return Payload 实例
     * @throws IllegalArgumentException 如果指定的 <code>map</code> 为 null
     */
    public static Payload of(final Map<String, ?> map) {
        Assert.notNull(map, "map");
        return new Payload(map);
    }

    /**
     * 使用指定的 key, value 创建一个 Payload 实例。
     *
     * @param key   键，不可为 null
     * @param value 值，可以为 null
     * @return Payload 实例
     * @throws IllegalArgumentException 如果指定的 <code>key</code> 为 null
     */
    public static Payload of(String key, Object value) {
        Assert.notNull(key, "key");
        return new Payload(8).put(key, value);
    }

    /**
     * 使用指定的 key, value 创建一个 Payload 实例。
     *
     * @param k1 键，不可为 null
     * @param v1 值，可以为 null
     * @param k2 键，不可为 null
     * @param v2 值，可以为 null
     * @return Payload 实例
     * @throws IllegalArgumentException 如果指定的 <code>key</code> 为 null
     */
    public static Payload of(String k1, Object v1, String k2, Object v2) {
        Assert.notNull(k1, "k1");
        Assert.notNull(k2, "k2");
        return Payload.of(new Object[]{
                new Object[]{k1, v1},
                new Object[]{k2, v2}
        });
    }

    /**
     * 使用指定的键值对数组创建一个 Payload 实例。
     *
     * @param pairs 键值对数组，可以为 null，为空时相当于 {@link #EMPTY Payload.EMPTY}
     * @return Payload 实例
     */
    public static Payload of(Object[] pairs) {
        return Payload.of(MapUtils.newHashMap(pairs));
    }

    /**
     * 向当前对象中存放一组 key，value 值。
     *
     * @param key   键，不能为 null
     * @param value 值，可以为 null
     * @return 返回当前对象自身
     * @throws IllegalArgumentException 如果 key 为 null
     */
    @Override
    public Payload put(String key, Object value) {
        Assert.notNull(key, "key");
        super.put(key, value);
        return this;
    }

    /**
     * 返回与指定的 key 对应的 value，如果 key 不存在，返回 null。
     *
     * @param key 键，不能为 null
     * @param <V> 返回值类型，内部会自动类型转换
     * @return 返回与指定的 key 对应的 value，如果 key 不存在，返回 null。
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <V> V get(String key) {
        Assert.notNull(key, "key");
        return (V) super.get(key);
    }
}
