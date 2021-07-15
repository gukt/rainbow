/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-08-01 01:06
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class ExcelParseException extends RuntimeException {

    public ExcelParseException(String message) {
        super(message);
    }

    public ExcelParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
