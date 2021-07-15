/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.json;

import com.codedog.rainbow.world.GameOptions;
import com.codedog.rainbow.world.net.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TODO 考虑是否需要使用Packet对象池方式返回对象，避免大量构建JsonPacket对象的消耗
 * TODO 消息派发器能不依赖具体类型吗?
 *
 * @author https://github.com/gukt
 */
@Slf4j
@Component
public final class JsonPacketDispatcher extends AbstractMessageDispatcher<JsonPacket> {

    public JsonPacketDispatcher(GameOptions opts) {
        super(opts);
    }

    /**
     * 将指定session的请求提交到"业务处理线程池"中运行，
     * 如果业务线程池当前忙导致提交任务被拒绝，应降级处理
     *
     * @throws java.util.concurrent.RejectedExecutionException 如果业务线程池当前已经处理不过来
     * @throws NullPointerException                            如果msg或session为null
     */
    @Override
    protected final void doDispatch(@NonNull JsonPacket request, @NonNull Session session) {
        if (session.isProcessing()) {
            log.error("TCP: 当前连接有请求正在处理，等待下次被调度执行: {} <- {}", request, session);
            return;
        } else {
            session.setProcessingRequest(request);
        }

        final String ext = request.getExt();
        final MessageHandler<Object> handler = getHandlerByType(request.getType());
        if (handler == null) {
            JsonPacket.ofError(ErrorCodeEnum.ERR_MESSAGE_HANDLER_NOT_FOUND).withExt(ext).writeTo(session);
            return;
        }

        log.debug("TCP: Dispatching: {}", request.toCompatString());
        // 执行任务
        bizExec.execute(new RequestTask<JsonPacket>(session, request) {
            @Override
            public void run() {
                // 记录开始处理请求的时间
                final long startTime = System.currentTimeMillis();
                JsonPacket response;
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
                        response = JsonPacket.ofError((ErrorCodeEnum) result);
                    } else if (result instanceof JsonPacket) {
                        response = (JsonPacket) result;
                    } else {
                        // 对于返回了不支持的处理结果类型，此时仍需要返回响应给客户端，
                        // 不能默默的吞掉，否则可能会导致客户端因收不到响应而等待
                        response = JsonPacket.ofError(ErrorCodeEnum.ERR_UNSUPPORTED_MESSAGE_HANDLE_RESULT);
                    }
                } catch (Exception e) {
                    // MessageHandleException中带有errorCode和errorMessage，需提取出来返回
                    if (e instanceof MessageHandleException) {
                        MessageHandleException e1 = (MessageHandleException) e;
                        response = JsonPacket.ofError(e1.getErrorCode(), e1.getErrorMessage());
                    } else {
                        // 消息处理过程中一切未捕捉的异常
                        response = JsonPacket.ofError(ErrorCodeEnum.ERR_UNKNOWN_MESSAGE_HANDLE_EXCEPTION, e.getMessage());
                    }
                }
                // 返回处理结果给客户端
                if (response != null) {
                    // 如果请求包含有ext字段（透传信息）则响应中也原封不动的返回
                    response.withExt(ext).writeTo(session);
                }
                // 后置处理
                postHandle(session, request, startTime);
            }
        });
    }
}
