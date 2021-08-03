/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */
package com.codedog.rainbow.tcp.message;

/**
 * 消息分发器，用以派发消息到指定的 {@link MessageHandler 消息处理器} 进行处理。
 *
 * @author https://github.com/gukt
 */
public interface MessageDispatcher {

    /**
     * 启动消息分发器
     */
    void start();

    /**
     * 停止消息分发器
     */
    void stop();

    /**
     * 注册一个 {@link MessageHandler} 用以处理指定类型的消息。不能为 null。
     * 如果重复注册，则最后一次注册的覆盖之前的。
     *
     * @param handler {@link MessageHandler 消息处理器}
     */
    void registerHandler(MessageHandler<?> handler);
}
