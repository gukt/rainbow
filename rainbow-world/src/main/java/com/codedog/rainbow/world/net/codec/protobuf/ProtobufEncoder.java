/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.codec.protobuf;

import com.codedog.rainbow.util.ProtoPackets;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Slf4j
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
        try {
            ProtoPacket packet = ProtoPackets.wrap(msg);
            if (packet != null) {
                ByteBuf buf = Unpooled.wrappedBuffer(packet.toByteArray());
                out.add(buf);
            } else {
                log.warn("TCP: a null packet wrapped, is this correct?");
            }
        } catch (Exception e) {
            log.error("TCP: exception: ", e);
        }
    }
}
