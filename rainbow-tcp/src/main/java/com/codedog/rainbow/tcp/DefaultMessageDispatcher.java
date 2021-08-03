/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.MessageHandler.Error;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.BaseError;
import com.codedog.rainbow.tcp.util.MessageUtils;
import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.util.Assert;
import com.google.protobuf.MessageLiteOrBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * TODO 考虑是否需要使用Packet对象池方式返回对象，避免大量构建JsonPacket对象的消耗
 * TODO 消息派发器能不依赖具体类型吗?
 * TODO 还需要泛型吗？？？、？？？？？？？？？
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class DefaultMessageDispatcher<T> extends AbstractMessageDispatcher<T> {

    public DefaultMessageDispatcher(TcpProperties properties) {
        super(properties);
    }

    /**
     * 将收到的指定 {@link Session 连接} 发送的请求，提交到“业务处理线程池”中运行。
     * <p>如果业务线程池当前忙导致提交任务被拒绝，应降级处理。
     *
     * @throws RejectedExecutionException 如果提交到线程池被拒绝，此时往往意味着处理该消息的线程池处于超载状态
     */
    @Override
    protected final void dispatch0(Session session, T request) {
        Assert.notNull(session, "session");
        Assert.notNull(request, "request");

        if (session.isProcessing()) {
            log.error("TCP - 当前连接有请求正在处理，等待下次被调度执行: {} <- {}", request, session);
            return;
        } else {
            session.setProcessingRequest(request);
        }
        final MessageHandler<Object> handler = getHandlerByType(MessageUtils.getType(request));
        if (handler == null) {
            Object error = MessageUtils.errorOf(BaseError.HANDLER_NOT_FOUND);
            session.write(error);
            return;
        }
        // log.debug("TCP - Dispatching a message to handle: {}", resolver.toString(request, true));
        log.debug("TCP - Dispatching: {}", request);
        executor.execute(new RequestTask<T>(session, request) {
            @Override
            public void run() {
                // 记录开始处理请求的时间
                final long startTime = System.currentTimeMillis();
                Object response;
                try {
                    Object result = handler.handle(session, request);
                    if (result == null) {
                        response = null;
                    } else if (request.getClass().isAssignableFrom(result.getClass())) {
                        // 如果是和请求对象类型相同直接返回
                        response = result;
                    } else if (MessageLiteOrBuilder.class.isAssignableFrom(result.getClass())) {
                        // NOTE：只有在使用 Protobuf 协议时，才支持返回值不是完整的 ProtoPacket 对象，
                        response = ProtoUtils.wrap(result);
                    } else if (result instanceof BaseError) {
                        response = result;
                    } else if (result instanceof MessageHandlerException) {
                        response = MessageUtils.errorOf((MessageHandlerException) result);
                    } else if (result instanceof MessageHandler.Error) {
                        List<Object> errors = ((Error) result).getErrors();
                        response = MessageUtils.errorOf(BaseError.HANDLER_MULTI_RESULT.getCode(), errors.toString());
                    } else {
                        // 其他不支持的返回类型
                        response = MessageUtils.errorOf(BaseError.UNKNOWN_HANDLER_RESULT);
                    }
                } catch (Exception e) {
                    if (e instanceof MessageHandlerException) {
                        response = MessageUtils.errorOf((MessageHandlerException) e);
                    } else {
                        response = MessageUtils.errorOf(BaseError.SERVER_INTERNAL_ERROR);
                    }
                }
                // 返回处理结果给客户端
                if (response != null) {
                    session.write(response);
                }
                // 后置处理
                postHandle(session, request, startTime);
            }
        });
    }
}
