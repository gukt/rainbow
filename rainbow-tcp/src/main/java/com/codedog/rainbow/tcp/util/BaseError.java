/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.tcp.message.MessageHandler;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.util.ObjectUtils;
import lombok.Getter;

import java.util.StringJoiner;

/**
 * 表示错误。更多的业务逻辑处理错误，请从该类继承。属性 {@link #code} 和 {@link #msg} 一旦定义了就不可变，如果需要改变，请调用 clone 方法并
 *
 * @author https://github.com/gukt
 * @apiNote
 */
public class BaseError implements MessageHandler.Error {

    public static final BaseError HANDLER_NOT_FOUND = BaseError.of(0, "没有找到相应的处理程序");
    public static final BaseError BAD_SEQUENCE_NUMBER = BaseError.of(1, "错误的序号");
    public static final BaseError BAD_REQUEST = BaseError.of(1, "错误的请求");
    public static final BaseError SERVER_BUSYING = BaseError.of(2, "服务器忙");
    public static final BaseError SERVER_INTERNAL_ERROR = BaseError.of(3, "服务器内部错误");
    public static final BaseError HANDLER_MULTI_RESULT = BaseError.of(3, "HANDLER_MULTI_RESULT");
    /**
     * 不能识别的返回值类型
     */
    public static final BaseError UNRECOGNIZED_HANDLE_RESULT = BaseError.of(3, "不能识别的返回值类型");
    public static final BaseError SERVER_UNAVAILABLE = BaseError.of(4, "服务当前不可用");
    public static final BaseError EXCEED_CONNECTIONS = BaseError.of(5, "超出最大连接数");
    public static final BaseError EXCEED_SESSION_BACKLOGS = BaseError.of(6, "EXCEED_SESSION_BACKLOGS");
    public static final BaseError EXCEED_BAD_REQUEST_THRESHOLD = BaseError.of(7, "超出错误请求阈值");

    /**
     * 错误代码
     */
    @Getter private final int code;
    /**
     * 错误描述
     */
    @Getter private final String msg;

    public BaseError msg(Object msg) {
        return msg(msg, false);
    }

    public BaseError msg(Object msg, boolean append) {
        return msg(msg, append, ": ");
    }

    public BaseError msg(Object msg, boolean append, String delimiter) {
        Assert.notNull(msg, "msg");
        delimiter = ObjectUtils.nullToDefault(delimiter, " ");
        if (append) {
            msg = this.msg + delimiter + msg;
        }
        return new BaseError(code, msg.toString());
    }

    private BaseError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BaseError of(int code, String msg) {
        return new BaseError(code, msg);
    }

    public static BaseError of(MessageHandler.Error error) {
        return of(error.getCode(), error.getMsg());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BaseError.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("msg='" + msg + "'")
                .toString();
    }
}
