/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.world.config.TcpProperties;
import com.codedog.rainbow.world.net.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;

import static com.codedog.rainbow.world.net.ErrorCode.ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT;

/**
 * TODO 考虑是否需要使用Packet对象池方式返回对象，避免大量构建JsonPacket对象的消耗
 * TODO 消息派发器能不依赖具体类型吗?
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class DefaultMessageDispatcher<T> extends AbstractMessageDispatcher<T> {

    private final MessageResolver<T> resolver;

    public DefaultMessageDispatcher(TcpProperties properties, MessageResolver<T> resolver) {
        super(properties);
        this.resolver = properties.getMessageResolver();
        this.supportedMessageType = resolver.getMessageClass();
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
        final Object rtd = resolver.getRtd(request);
        final MessageHandler<Object> handler = getHandlerByType(resolver.getType(request));
        if (handler == null) {
            T error = resolver.handlerNotFoundError();
            session.write(resolver.withRtd(error, rtd));
            return;
        }
        // log.debug("TCP - Dispatching a message to handle: {}", resolver.toString(request, true));
        log.debug("TCP - Dispatching message: {}", request);
        // 执行任务Message pumping loop error
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
                    // 2. 如果不需要返回消息给客户端，返回 null 即可。
                    // 3. 很多地方需要前置检查，此时可能会返回 ErrorCodeEnum
                    // 4. 前置检查失败的另一中表现形式是抛出MessageHandleException
                    if (result == null) return;
                    if (result instanceof ErrorCode) {
                        // TODO 简化
                        response = resolver.resolveError(((ErrorCode) result).getCode(), ((ErrorCode) result).getError());
                    } else {
                        // 对于返回了不支持的处理结果类型，此时仍需要返回响应给客户端，
                        // 不能默默的吞掉，否则可能会导致客户端因收不到响应而等待
                        response = resolver.resolveError(ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT.getCode(),
                                ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT.getError());
                    }
                } catch (Exception e) {
                    // MessageHandleException 中带有errorCode和errorMessage，需提取出来返回
                    if (e instanceof MessageHandleException) {
                        MessageHandleException e1 = (MessageHandleException) e;
                        response = resolver.resolveError(e1.getErrorCode(), e1.getErrorMessage());
                    } else {
                        // 消息处理过程中一切未捕捉的异常
                        response = resolver.resolveError(ErrorCode.ERR_UNKNOWN_HANDLING_EXCEPTION, e.getMessage());
                    }
                }
                // 返回处理结果给客户端
                if (response != null) {
                    // 如果请求包含有ext字段（透传信息）则响应中也原封不动的返回
                    // TODO 判断是否有 rtd，如果没有就不要加了
                    session.write(resolver.withRtd(request, rtd));
                }
                // 后置处理
                postHandle(session, request, startTime);
            }
        });
    }
}
