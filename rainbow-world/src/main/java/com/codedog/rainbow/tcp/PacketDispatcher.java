/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;
import com.codedog.rainbow.world.config.TcpProperties;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.net.*;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.google.protobuf.ByteString;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.RejectedExecutionException;

import static com.codedog.rainbow.world.net.ErrorCodeEnum.ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT;

/**
 * TODO 考虑是否需要使用Packet对象池方式返回对象，避免大量构建JsonPacket对象的消耗
 * TODO 消息派发器能不依赖具体类型吗?
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class PacketDispatcher<T> extends AbstractMessageDispatcher<T> {

    private final PacketResolver<T> resolver;

    @SuppressWarnings("unchecked")
    public PacketDispatcher(TcpProperties properties, PacketResolver<T> resolver) {
        super(properties);
        Type superClass = this.getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        this.packetType = (Class<T>) type;
        this.resolver = resolver;
    }

    /**
     * 将指定session的请求提交到"业务处理线程池"中运行，
     * 如果业务线程池当前忙导致提交任务被拒绝，应降级处理
     *
     * @throws RejectedExecutionException 如果业务线程池当前已经处理不过来
     * @throws NullPointerException       如果msg或session为null
     */
    @Override
    protected final void doDispatch(@NonNull T request, @NonNull Session session) {
        if (session.isProcessing()) {
            log.error("TCP: 当前连接有请求正在处理，等待下次被调度执行: {} <- {}", request, session);
            return;
        } else {
            session.setProcessingRequest(request);
        }

        final Object rtd = resolver.getRtd(request);
        final MessageHandler<Object> handler = getHandlerByType(resolver.getType(request));
        if (handler == null) {
            JsonPacket.ofError(ErrorCodeEnum.ERR_MESSAGE_HANDLER_NOT_FOUND)
                    .withRtd(rtd)
                    .writeTo(session);
            return;
        }
        log.debug("TCP: Dispatching: {}", resolver.toCompactString(request));
        // 执行任务
        executor.execute(new RequestTask<T>(session, request) {
            @Override
            public void run() {
                // 记录开始处理请求的时间
                final long startTime = System.currentTimeMillis();
                T response;
                try {
                    // 执行请求
                    Object result = handler.handle(session, request);
                    // 处理返回结果，handle()方法可以以多种多样的方式返回处理结果：
                    // 1. 如果需要返回消息给客户端，一般直接返回JsonPacket对象
                    // 2. 如果不需要返回消息给客户端，返回null即可
                    // 3. 很多地方需要前置检查，此时可能会返回ErrorCodeEnum
                    // 4. 前置检查失败的另一中表现形式是抛出MessageHandleException
                    if (result == null) {
                        return;
                    } else if (result instanceof ErrorCodeEnum) {
                        // TODO 简化
                        response = resolver.ofError(((ErrorCodeEnum) result).getCode(), ((ErrorCodeEnum) result).getError());
//                   // TODO fix it ASAP
//                    } else if (result instanceof packetType) {
//                        response = (T) result;
                    } else {
                        // 对于返回了不支持的处理结果类型，此时仍需要返回响应给客户端，
                        // 不能默默的吞掉，否则可能会导致客户端因收不到响应而等待
                        response = resolver.ofError(ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT.getCode(),
                                ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT.getError());
                    }
                } catch (Exception e) {
                    // MessageHandleException中带有errorCode和errorMessage，需提取出来返回
                    if (e instanceof MessageHandleException) {
                        MessageHandleException e1 = (MessageHandleException) e;
                        response = resolver.ofError(e1.getErrorCode(), e1.getErrorMessage());
                    } else {
                        // 消息处理过程中一切未捕捉的异常
                        response = resolver.ofError(ErrorCodeEnum.ERR_UNKNOWN_MESSAGE_HANDLE_EXCEPTION, e.getMessage());
                    }
                }
                // 返回处理结果给客户端
                if (response != null) {
                    // 如果请求包含有ext字段（透传信息）则响应中也原封不动的返回
                    session.write(resolver.withRtd(request, rtd));
                }
                // 后置处理
                postHandle(session, request, startTime);
            }
        });

    }

    public interface PacketResolver<T> {

        Object getRtd(T packet);

        String getType(T packet);

        String toCompactString(T packet);

        T ofError(Serializable code, String error);

        T withRtd(T packet, Object rtd);

        default T serverUnavailable() {
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
    }

    public static class ProtobufPacketResolver implements PacketResolver<ProtoPacket> {

        public Object getRtd(ProtoPacket packet) {
            return packet.getRtd();
        }

        @Override
        public String getType(ProtoPacket packet) {
            // TODO 要不要用一个 Object 类型
            return packet.getType().name();
        }

        @Override
        public String toCompactString(ProtoPacket packet) {
            return String.format("ProtoPacket#%d-%s-%s", packet.getSn(), packet.getType(), packet.getPayload());
        }

        @Override
        public ProtoPacket ofError(Serializable code, String error) {
            return null;
        }

        @Override
        public ProtoPacket withRtd(ProtoPacket packet, Object rtd) {
            // TODO 需不需要 build?
            // TODO 这里 rtd 赚 ByteString 有问题
            packet = packet.toBuilder().setRtd((ByteString) rtd).build();
            return packet;
        }
    }

    public static class JsonPacketResolver implements PacketResolver<JsonPacket> {

        @Override
        public Object getRtd(JsonPacket packet) {
            return packet.getRtd();
        }

        @Override
        public String getType(JsonPacket packet) {
            return packet.getType();
        }

        @Override
        public String toCompactString(JsonPacket packet) {
            return String.format("JsonPacket#%d-%s-%s", packet.getSn(), packet.getType(), packet.getPayload());
        }

        @Override
        public JsonPacket ofError(Serializable code, String error) {
            return null;
        }

        @Override
        public JsonPacket withRtd(JsonPacket packet, Object rtd) {
            return packet.withRtd(rtd);
        }
    }
}
