/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.world.net.json.JsonPacket;

import java.io.Serializable;

/**
 * JsonPacketMessageResolver class
 *
 * @author https://github.com/gukt
 */
public class JsonPacketMessageResolver implements MessageResolver<JsonPacket> {

    @Override
    public Object getRtd(JsonPacket msg) {
        return msg.getRtd();
    }

    @Override
    public String getType(JsonPacket msg) {
        return msg.getType();
    }

    @Override
    public String toCompactString(JsonPacket msg) {
        return String.format("JsonPacket#%d-%s-%s", msg.getSn(), msg.getType(), msg.getPayload());
    }

    @Override
    public JsonPacket resolveError(Serializable code, String error) {
        return null;
    }

    @Override
    public JsonPacket withRtd(JsonPacket msg, Object rtd) {
        return msg.withRtd(rtd);
    }

    @Override
    public Class<JsonPacket> getMessageClass() {
        return JsonPacket.class;
    }
}
