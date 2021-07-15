/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Objects.equal;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class MessageHandlerAdapter<T> implements MessageHandler<T> {

    private final Object delegate;
    private final MethodAccess methodAccess;
    private final String methodName;
    private final int methodIndex;

    protected MessageHandlerAdapter(Object delegate, MethodAccess methodAccess, String methodName) {
        this.delegate = delegate;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodIndex = methodAccess.getIndex(methodName);
    }

    /**
     * 处理指定的消息
     *
     * @param session session
     * @param message message
     * @throws MessageHandleException MessageHandleException
     */
    @Override
    public final Object handle(Session session, T message) {
        return methodAccess.invoke(delegate, methodIndex, session, message);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageHandlerAdapter<?> that = (MessageHandlerAdapter<?>) o;
        return equal(delegate, that.delegate) && equal(methodName, that.methodName);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(delegate, methodName);
    }

    @Override
    public final String toString() {
        return delegate.getClass().getSimpleName() + "#" + methodName;
    }
}
