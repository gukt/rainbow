/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * SessionAttributes class
 *
 * @author https://github.com/gukt
 */
public class AttributeSupport implements AttributeAware {

    private final static String ROLE = "role";
    private final static String ROLE_ID = "role_id";

    private final Map<Object, Object> attrs = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(Object key) {
        return (V) attrs.get(key);
    }

    @Override
    public <V> V getOrDefault(Object key, V defaultValue) {
        return ObjectUtils.nullToDefault(get(key), defaultValue);
    }

    // TODO ifPresent, ifAbsent
    // TODO has(Object key)
    // TODO putIfAbsent

    @Override
    public AttributeSupport put(Object key, Object value) {
        Assert.notNull(key, "key");
        attrs.put(key, value);
        return this;
    }
}
