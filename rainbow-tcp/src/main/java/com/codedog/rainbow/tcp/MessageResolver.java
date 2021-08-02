/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;
import com.codedog.rainbow.tcp.util.BaseError;

/**
 * MessageResolver class
 *
 * @author https://github.com/gukt
 */
public interface MessageResolver<T> {

    Object getRtd(T message);

    T withRtd(T message, Object rtd);

    default int getSync(T message) { return 0;}

    void setSync(T message, int sync);

    Object getType(T message);

    void setSn(T message, int sn);

    void setAck(T message, int ack);

    void setTime(T message, long timestamp);

    /**
     * 返回字符串表示的消息对象，一般用于控制台或日志的打印输出。可以指定 <code>compact=true</code> 以输出精简的内容。
     * Deprecated ???
     *
     * @param message 消息对象
     * @param compact 是否只输出精简的文本内容
     * @return 字符串表示的消息对象
     */
    @Deprecated
    default String toString(T message, boolean compact) {
        return message.toString();
    }

    @Deprecated
    default Object getPayload(T message) {
        throw new NotImplementedException();
    }

    default T errorOf(BaseError error) {
        return errorOf(error.getCode(), error.getMsg());
    }

    T errorOf(int code, String msg);

    default T errorOf(MessageHandlerException e) {
        return errorOf(e.getErrorCode(), e.getErrorMessage());
    }

    // T wrap(Object obj);

    Class<T> getMessageClass();

    // enum ErrorType {
    //     HANDLER_NOT_FOUND,
    //     ILLEGAL_SEQUENCE_NUMBER,
    //     SERVER_BUSYING,
    //     SERVER_INTERNAL_ERROR,
    //     SERVER_UNAVAILABLE,
    //     EXCEED_CONNECTIONS,
    //     EXCEED_SESSION_BACKLOGS,
    //     EXCEED_BAD_REQUEST_THRESHOLD
    // }
}
