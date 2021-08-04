/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.lang;

import com.codedog.rainbow.util.ObjectUtils;

import java.util.Arrays;

/**
 * ObjectCastException class
 *
 * @author https://github.com/gukt
 */
public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException(Object obj, Class<?> targetType) {
        this(obj, targetType, null);
    }

    public TypeMismatchException(Object obj, Class<?>... targetTypes) {
        this(obj, targetTypes, null);
    }

    public TypeMismatchException(Object obj, Class<?>[] targetTypes, Throwable cause) {
        super("Type mismatch: " + ObjectUtils.requireNonNull(obj, "name").getClass()
                + " (expected: " + Arrays.toString(ObjectUtils.requireNonEmpty(targetTypes, "targetTypes.length > 0")) + ")", cause);
    }
}
