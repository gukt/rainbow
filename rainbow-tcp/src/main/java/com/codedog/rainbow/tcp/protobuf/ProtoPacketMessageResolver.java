/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.protobuf;

import com.codedog.rainbow.tcp.MessageResolver;
import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.world.generated.CommonProto.Error;
import com.codedog.rainbow.world.generated.CommonProto.Error.ErrorCode;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket.MsgType;
import com.google.protobuf.ByteString;

/**
 * ProtoPacketMessageResolver class
 *
 * @author https://github.com/gukt
 */
public class ProtoPacketMessageResolver implements MessageResolver<ProtoPacket> {

    @Override
    public Object getRtd(ProtoPacket message) {
        return message.getRtd();
    }

    @Override
    public MsgType getType(ProtoPacket message) {
        return message.getType();
    }

    @Override
    public Object getPayload(ProtoPacket message) {
        // TODO 要不要用一个 Object 类型
        return message.getPayload();
    }

    @Override
    public void setAck(ProtoPacket message, int ack) {

    }

    @Override
    public void setTime(ProtoPacket message, long timestamp) {

    }

    @Override
    public void setSync(ProtoPacket message, int sync) {

    }

    @Override
    public void setSn(ProtoPacket message, int sn) {
        // TODO 这里是不是有点问题
        message = message.toBuilder().setSn(sn).build();
    }

    @Override
    public String toString(ProtoPacket message, boolean compact) {
        return String.format("ProtoPacket#%d-%s-%s", message.getSn(), message.getType(), message.getPayload());
    }

    @Override
    public ProtoPacket errorOf(int code, String msg) {
        return ProtoUtils.wrap(Error.newBuilder()
                .setCode(ErrorCode.forNumber(code))
                .setMsg(msg));
    }

    @Override
    public ProtoPacket withRtd(ProtoPacket message, Object rtd) {
        // TODO 需不需要 build?
        // TODO 这里 rtd 赚 ByteString 有问题
        message = message.toBuilder().setRtd((ByteString) rtd).build();
        return message;
    }

    @Override
    public Class<ProtoPacket> getMessageClass() {
        return ProtoPacket.class;
    }
}
