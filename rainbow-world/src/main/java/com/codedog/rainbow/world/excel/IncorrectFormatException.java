/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

/**
 * @author https://github.com/gukt
 */
public class IncorrectFormatException extends RuntimeException {

    public IncorrectFormatException(String message) {
        super(message);
    }

    public IncorrectFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
