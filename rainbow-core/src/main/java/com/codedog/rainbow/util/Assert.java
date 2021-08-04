/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Assert class
 *
 * @author https://github.com/gukt
 */
public final class Assert {

    /** Prevents to construct an instance. */
    private Assert() {
        throw new AssertionError("No Assert instances for you!");
    }

    // state
    public static void state(boolean expected) {

    }

    public static void notNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + ": null (expected: non-null)");
        }
    }

    public static void notNull(Object obj, Supplier<String> supplier) {
        if (obj == null) {
            throw new IllegalArgumentException(nullSafeGet(supplier));
        }
    }

    // isTrue

    public static void isTrue(boolean expected) {
        if (!expected) {
            throw new IllegalArgumentException();
        }
    }

    public static void isTrue(boolean expected, String name) {
        if (!expected) {
            throw new IllegalArgumentException(name + ": false (expected: true)");
        }
    }

    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    public static void isTrue(boolean expected, String format, Object... args) {
        if (!expected) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    private static <V> V nullSafeGet(Supplier<V> supplier) {
        return supplier != null ? supplier.get() : null;
    }

    private static <V> V nullSafeGet(Map<?, V> map, Object key) {
        return map != null ? map.get(key) : null;
    }

    // Internal helper methods
}
