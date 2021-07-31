/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core;

import java.util.Map;

/**
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused, unchecked")
@Deprecated
public interface AttributeSupport {

    Map<String, Object> getAttrsMap();

    default void putAttrs(Map<String, Object> attrs) {
        getAttrsMap().putAll(attrs);
    }

    default void putAttr(String key, Object value) {
        getAttrsMap().put(key, value);
    }

    default void putAttrIfAbsent(String key, Object value) {
        getAttrsMap().putIfAbsent(key, value);
    }

    default <V> V getAttr(String key) {
        return (V) getAttrsMap().get(key);
    }

    default <V> V getAttrOrDefault(String key, V defaultValue) {
        return (V) getAttrsMap().getOrDefault(key, defaultValue);
    }

    default boolean containsAttr(String key) {
        return getAttrsMap().containsKey(key);
    }
}

