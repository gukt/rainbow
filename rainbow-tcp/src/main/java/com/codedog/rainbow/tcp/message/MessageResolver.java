/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.message;

import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;

/**
 * MessageResolver class
 *
 * @author https://github.com/gukt
 */
public interface MessageResolver<T> {

    class ProtoPacketMessageResolver implements MessageResolver<ProtoPacket> {}

    class JsonPacketMessageResolver implements MessageResolver<JsonPacket> {}
}
