/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.lang;

import static java.util.Objects.requireNonNull;

/**
 * ObjectCastException class
 *
 * @author https://github.com/gukt
 */
public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException(Object obj, Class<?> targetType) {
        this(obj, targetType, null);
    }

    public TypeMismatchException(Object obj, Class<?> targetType, Throwable cause) {
        super("Type mismatch: " + requireNonNull(obj).getClass() + " (expected: " + requireNonNull(targetType) + ")", cause);
    }

}
