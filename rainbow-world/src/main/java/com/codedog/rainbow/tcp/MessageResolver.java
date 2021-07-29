/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;

import java.io.Serializable;

/**
 * MessageResolver class
 *
 * @author https://github.com/gukt
 */
public interface MessageResolver<T> {

    Object getRtd(T msg);

    T withRtd(T msg, Object rtd);

    default int getSync(T msg) { return 0;}

    void setSync(T msg, int sync);

    String getType(T msg);

    void setSn(T msg, int sn);

    void setAck(T msg, int ack);

    void setTime(T msg, long timestamp);

    /**
     * 返回字符串表示的消息对象，一般用于控制台或日志的打印输出。可以指定 <code>compact=true</code> 以输出精简的内容。
     * Deprecated ???
     *
     * @param msg     消息对象
     * @param compact 是否只输出精简的文本内容
     * @return 字符串表示的消息对象
     */
    @Deprecated
    default String toString(T msg, boolean compact) {
        return msg.toString();
    }

    T resolveError(Serializable code, String error);

    @Deprecated
    default Object getPayload(T msg) {
        throw new NotImplementedException();
    }

    default T serverUnavailableError() {
        throw new NotImplementedException();
    }

    default T serverBusyingError() {
        throw new NotImplementedException();
    }

    default T connectionExceededError() {
        throw new NotImplementedException();
    }

    default T sessionBacklogsExceededError() {
        throw new NotImplementedException();
    }

    default T handlerNotFoundError() {throw new NotImplementedException();}

    default T EXCEED_CONTINUOUS_BAD_REQUEST_THRESHOLD_ERROR() {throw new NotImplementedException();}

    default T ERR_ILLEGAL_SN() {throw new NotImplementedException();}

    Class<T> getMessageClass();
}
