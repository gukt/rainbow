/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.interceptor.protobuf;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.interceptor.TcpSecurityMessageInterceptor;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacketOrBuilder;

/**
 * ProtoTcpSecurityMesssageInterceptor class
 *
 * @author https://github.com/gukt
 */
public class ProtoTcpSecurityMessageInterceptor extends TcpSecurityMessageInterceptor<ProtoPacketOrBuilder> {

    public ProtoTcpSecurityMessageInterceptor(TcpProperties properties) {
        super(properties);
    }

    @Override
    protected int getSn(ProtoPacketOrBuilder message) {
        return message.getSn();
    }

    @Override
    protected int getAck(ProtoPacketOrBuilder message) {
        return message.getAck();
    }
}
