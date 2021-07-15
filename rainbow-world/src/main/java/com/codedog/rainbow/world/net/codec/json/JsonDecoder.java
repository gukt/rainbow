/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.codec.json;

import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.util.Jsons;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class JsonDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String json = new String(bytes);
        // TODO 如果JSON解析异常这里会怎么表现
        JsonPacket packet = Jsons.toBean(json, JsonPacket.class);
        out.add(packet);
    }
}
