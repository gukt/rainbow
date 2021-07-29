/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.tcp.MessageHandler;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.world.net.json.JsonPacket;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author https://github.com/gukt
 */
@Component
public final class EchoMessageHandler implements MessageHandler<JsonPacket> {

    @Override
    public String getType() {
        return "Echo";
    }

    @Override
    public Object handle(@NonNull Session session, @NonNull JsonPacket message) {
        return message;
    }
}
