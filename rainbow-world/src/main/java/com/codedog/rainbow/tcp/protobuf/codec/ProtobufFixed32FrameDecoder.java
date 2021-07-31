/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.protobuf.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * @author https://github.com/gukt
 */
public class ProtobufFixed32FrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int lenBytes = 4;
        if (in.readableBytes() < lenBytes) {
            return;
        }
        in.markReaderIndex();
        int len = in.readInt();
        if (len < 0) {
            throw new CorruptedFrameException("Codec: negative length: " + len);
        }
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
        } else {
            out.add(in.readBytes(len));
        }
        // Couldn't find the byte whose MSB is off.
        // throw new CorruptedFrameException("length wider than 32-bit");
    }
}
