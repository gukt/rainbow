/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.message;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.MessageUtils;
import com.codedog.rainbow.util.Assert;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Assert.notNull(delegate, "delegate");
        Assert.notNull(methodAccess, "methodAccess");
        Assert.notNull(methodName, "methodName");

        this.delegate = delegate;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodIndex = methodAccess.getIndex(methodName);
    }

    /**
     * 创建一个 MessageHandlerAdapter 实例。
     *
     * @param delegate     Handler 方法所在的对象，不可为 null
     * @param methodAccess MethodAccess，不可为 null
     * @param methodName   方法名，不可为 null
     * @param messageType  可处理的协议类型（业务），不可为 null
     * @param <T>          Packet 类型
     * @return MessageHandlerAdapter 实例
     */
    public static <T> MessageHandler<T> of(Object delegate, MethodAccess methodAccess, String methodName, String messageType) {
        Assert.notNull(messageType, "messageType");
        return new MessageHandlerAdapter<T>(delegate, methodAccess, methodName) {
            @Override
            public Serializable getType() {
                return messageType;
            }
        };
    }

    /**
     * 处理指定的消息
     *
     * @param session 表示客户端的连接
     * @param message 待处理的消息
     * @throws MessageHandlerException MessageHandleException
     */
    @Override
    public final Object handle(Session session, T message) {
        Assert.notNull(session, "session");
        Assert.notNull(session, "message");

        Object[] args = resolveArgs(session, message);
        Object result = methodAccess.invoke(delegate, methodIndex, args);
        // 如果有错误直接返回错误，反之返回 result 值
        Optional<BindingResult> error = getError(args);
        if (error.isPresent()) {
            return error.get();
        }
        return result;
    }

    private Object[] resolveArgs(Session session, T packet) {
        Class<?>[] parameterTypes = methodAccess.getParameterTypes()[methodIndex];
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            if (Session.class.isAssignableFrom(paramType)) {
                args[i] = session;
            } else if (paramType.equals(BindingResult.class)) {
                args[i] = BindingResult.EMPTY;
            } else {
                try {
                    args[i] = MessageUtils.resolveArgs(packet, paramType);
                } catch (Exception e) {
                    log.warn("Cannot resolve argument value: #{}, parameter type: {}, method: {}", i, paramType, methodAccess);
                    args[i] = null;
                }
            }
        }
        return args;
    }

    private Optional<BindingResult> getError(Object[] args) {
        BindingResult result = null;
        if (args[args.length - 1] instanceof MessageHandler.BindingResult) {
            result = (BindingResult) args[args.length - 1];
        }
        if (result != null && !result.isEmpty()) {
            return Optional.of(result);
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
        String params = Arrays.stream(methodAccess.getParameterTypes()[0])
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
        return "MessageHandlerAdapter(" + delegate.getClass().getSimpleName() + "#" + methodName + "(" + params + "))";
    }
}
