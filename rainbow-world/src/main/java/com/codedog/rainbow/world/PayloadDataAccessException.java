/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

/**
 * @author https://github.com/gukt
 */
public class PayloadDataAccessException extends RuntimeException {

    public PayloadDataAccessException(String message) {
        super(message);
    }

    public PayloadDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
