/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.support.AttributeSupport;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
public interface Session extends AttributeSupport {

    /**
     * Session ID, 一般使用对端地址表示
     *
     * @return return session id
     */
    Serializable getId();

    /**
     * 获得对端相关连接信息
     *
     * @return 返回对端相关连接信息
     */
    PeerInfo getPeerInfo();

    /**
     * 发送指定的消息给对端
     *
     * @param message 要发送的消息
     * @return 返回WriteFuture对象，便于监听完成事件并处理回调逻辑
     */
    CompletableFuture<Void> write(Object message);

    /**
     * 发送指定的消息给对端
     *
     * @param message 要发送的消息
     * @param flush 是否立即发送
     * @return 返回WriteFuture对象，便于监听完成事件并处理回调逻辑
     */
    CompletableFuture<Void> write(Object message, boolean flush);

    /**
     * 关闭连接
     *
     * @return 返回CloseFuture对象，便于监听完成事件并处理回调逻辑
     */
    CompletableFuture<Void> close();

    /**
     * 该连接是否已关闭中，这里的关闭是指物理连接已断开
     *
     * @return 如果物理连接已断开返回true，反之false
     */
    boolean isClosed();

    long getCloseTimeMillis();

    SessionStore getStore();

    /**
     * 设置当前正在处理的请求
     *
     * @param request request object
     */
    void setProcessingRequest(Object request);

    /**
     * 返回当前是否有请求正在处理中
     *
     * @return 如果有返回true，反之false
     */
    boolean isProcessing();

    /**
     * 完成对请求的处理，该方法主要用于：
     * 1. 将当前已成功处理完成的消息出列
     * 2. 将当前正在处理的请求标记位设置为null，以便MessagePumper可以继续调度下一条请求，该机制保证了单个Session请求的串行执行
     */
    void completeRequest();

    void completeRequestExceptionally();

    /**
     * 用于断线重连成功后
     */
    void reopen();

    // default <V> void beforeWrite(V message) {}
    //
    // default <V> void afterWrite(V message) {}
}
