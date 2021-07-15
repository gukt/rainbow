/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.codec.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
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
