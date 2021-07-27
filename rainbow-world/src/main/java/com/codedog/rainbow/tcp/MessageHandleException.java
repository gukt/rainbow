/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import lombok.Getter;
import lombok.NonNull;

/**
 * 消息处理异常
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
public final class MessageHandleException extends RuntimeException {

    @Getter
    private final int errorCode;
    @Getter
    private final String errorMessage;

    public MessageHandleException(int errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = null;
    }

    public MessageHandleException(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public MessageHandleException(int errorCode, @NonNull Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = cause.getMessage();
    }
}
