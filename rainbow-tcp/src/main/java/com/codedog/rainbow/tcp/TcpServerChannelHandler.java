/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.BaseError;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import static com.codedog.rainbow.tcp.util.BaseError.SERVER_BUSYING;
import static com.codedog.rainbow.tcp.util.BaseError.SERVER_UNAVAILABLE;


/**
 * AbstractTcpServerHandler
 * 创建TcpServerHandler对象能不依赖具体类型吗
 *
 * @author https://github.com/gukt
 */
@Slf4j
@Sharable
public class TcpServerChannelHandler<T> extends AbstractTcpServerHandler<T> {

    public TcpServerChannelHandler(TcpProperties properties) {
        super(properties);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("TCP - New client registering: {}", ctx.channel());
        if (!active) {
            rejectConnection(ctx, resolver.errorOf(SERVER_UNAVAILABLE));
            return;
        }
        if (isOverload()) {
            rejectConnection(ctx, resolver.errorOf(SERVER_BUSYING));
            return;
        }
        if (isConnectionsExceeded()) {
            rejectConnection(ctx, resolver.errorOf(BaseError.EXCEED_CONNECTIONS));
            return;
        }
        super.channelRegistered(ctx);
        log.debug("TCP - New client registered: {}", ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final T message) {
        final Session session = ctx.channel().attr(Session.KEY).get();
        log.debug("TCP - Receiving a message: {}, session={}, current queued={}", message, session, session.getStore().getPendingRequests().size());

        // 应用拦截器链的所有前置检查
        if (applyPreHandle(session, message)) {
            T err = null;
            if (!active) { // non-active?
                err = resolver.errorOf(BaseError.SERVER_UNAVAILABLE);
            } else if (isOverload()) { // Busying?
                err = resolver.errorOf(BaseError.SERVER_BUSYING);
            }
            // 将接收到的请求消息入列到 buffer 中等待处理，
            // 如果入列失败，表示积压待处理的消息太多，立即返回错误码
            // TODO 这里的写法太长了，是不是得改写一下？
            else if (!session.getStore().getPendingRequests().write(message)) {
                err = resolver.errorOf(BaseError.EXCEED_SESSION_BACKLOGS);
            }
            if (err != null) {
                Object rtd = resolver.getRtd(message);
                session.write(resolver.withRtd(err, rtd));
            }
        }
        // 应用拦截器链中的“后置处理”
        this.applyPostHandle(session, message);
    }
}
