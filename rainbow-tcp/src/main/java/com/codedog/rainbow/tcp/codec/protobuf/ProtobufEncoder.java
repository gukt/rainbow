/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.codec.protobuf;

import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
        try {
            ProtoPacket packet = ProtoUtils.wrap(msg);
            if (packet != null) {
                ByteBuf buf = Unpooled.wrappedBuffer(packet.toByteArray());
                out.add(buf);
            } else {
                log.warn("TCP - a null packet wrapped, is this correct?");
            }
        } catch (Exception e) {
            log.error("TCP - exception: ", e);
        }
    }
}
