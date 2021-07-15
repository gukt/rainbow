/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-08-04 21:06
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class PayloadDataAccessException extends RuntimeException {

    public PayloadDataAccessException(String message) {
        super(message);
    }

    public PayloadDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
