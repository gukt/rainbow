/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.common;

import com.codedog.rainbow.core.rest.ApiResult;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * BodyData class
 *
 * @author https://github.com/gukt
 */
public class BodyData extends HashMap<String , Object> {

    // TODO 添加 Nullable 注释
    @SuppressWarnings("unchecked")
    public <V> V get(String key) {
        return (V)super.get(key);
    }

    public <V> Optional<V> find(String key) {
        return Optional.ofNullable(get(key));
    }

//    public <V> V getRequired(String key) {
//        V value = get0(key);
//        if(value == null) {
//            throw new IllegalArgumentException("No value for key: " + key);
//        }
//        return value;
//    }

//        public <V> Function<String, V> get(String key) {
//            return null;
//        }

//        @SuppressWarnings("unchecked")
//        public <V> Optional<V> get(String key) {
//            Object value = super.get(key);
//            return Optional.ofNullable((V) value);
//        }

    public <V> V getOrThrow(Object key, Supplier<RuntimeException> supplier) {
        return getOrThrow(key, supplier.get());
    }

    public <V> V getOrThrow(Object key, ApiResult result) {
        return getOrThrow(key, result.toException());
    }

    @SuppressWarnings("unchecked")
    public <V> V getOrThrow(Object key, RuntimeException throwable) {
        V value = (V) get(key);
        if (value == null) {
            throw throwable;
        }
        return value;
    }
}
