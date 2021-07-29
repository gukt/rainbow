/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import com.codedog.rainbow.core.AttributeSupport;
import com.codedog.rainbow.tcp.util.PeerInfo;
import io.netty.util.AttributeKey;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * 表示一个客户端连接
 *
 * @author https://github.com/gukt
 */
public interface Session extends AttributeSupport {

    AttributeKey<Session> KEY = AttributeKey.newInstance("session");

    /**
     * Session 的唯一标记，一般使用 <code>Remote Address</code> 表示。
     *
     * @return Session 的唯一标记
     */
    Serializable getId();

    /**
     * 获得当前连接客户端（{@link PeerInfo 对端}）相关信息。
     *
     * @return {@link PeerInfo}
     */
    PeerInfo getPeerInfo();

    /**
     * 发送指定的消息给对端。如果 <code>message</code> 为 null 将什么都不做。
     *
     * @param message 要发送的消息。如果为 null，则什么都不做
     * @return 返回 {@link CompletableFuture } 对象，便于监听完成事件并处理回调逻辑
     */
    CompletableFuture<Void> write(Object message);

    /**
     * 发送指定的消息给对端。可以指定 <code>flush = true</code> 立即发送。
     *
     * @param message 要发送的消息。如果为 null，则什么都不做
     * @param flush   是否立即发送
     * @return 返回 {@link CompletableFuture } 对象，便于监听完成事件并处理回调逻辑
     */
    CompletableFuture<Void> write(Object message, boolean flush);

    /**
     * 关闭当前连接。
     *
     * @return 返回 {@link CompletableFuture } 对象，便于监听完成事件并处理回调逻辑
     */
    CompletableFuture<Void> close();

    /**
     * 该连接是否已关闭，这里的'关闭'指的是物理连接已断开。
     *
     * @return 如果物理连接已断开，返回 true；反之 false
     */
    boolean isClosed();

    /**
     * 获取关闭连接时间戳（毫秒）
     *
     * @return 关闭连接时间戳
     */
    long getCloseTimeMillis();

    SessionStore getStore();

    /**
     * 设置当前正在处理的请求
     *
     * @param request request object
     */
    void setProcessingRequest(Object request);

    /**
     * 当前是否有请求正在处理中
     *
     * @return 如果有，返回 true；反之 false
     */
    boolean isProcessing();

    /**
     * 完成请求，该方法主要用于：
     * <li>将当前已成功处理完成的消息出列</li>
     * <li>将当前正在处理的请求标记位设置为 null，以便 MessagePumper 可以继续调度下一条请求，该机制保证了单个 Session 请求的串行执行</li>
     */
    void completeRequest();

    void completeRequestExceptionally();

    /**
     * 重用（重新打开）该连接，用于断线重连。
     */
    void reopen();

    // default <V> void beforeWrite(V message) {}
    //
    // default <V> void afterWrite(V message) {}
}
