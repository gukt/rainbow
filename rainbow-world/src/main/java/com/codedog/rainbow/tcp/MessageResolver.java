/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;
import com.codedog.rainbow.world.net.json.JsonPacket;

import java.io.Serializable;

/**
 * MessageResolver class
 *
 * @author https://github.com/gukt
 */
public interface MessageResolver<T> {

    Object getRtd(T msg);

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

    Class<T> getMessageClass();
}
