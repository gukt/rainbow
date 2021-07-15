/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net;

import lombok.Getter;

/**
 * 错误代码枚举
 *
 * @author https://github.com/gukt
 */
public enum ErrorCodeEnum {
    /**
     * Bad Request
     */
    BAD_REQUEST(0, "BAD_REQUEST"),
    /**
     * 超出最大连接数
     */
    ERR_EXCEED_MAX_CONNECTION(0, "超出最大连接数"),
    /**
     * 超出最大连接数
     */
    ERR_EXCEED_CONTINUOUS_BAD_REQUEST_THRESHOLD(0, "连续接收到无效包"),
    /**
     * 超出连接最大等待处理请求数
     */
    ERR_EXCEED_SESSION_MAX_BACKLOGS(1, "ERR_EXCEED_SESSION_MAX_BACKLOGS"),
    /**
     * 没有找到相应的MessageHandler
     */
    ERR_MESSAGE_HANDLER_NOT_FOUND(1, "没有找到相应的MessageHandler"),
    /**
     * 消息处理过程中发生的未知异常
     */
    ERR_UNKNOWN_MESSAGE_HANDLE_EXCEPTION(1, "未知消息处理异常"),
    /**
     * 服务器当前忙
     */
    ERR_SERVER_IS_BUSYING(1, "服务器当前忙"),
    /**
     * 服务器正在关闭中
     */
    ERR_SERVER_IS_SHUTTING_DOWN(1, "服务器正在关闭中"),
    /**
     * 服务器当前不可用
     */
    ERR_SERVER_UNAVAILABLE(1, "服务器当前不可用"),

    ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT(1, "ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT"),

    ERR_RECONNECT_EXPIRED(1, "重连过期"),
    ERR_UNEXCEPTED_PACKET_SEQ(1, "不正确的包序"),

    // more
    ;

    @Getter
    private final int value;
    @Getter
    private final String text;

    ErrorCodeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
