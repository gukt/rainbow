/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.PacketDispatcher.PacketResolver;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.world.config.TcpProperties;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * AbstractTcpServerHandler
 * 创建TcpServerHandler对象能不依赖具体类型吗
 *
 * @author https://github.com/gukt
 */
@Slf4j
@Sharable
public class TcpServerHandler<T> extends AbstractTcpServerHandler<T> {

    private final PacketResolver<T> resolver;

    public TcpServerHandler(TcpProperties tcpProperties, PacketResolver<T> resolver) {
        super(tcpProperties);
        this.resolver = resolver;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("TCP: New client registering: {}", ctx.channel());
        if (!active) {
            rejectConnection(ctx, resolver.serverUnavailable());
            return;
        }
        if (isOverload()) {
            rejectConnection(ctx, resolver.serverBusyingError());
            return;
        }
        if (isConnectionsExceeded()) {
            rejectConnection(ctx, resolver.connectionExceededError());
            return;
        }
        super.channelRegistered(ctx);
        log.debug("TCP: New client registered: {}", ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final T message) {
        final Session session = ctx.channel().attr(Session.KEY).get();
        log.debug("TCP: Receiving: {} <- {}, w:{}", message, session, session.getStore().getPendingRequests().size());

        // 只有通过所有interceptors的前置检查，才会执行后续消息处理
        if (applyPreHandle(session, message)) {
            // the error response for fail-fast
            T err = null;
            // 如果服务器处于“非运行”状态(此时应该是正在关闭)，此时对于接受到的任何新消息都立即返回错误
            if (!active) {
                err = resolver.serverUnavailable();
            }
            // 如果当前系统过载，则立即返回"系统忙"错误
            else if (isOverload()) {
                err = resolver.serverBusyingError();
            }
            // 将接收到的请求消息入列到 buffer 中等待处理，
            // 如果入列失败，表示积压待处理的消息太多，立即返回错误码
            else if (!session.getStore().getPendingRequests().write(message)) {
                err = resolver.sessionBacklogsExceededError();
            }
            if (err != null) {
//                err.withRtd(request.getRtd()).writeTo(session);
                Object rtd = resolver.getRtd(message);
                session.write(resolver.withRtd(message, rtd));
            }
        }
        // apply each post handle in reverse order
        this.applyPostHandle(session, message);
    }
}
