/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.support;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-30 00:36
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class LifecycleException extends RuntimeException {

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }
}
