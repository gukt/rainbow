/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;

import java.io.Serializable;
import java.util.Optional;

/**
 * MessageResolver class
 *
 * @author https://github.com/gukt
 */
public interface MessageResolver<T> {

    Object getRtd(T msg);

    default int getSync(T msg) { return 0;}

    String getType(T msg);

    String toCompactString(T msg);

    T resolveError(Serializable code, String error);

    T withRtd(T msg, Object rtd);

    default  Object getPayload(T msg) {
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

    <V> Optional<V> resolveArgs(T msg, Class<?> paramType);

    Class<T> getMessageClass();

    void setSn(T msg, int sn);

    void setAck(T msg, int ack);

    void setTime(T msg, long timestamp);

    void setSync(T msg, int sync);
}
