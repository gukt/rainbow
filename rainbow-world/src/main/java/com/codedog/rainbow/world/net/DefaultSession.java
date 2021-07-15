/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Slf4j
public class DefaultSession extends AbstractSession {

    private ChannelHandlerContext delegate;

    protected DefaultSession(ChannelHandlerContext delegate, int maxPendingRequestSize, int maxCacheResponseSize) {
        super(maxPendingRequestSize, maxCacheResponseSize);
        this.delegate = delegate;
        this.peerInfo = PeerInfo.builder()
                .remoteAddress((InetSocketAddress) delegate.channel().remoteAddress())
                .build();
        this.id = peerInfo.getRemoteAddress().toString();
    }

    @Override
    public CompletableFuture<Void> write(@NonNull Object message, boolean flush) {
        if (beforeWrite(message)) {
            log.debug("TCP: Writing: {} -> {}", message, this);

            if (isClosed()) {
                log.warn("TCP: Write message to a closed session: {} -> {}", message, this);
            }
            if (flush) {
                delegate.writeAndFlush(message);
            } else {
                delegate.write(message);
            }
        }
        // TODO FIX IT
        return new CompletableFuture<>();
    }

    // /**
    //  * 主要做以下工作:
    //  * 1. 递增seq以及给time字段赋值
    //  * 2. 缓存消息
    //  *
    //  * @param message 要发送的message
    //  */
    // @Override
    // protected boolean beforeWrite(@NonNull Object message) {
    //     int seq = store.incrementSeq();
    //     long now = System.currentTimeMillis();
    //     message.setSeq(seq);
    //     message.setTime(now);
    //     message.setSync(processingRequest.getSync());
    //     // 将该消息压缩后缓存起来
    //     store.getCachedResponses().write(new Object[]{seq, Encrypts.encrypt(message)});
    //     return true;
    // }

    @Override
    public CompletableFuture<Void> close() {
        // double-checked locking
        if (!isClosed()) {
            synchronized (this) {
                if (!isClosed()) {
                    // flush then close
                    delegate.flush().close().addListener((ChannelFutureListener) future ->
                            closeTimeMillis = System.currentTimeMillis());
                }
            }
        }
        // TODO FIX IT
        return new CompletableFuture<>();
    }

    public void attachTo(DefaultSession targetSession) {
        // 代表旧的连接
        ChannelHandlerContext old = targetSession.delegate;
        targetSession.delegate = this.delegate;
        // 将旧的连接关闭，防止对端在第一条连接没有关闭的情况下又发起一条
        // 确保之前的连接是断开的非常重要，因为在那条连接上服务器可能关联监听器，比如存档逻辑
        // 两条连接各自取自己Session中的数据进行存档，会造成数据相互覆盖
        old.flush().close();
    }

    public boolean beforeWrite(@NonNull Object message) {
        return true;
    }
}
