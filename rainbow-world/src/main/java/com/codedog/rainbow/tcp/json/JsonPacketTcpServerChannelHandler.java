/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.json;

import com.codedog.rainbow.tcp.MessageResolver;
import com.codedog.rainbow.tcp.TcpServerChannelHandler;
import com.codedog.rainbow.world.config.TcpProperties;
import com.codedog.rainbow.world.net.json.JsonPacket;


/**
 * JsonPacketTcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
public class JsonPacketTcpServerChannelHandler extends TcpServerChannelHandler<JsonPacket> {

    public JsonPacketTcpServerChannelHandler(TcpProperties properties, MessageResolver<JsonPacket> resolver) {
        super(properties, resolver);
    }
}
