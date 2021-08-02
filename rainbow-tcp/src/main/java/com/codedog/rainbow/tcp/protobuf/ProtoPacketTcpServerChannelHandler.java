/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.protobuf;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.TcpServerChannelHandler;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;


/**
 * ProtoPacketTcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
public final class ProtoPacketTcpServerChannelHandler extends TcpServerChannelHandler<ProtoPacket> {

    public ProtoPacketTcpServerChannelHandler(TcpProperties properties) {
        super(properties);
    }
}
