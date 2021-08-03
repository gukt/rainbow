/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.util.PeerInfo;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * DefaultSession
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class DefaultSession extends AbstractSession {

    private ChannelHandlerContext delegate;

    protected DefaultSession(ChannelHandlerContext delegate, TcpProperties properties) {
        super(properties);
        this.delegate = delegate;
        this.peerInfo = PeerInfo.of(delegate.channel().remoteAddress());
        this.id = peerInfo.getAddressString();
    }

    @Override
    public CompletableFuture<Session> write(Object message, boolean flush) {
        message = beforeWrite(message);
        if (message != null) {
            log.debug("TCP - Writing: {} -> {}", message, this);
            if (isClosed()) {
                log.warn("TCP - You are writing message to a closed session: message={}, session={}", message, this);
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
        // Double-checked locking (https://en.wikipedia.org/wiki/Double-checked_locking)
        if (!isClosed()) {
            synchronized (this) {
                if (!isClosed()) {
                    // Flush and close
                    delegate.flush().close().addListener((ChannelFutureListener) future ->
                            closeTimeMillis = System.currentTimeMillis());
                }
            }
        }
        // TODO FIX IT
        return new CompletableFuture<>();
    }

    /**
     * 重用此连接
     *
     * @param session
     */
    public void reuseBy(Session session) {
        DefaultSession currentSession = (DefaultSession) session;
        // 将旧的连接关闭，防止对端在第一条连接没有关闭的情况下又发起一条
        // 确保之前的连接是断开的非常重要，因为在那条连接上服务器可能关联监听器，比如存档逻辑
        // 两条连接各自取自己Session中的数据进行存档，会造成数据相互覆盖
        this.delegate.flush().close();
        this.delegate = currentSession.delegate;
        this.reopen();
    }

    @Override
    protected Object beforeWrite(Object message) {
        return message;
    }
}
