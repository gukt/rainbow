/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 消息处理器。
 *
 * @param <T> 可以处理的 *Packet 类型，如：{@link ProtoPacket ProtoPacket} 或 {@link JsonPacket JsonPacket}（注意：不是业务协议的类型）
 * @author https://github.com/gukt
 */
public interface MessageHandler<T> {

    /**
     * 可处理消息的类型
     *
     * @return 返回消息类型
     */
    default Serializable getType() {
        throw new NotImplementedException();
    }

    /**
     * 处理 {@link Session 指定连接} 发来的请求。方法的返回值支持以下多种场景：
     * <p>1. 如果处理完成后，没有消息需要返回给客户端，则返回 null。
     * <p>2. 如果需要返回错误，可返回 {@link com.codedog.rainbow.tcp.util.BaseError} 对象。
     * <p>3. 如果需要返回错误，也可以抛出 {@link MessageHandlerException} 异常。
     * <p>4. 如果一次有多个错误需要返回，可以新增 {@link MessageHandler.Error} 参数，将多个错误填充进该参数。
     * <p>5. 可以返回完整的 *Packet 对象（比如：{@link ProtoPacket ProtoPacket}、{@link JsonPacket JsonPacket}），
     * 也可以只返回它们的 {@link JsonPacket#getPayload() Payload} 字段值。
     *
     * @param session Session 对象，不能为 null
     * @param request 请求消息，不能为 null
     * @return 处理的结果，返回值可以很灵活，详见上述说明。
     * @throws MessageHandlerException 消息处理异常
     */
    @Nullable
    default Object handle(Session session, T request) {
        throw new NotImplementedException();
    }

    class Error {

        public final static Error EMPTY = new Error();

        private final List<Object> errors = new ArrayList<>();

        public List<Object> getErrors() {
            return errors;
        }

        /** Prevents to construct an instance. */
        private Error() {}

        // public static Error of() {
        //     return new Error();
        // }

        public static Error of(Object... errors) {
            Error instance = new Error();
            if (errors != null && errors.length > 0) {
                instance.errors.addAll(Arrays.asList(errors));
            }
            return instance;
        }

        public void add(Object error) {
            this.errors.add(error);
        }

        public boolean isEmpty() {
            return errors.isEmpty();
        }
    }
}
