/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.interceptor.json;

import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.interceptor.KeepAliveMessageInterceptor;

/**
 * 该拦截器用于拦截 KeepAlive 请求。对于 KeepAlive 消息只接收不响应，保持读通道不空闲即可。
 *
 * @author https://github.com/gukt
 */
public final class JsonKeepAliveMessageInterceptor extends KeepAliveMessageInterceptor<JsonPacket> {

    private String messageType = "KeepAlive";

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    protected boolean isKeepAlive(JsonPacket request) {
        return request.getType().equalsIgnoreCase(messageType);
    }
}
