/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import lombok.Getter;

/**
 * BaseError class
 *
 * @author https://github.com/gukt
 */
public class BaseError {

    public static final BaseError HANDLER_NOT_FOUND = BaseError.of(0, "没有找到相应的处理程序");
    public static final BaseError BAD_SEQUENCE_NUMBER = BaseError.of(1, "错误的序号");
    public static final BaseError BAD_REQUEST = BaseError.of(1, "错误的请求");
    public static final BaseError SERVER_BUSYING = BaseError.of(2, "服务器忙");
    public static final BaseError SERVER_INTERNAL_ERROR = BaseError.of(3, "服务器内部错误");
    public static final BaseError HANDLER_MULTI_RESULT = BaseError.of(3, "HANDLER_MULTI_RESULT");
    public static final BaseError UNKNOWN_HANDLER_RESULT = BaseError.of(3, "未知的处理结果");
    public static final BaseError SERVER_UNAVAILABLE = BaseError.of(4, "服务当前不可用");
    public static final BaseError EXCEED_CONNECTIONS = BaseError.of(5, "超出最大连接数");
    public static final BaseError EXCEED_SESSION_BACKLOGS = BaseError.of(6, "EXCEED_SESSION_BACKLOGS");
    public static final BaseError EXCEED_BAD_REQUEST_THRESHOLD = BaseError.of(7, "超出错误请求阈值");

    @Getter private int code;
    @Getter private String msg;

    private BaseError() {}

    public static BaseError of(int code, String msg) {
        BaseError instance = new BaseError();
        instance.code = code;
        instance.msg = msg;
        return instance;
    }
}
