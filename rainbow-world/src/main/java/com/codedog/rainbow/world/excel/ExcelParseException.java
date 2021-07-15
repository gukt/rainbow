/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

/**
 * @author https://github.com/gukt
 */
public class ExcelParseException extends RuntimeException {

    public ExcelParseException(String message) {
        super(message);
    }

    public ExcelParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
