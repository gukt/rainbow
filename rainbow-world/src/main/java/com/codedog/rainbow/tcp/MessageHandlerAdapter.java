/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.MessageUtils;
import com.codedog.rainbow.util.Assert;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.google.common.base.Objects.equal;
import static java.util.Objects.requireNonNull;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class MessageHandlerAdapter<T> implements MessageHandler<T> {

    private final Object delegate;
    private final MethodAccess methodAccess;
    private final String methodName;
    private final int methodIndex;
    private MessageResolver<T> messageResolver;

    protected MessageHandlerAdapter(Object delegate, MethodAccess methodAccess, String methodName) {
        Assert.notNull(delegate, "delegate");
        Assert.notNull(methodAccess, "methodAccess");
        Assert.notNull(methodName, "methodName");
        this.delegate = delegate;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodIndex = methodAccess.getIndex(methodName);
    }

    public void setMessageResolver(MessageResolver<T> messageResolver) {
        this.messageResolver = messageResolver;
    }

    /**
     * 处理指定的消息
     *
     * @param session 表示客户端的连接
     * @param message 待处理的消息
     * @throws MessageHandleException MessageHandleException
     */
    @Override
    public final Object handle(Session session, T message) {
//        return methodAccess.invoke(delegate, methodIndex, session, message);
        requireNonNull(session, "session: null (expected: not null)");
        requireNonNull(message, "message: null (expected: not null)");
        Object[] args = resolveArgs(session, message);
        Object result = methodAccess.invoke(delegate, methodIndex, args);
        // 如果有错误直接返回错误，反之返回 result 值
        Optional<MessageHandler.Error> error = getError(args);
        if (error.isPresent()) {
            return error.get();
        } else {
            return result;
        }
    }

    private Object[] resolveArgs(Session session, T packet) {
        Class<?>[] parameterTypes = methodAccess.getParameterTypes()[methodIndex];
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            if (Session.class.isAssignableFrom(paramType)) {
                args[i] = session;
            } else if (paramType.equals(Error.class)) {
                args[i] = Error.of();
            } else {
                args[i] = MessageUtils.resolveArgs(packet, paramType);
            }
        }
        return args;
    }

    private Optional<MessageHandler.Error> getError(Object[] args) {
        MessageHandler.Error error = null;
        if (args[args.length - 1] instanceof MessageHandler.Error) {
            error = (MessageHandler.Error) args[args.length - 1];
        }
        if (error != null && !error.isEmpty()) {
            return Optional.of(error);
        } else {
            return Optional.empty();
        }
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
