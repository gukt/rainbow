/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
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
