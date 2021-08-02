/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.json;

import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.TcpServerChannelHandler;


/**
 * JsonPacketTcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
public final class JsonPacketTcpServerChannelHandler extends TcpServerChannelHandler<JsonPacket> {

    public JsonPacketTcpServerChannelHandler(TcpProperties properties) {
        super(properties);
    }
}
