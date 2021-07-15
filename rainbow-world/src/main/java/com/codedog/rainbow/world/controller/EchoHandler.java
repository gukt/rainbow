/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.MessageHandler;
import com.codedog.rainbow.world.net.Session;
import lombok.NonNull;
import org.springframework.stereotype.Controller;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-09 16:26
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Controller
public final class EchoHandler implements MessageHandler<JsonPacket> {

    @Override
    public String getType() {
        return "Echo";
    }

    @Override
    public Object handle(@NonNull Session session, @NonNull JsonPacket message) {
        return message;
    }
}
