/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.support;

/**
 * @author https://github.com/gukt
 */
public class LifecycleException extends RuntimeException {

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
}
