/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.message;

import lombok.Getter;

import java.util.Objects;

/**
 * 消息处理异常
 *
 * @author https://github.com/gukt
 */
public final class MessageHandlerException extends RuntimeException implements MessageHandler.Error {

    /**
     * 错误代码
     */
    @Getter private final int code;
    /**
     * 错误描述
     */
    @Getter private final String msg;

    public MessageHandlerException(int code) {
        this.code = code;
        this.msg = null;
    }

    public MessageHandlerException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MessageHandlerException(int code, Throwable cause) {
        super(cause);
        this.code = code;
        this.msg = Objects.requireNonNull(cause, "cause should not be null.").getMessage();
    }

    public static MessageHandlerException of(MessageHandler.Error error) {
        return new MessageHandlerException(error.getCode(), error.getMsg());
    }
}
