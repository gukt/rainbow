/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

/**
 * 消息拦截器，拦截器会在消息被分发之前被处理
 *
 * @author https://github.com/gukt
 */
public interface MessageInterceptor<T> {

    /**
     * 消息预处理，如果通过预处理则返回true，反之false，如果返回false，则消息不会进入分发流程
     *
     * @param session The session object
     * @param request The request object
     * @return 如果通过预处理返回true，反之false，返回false时消息不会进入后续分发流程
     */
    default boolean preHandle(Session session, T request) {
        return true;
    }

    /**
     * 后置处理
     *
     * @param session The session object
     * @param request The request object
     */
    default void postHandle(Session session, T request) {
        // NOOP
    }
}
