/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */
package com.codedog.rainbow.tcp.protobuf.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author https://github.com/gukt
 */
@Sharable
public class ProtobufFixed32LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        int bodyLen = msg.readableBytes();
        int headerLen = 4;
        out.ensureWritable(headerLen + bodyLen);
        out.writeInt(bodyLen);
        out.writeBytes(msg, msg.readerIndex(), bodyLen);
    }
}
