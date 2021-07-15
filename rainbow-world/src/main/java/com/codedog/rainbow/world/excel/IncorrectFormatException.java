/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-08-01 23:11
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class IncorrectFormatException extends RuntimeException {

    public IncorrectFormatException(String message) {
        super(message);
    }

    public IncorrectFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
