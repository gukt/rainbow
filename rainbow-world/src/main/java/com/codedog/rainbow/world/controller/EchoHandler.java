/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.world.net.MessageHandler;
import com.codedog.rainbow.world.net.Session;
import com.codedog.rainbow.world.net.json.JsonPacket;
import lombok.NonNull;
import org.springframework.stereotype.Controller;

/**
 * @author https://github.com/gukt
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
