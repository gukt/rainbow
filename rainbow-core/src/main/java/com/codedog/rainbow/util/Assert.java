/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

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

    // isTrue

    public static void isTrue(boolean expected) {
        if (!expected) {
            throw new IllegalArgumentException();
        }
    }

    public static void isTrue(boolean expected, String message) {
        if (!expected) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expected, RuntimeException cause) {
        if (!expected) {
            throw cause;
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

    private static <V> V nullSafeGet(Supplier<V> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }

    // Internal helper methods
}
