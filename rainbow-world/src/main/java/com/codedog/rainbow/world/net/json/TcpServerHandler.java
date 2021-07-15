/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.json;

import static com.codedog.rainbow.world.net.NetConstants.SESSION_KEY;

import com.codedog.rainbow.world.EventPublisher;
import com.codedog.rainbow.world.GameOptions;
import com.codedog.rainbow.world.net.ErrorCodeEnum;
import com.codedog.rainbow.world.net.Session;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * AbstractTcpServerHandler
 * 创建TcpServerHandler对象能不依赖具体类型吗
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Slf4j
@Sharable
@Component
public final class TcpServerHandler extends AbstractTcpServerHandler<JsonPacket> {

    public TcpServerHandler(GameOptions opts, EventPublisher eventPublisher) {
        super(opts, eventPublisher);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("TCP: New client registering: {}", ctx.channel());
        if (!active) {
            rejectConnection(ctx, JsonPacket.ofError(ErrorCodeEnum.ERR_SERVER_UNAVAILABLE));
            return;
        }
        if (isOverload()) {
            rejectConnection(ctx, JsonPacket.ofError(ErrorCodeEnum.ERR_SERVER_IS_BUSYING));
            return;
        }
        if (isMaxConnectionsExceeded()) {
            rejectConnection(ctx, JsonPacket.ofError(ErrorCodeEnum.ERR_EXCEED_MAX_CONNECTION));
            return;
        }
        super.channelRegistered(ctx);
        log.debug("TCP: New client registered: {}", ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final JsonPacket request) {
        final Session session = ctx.channel().attr(SESSION_KEY).get();
        log.debug("TCP: Receiving: {} <- {}, w:{}", request, session, session.getStore().getPendingRequests().size());

        // 只有通过所有interceptors的前置检查，才会执行后续消息处理
        if (applyPreHandle(session, request)) {
            // the error response for fail-fast
            JsonPacket err = null;

            // 如果服务器处于“非运行”状态(此时应该是正在关闭)，此时对于接受到的任何新消息都立即返回错误
            if (!active) {
                err = JsonPacket.ofError(ErrorCodeEnum.ERR_SERVER_UNAVAILABLE);
            }
            // 如果当前系统过载，则立即返回"系统忙"错误
            else if (isOverload()) {
                err = JsonPacket.ofError(ErrorCodeEnum.ERR_SERVER_IS_BUSYING);
            }
            // 将接收到的请求消息入列到buffer中等待处理，
            // 如果入列失败，表示积压待处理的消息太多，立即返回错误码
            else if (!session.getStore().getPendingRequests().write(request)) {
                err = JsonPacket.ofError(ErrorCodeEnum.ERR_EXCEED_SESSION_MAX_BACKLOGS);
            }
            if (err != null) {
                err.withExt(request.getExt()).writeTo(session);
            }
        }
        // apply each post handle in reverse order
        this.applyPostHandle(session, request);
    }
}
