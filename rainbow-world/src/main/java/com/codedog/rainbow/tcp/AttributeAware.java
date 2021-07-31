/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

/**
 * AttributeAware class
 *
 * @author https://github.com/gukt
 */
public interface AttributeAware {

    <V> V get(Object key);

    <V> V getOrDefault(Object key, V defaultValue);

    AttributeAware put(Object key, Object value);
}
