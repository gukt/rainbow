/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.google.protobuf.ByteString;

import java.io.Serializable;

/**
 * ProtoPacketMessageResolver class
 *
 * @author https://github.com/gukt
 */
public class ProtoPacketMessageResolver implements MessageResolver<ProtoPacket> {

    public Object getRtd(ProtoPacket msg) {
        return msg.getRtd();
    }

    @Override
    public String getType(ProtoPacket msg) {
        // TODO 要不要用一个 Object 类型
        return msg.getType().name();
    }

    @Override
    public String toCompactString(ProtoPacket msg) {
        return String.format("ProtoPacket#%d-%s-%s", msg.getSn(), msg.getType(), msg.getPayload());
    }

    @Override
    public ProtoPacket resolveError(Serializable code, String error) {
        return null;
    }

    @Override
    public ProtoPacket withRtd(ProtoPacket msg, Object rtd) {
        // TODO 需不需要 build?
        // TODO 这里 rtd 赚 ByteString 有问题
        msg = msg.toBuilder().setRtd((ByteString) rtd).build();
        return msg;
    }

    @Override
    public Class<ProtoPacket> getMessageClass() {
        return ProtoPacket.class;
    }
}
