/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.codec.json;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author https://github.com/gukt
 */
public class JsonSecurity extends ChannelDuplexHandler {

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
    }
}
