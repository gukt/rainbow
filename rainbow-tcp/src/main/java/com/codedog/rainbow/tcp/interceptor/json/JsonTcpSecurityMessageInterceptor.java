/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.interceptor.json;

import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.interceptor.TcpSecurityMessageInterceptor;

/**
 * ProtoTcpSecurityMesssageInterceptor class
 *
 * @author https://github.com/gukt
 */
public class JsonTcpSecurityMessageInterceptor extends TcpSecurityMessageInterceptor<JsonPacket> {

    public JsonTcpSecurityMessageInterceptor(TcpProperties properties) {
        super(properties);
    }

    @Override
    protected int getSn(JsonPacket message) {
        return message.getSn();
    }

    @Override
    protected int getAck(JsonPacket message) {
        return message.getAck();
    }
}
