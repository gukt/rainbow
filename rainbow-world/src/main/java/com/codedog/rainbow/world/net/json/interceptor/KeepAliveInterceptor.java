/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.json.interceptor;

import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.json.MsgTypeEnum;
import com.codedog.rainbow.world.net.MessageInterceptor;
import com.codedog.rainbow.world.net.Session;

/**
 * 该拦截器用于拦截KeepAlive请求，对于KeepAlive消息，只接收不响应，保持读通道不空闲即可
 *
 * @author gukt <gukaitong@gmail.com>
 */
public final class KeepAliveInterceptor implements MessageInterceptor<JsonPacket> {

    @Override
    public boolean preHandle(Session session, JsonPacket request) {
        // KeepAlive也占用一个包序
        session.getStore().getNextAck().incrementAndGet();

        // 将不等于KeepAlive类型的消息放行
        return !MsgTypeEnum.KEEP_ALIVE.getText().equals(request.getType());
    }
}
