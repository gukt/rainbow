/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.codec.json;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import com.codedog.rainbow.util.Jsons;
import com.codedog.rainbow.world.net.json.JsonPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class JsonEncoder extends MessageToMessageEncoder<JsonPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonPacket msg, List<Object> out) {
        String response = Jsons.toJson(msg);
        ByteBuf buf = wrappedBuffer(response.getBytes());
        out.add(buf);
    }
}
