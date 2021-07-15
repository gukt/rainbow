/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */
package com.codedog.rainbow.world.net;

/**
 * 消息分发器，用以将Packet消息或HttpRequest派发到指定的handler处理
 *
 * @author https://github.com/gukt
 */
public interface MessageDispatcher {

    /**
     * Start message dispatcher
     */
    void start();

    /**
     * Stop message dispatcher
     */
    void stop();

    /**
     * Register specified handler
     *
     * @param handler handler object
     */
    void registerHandler(MessageHandler<?> handler);
}
