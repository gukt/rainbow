/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.codec.json;

import com.codedog.rainbow.util.JsonUtils;
import com.codedog.rainbow.world.net.json.JsonPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * @author https://github.com/gukt
 */
public class JsonEncoder extends MessageToMessageEncoder<JsonPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonPacket msg, List<Object> out) {
        String response = JsonUtils.toJson(msg);
        ByteBuf buf = wrappedBuffer(response.getBytes());
        out.add(buf);
    }
}
