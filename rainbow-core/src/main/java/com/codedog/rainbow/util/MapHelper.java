/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

import static com.codedog.rainbow.util.ObjectUtils.nullToDefault;

/**
 * MapHelper
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
@Slf4j
public final class MapHelper {

    private static final Consumer<RuntimeException> DEFAULT_TYPE_MISMATCH_HANDLER = e -> log.warn(e.getMessage());
    @Setter private static boolean throwsOnTypeMismatch = true;
    @Setter private static Consumer<RuntimeException> typeMismatchExceptionHandler = DEFAULT_TYPE_MISMATCH_HANDLER;

    private final Map<?, ?> map;

    private MapHelper(Map<?, ?> map) {
        Assert.notNull(map, "map");
        this.map = map;
    }

    public static MapHelper of(final Map<?, ?> map) {
        return new MapHelper(map);
    }

    @Nullable
    public Integer getInt(Object key) {
        return MapHelper.getInt(map, key);
    }

    public Integer getIntOrDefault(Object key, Integer defaultValue) {
        return MapHelper.getIntOrDefault(map, key, defaultValue);
    }

    @Nullable
    public Long getLong(Object key) {
        return MapHelper.getLong(map, key);
    }

    public Long getLongOrDefault(Object key, Long defaultValue) {
        return MapHelper.getLongOrDefault(map, key, defaultValue);
    }

    @Nullable
    public Boolean getBoolean(Object key) {
        return MapHelper.getBoolean(map, key);
    }

    public Boolean getBooleanOrDefault(Object key, Boolean defaultValue) {
        return MapHelper.getBooleanOrDefault(map, key, defaultValue);
    }

    @Nullable
    public String getString(Object key) {
        return MapHelper.getString(map, key);
    }

    public String getStringOrDefault(Object key, String defaultValue) {
        return MapHelper.getStringOrDefault(map, key, defaultValue);
    }

    // Static methods

    @Nullable
    public static Integer getInt(Map<?, ?> map, Object key) {
        Assert.notNull(key, "map");
        Assert.notNull(key, "key");
        Number value = checkTypeAndGet(map.get(key), Number.class);
        return value != null ? value.intValue() : null;
    }

    public static Integer getIntOrDefault(Map<?, ?> map, Object key, Integer defaultValue) {
        Integer value = getInt(map, key);
        return nullToDefault(value, defaultValue);
    }

    @Nullable
    public static Long getLong(Map<?, ?> map, Object key) {
        Assert.notNull(key, "map");
        Assert.notNull(key, "key");
        Number value = checkTypeAndGet(map.get(key), Number.class);
        return value != null ? value.longValue() : null;
    }

    public static Long getLongOrDefault(Map<?, ?> map, Object key, Long defaultValue) {
        Long value = getLong(map, key);
        return nullToDefault(value, defaultValue);
    }

    @Nullable
    public static Double getDouble(Map<?, ?> map, Object key) {
        Assert.notNull(key, "map");
        Assert.notNull(key, "key");
        Number value = checkTypeAndGet(map.get(key), Number.class);
        return value != null ? value.doubleValue() : null;
    }

    public static Double getDoubleOrDefault(Map<?, ?> map, Object key, Double defaultValue) {
        Double value = getDouble(map, key);
        return nullToDefault(value, defaultValue);
    }

    @Nullable
    public static Boolean getBoolean(Map<?, ?> map, Object key) {
        Assert.notNull(key, "map");
        Assert.notNull(key, "key");
        return checkTypeAndGet(map.get(key), Boolean.class);
    }

    public static Boolean getBooleanOrDefault(Map<?, ?> map, Object key, Boolean defaultValue) {
        Boolean value = getBoolean(map, key);
        return nullToDefault(value, defaultValue);
    }

    @Nullable
    public static String getString(Map<?, ?> map, Object key) {
        Assert.notNull(key, "key");
        Object value = map.get(key);
        return value instanceof String ? (String) value : String.valueOf(value);
    }

    public static String getStringOrDefault(Map<?, ?> map, Object key, String defaultValue) {
        String value = getString(map, key);
        return nullToDefault(value, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T> T checkTypeAndGet(Object value, Class<T> expectedType) {
        if (value == null) return null;
        Assert.notNull(expectedType, "expectedType");
        if (!expectedType.isAssignableFrom(value.getClass())) {
            RuntimeException e = new TypeMismatchException("Actual: " + value.getClass() + " (expected: " + expectedType + "), value: " + value);
            if (throwsOnTypeMismatch) {
                throw e;
            } else {
                if (typeMismatchExceptionHandler != null) {
                    typeMismatchExceptionHandler.accept(e);
                    return null;
                }
            }
        }
        return (T) value;
    }

    static class TypeMismatchException extends RuntimeException {

        public TypeMismatchException(String message) {
            super(message);
        }
    }
}
