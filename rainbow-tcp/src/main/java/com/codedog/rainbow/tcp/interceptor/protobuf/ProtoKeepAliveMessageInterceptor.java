/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.interceptor.protobuf;

import com.codedog.rainbow.tcp.interceptor.KeepAliveMessageInterceptor;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket.MsgType;

/**
 * 该拦截器用于拦截 KeepAlive 请求。对于 KeepAlive 消息只接收不响应，保持读通道不空闲即可。
 *
 * @author https://github.com/gukt
 */
public final class ProtoKeepAliveMessageInterceptor extends KeepAliveMessageInterceptor<ProtoPacket> {

    public boolean isKeepAlive(ProtoPacket request) {
        return request.getType() == MsgType.KeepAlive;
    }
}
