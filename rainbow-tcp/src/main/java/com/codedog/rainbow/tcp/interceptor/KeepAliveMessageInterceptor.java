/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.interceptor;

import com.codedog.rainbow.tcp.session.Session;

/**
 * 该拦截器用于拦截 KeepAlive 请求。对于 KeepAlive 消息只接收不响应，保持读通道不空闲即可。
 *
 * @author https://github.com/gukt
 */
public abstract class KeepAliveMessageInterceptor<T> implements MessageInterceptor<T> {

    @Override
    public boolean preHandle(Session session, T request) {
        // KeepAlive 也占用一个包序
        session.getStore().getAckNumber().incrementAndGet();

        // 如果是 KeepAlive, 返回 false，表示不继续往后传播了
        // 如果不是 KeepAlive，则继续放行
        return !isKeepAlive(request);
    }

    protected abstract boolean isKeepAlive(T request);
}
