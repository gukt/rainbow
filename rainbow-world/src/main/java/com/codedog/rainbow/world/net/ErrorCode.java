/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.world.net.json.JsonPacket;
import lombok.Getter;

/**
 * 业务层定义的“错误代码枚举类”
 *
 * @author https://github.com/gukt
 */
public enum ErrorCode {

    /**
     * 错误的请求
     */
    ERR_BAD_REQUEST(0, "错误的请求"),
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
    ERR_HANDLER_NOT_FOUND(1, "没有找到相应的MessageHandler"),
    /**
     * 消息处理过程中发生的未知异常
     */
    ERR_UNKNOWN_HANDLING_EXCEPTION(1, "未知消息处理异常"),
    /**
     * 服务器当前忙
     */
    ERR_SERVER_BUSYING(1, "服务器当前忙"),
    /**
     * 服务器正在关闭中
     */
    ERR_SERVER_TERMINATING(1, "服务器正在关闭中"),
    /**
     * 服务器当前不可用
     */
    ERR_SERVER_UNAVAILABLE(1, "服务器当前不可用"),
    ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT(1, "ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT"),
    ERR_SESSION_NOT_FOUND(1, "连接不存在"),
    // ERR_SESSION_EXPIRED(1, "重连过期"),
    ERR_ILLEGAL_SN(1, "不正确的包序"),

    // more
    ;

    /**
     * 错误代码
     */
    @Getter private final int code;

    /**
     * 错误详细描述
     */
    @Getter private final String error;

    ErrorCode(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public JsonPacket toJsonPaket() {
        return JsonPacket.ofError(code, error);
    }
}
