/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.world.config.TcpProperties;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;


/**
 * ProtoPacketTcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
public class ProtoPacketTcpServerChannelHandler extends TcpServerChannelHandler<ProtoPacket> {

    public ProtoPacketTcpServerChannelHandler(TcpProperties properties, MessageResolver<ProtoPacket> resolver) {
        super(properties, resolver);
    }
}
