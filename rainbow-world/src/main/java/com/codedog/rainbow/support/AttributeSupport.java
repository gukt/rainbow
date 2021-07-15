/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.support;

import java.util.Map;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
@SuppressWarnings("unused, unchecked")
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

