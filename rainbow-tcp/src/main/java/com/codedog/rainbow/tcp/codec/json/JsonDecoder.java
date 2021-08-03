/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.codec.json;

import com.codedog.rainbow.tcp.message.JsonPacket;
import com.codedog.rainbow.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author https://github.com/gukt
 */
public class JsonDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String json = new String(bytes);
        // TODO 如果JSON解析异常这里会怎么表现
        JsonPacket packet = JsonUtils.toBean(json, JsonPacket.class);
        out.add(packet);
    }
}
