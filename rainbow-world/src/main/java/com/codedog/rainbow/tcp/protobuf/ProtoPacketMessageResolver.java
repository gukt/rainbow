/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.protobuf;

import com.codedog.rainbow.tcp.MessageResolver;
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
    public Object getPayload(ProtoPacket msg) {
        // TODO 要不要用一个 Object 类型
        return msg.getPayload();
    }

    @Override
    public void setAck(ProtoPacket msg, int ack) {

    }

    @Override
    public void setTime(ProtoPacket msg, long timestamp) {

    }

    @Override
    public void setSync(ProtoPacket msg, int sync) {

    }

    @Override
    public String toString(ProtoPacket msg, boolean compact) {
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

    // @Override
    // @SuppressWarnings("unchecked")
    // public <V> Optional<V> resolveArgs(ProtoPacket msg, Class<?> paramType) {
    //     if(ByteString.class.isAssignableFrom(paramType)) {
    //         return Optional.ofNullable((V)getPayload(msg));
    //     }
    //     return Optional.empty();
    // }

    @Override
    public Class<ProtoPacket> getMessageClass() {
        return ProtoPacket.class;
    }

    @Override
    public void setSn(ProtoPacket msg, int sn) {
        // TODO 这里是不是有点问题
        msg = msg.toBuilder().setSn(sn).build();
    }
}
