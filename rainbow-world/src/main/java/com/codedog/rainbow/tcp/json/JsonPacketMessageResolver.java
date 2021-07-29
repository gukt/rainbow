/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.json;

import com.codedog.rainbow.tcp.MessageResolver;
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
        return JsonPacket.ofError((int) code, error);
    }

    @Override
    public JsonPacket withRtd(JsonPacket msg, Object rtd) {
        return msg.withRtd(rtd);
    }

    // @Override
    // @SuppressWarnings("unchecked")
    // public <V> Optional<V> resolveArgs(JsonPacket msg, Class<?> paramType) {
    //     V value = null;
    //     if (Set.class.isAssignableFrom(paramType)) {
    //         value = (V) new HashSet<>((Collection<V>) getPayload(msg));
    //     } else if (List.class.isAssignableFrom(paramType)) {
    //         value = (V) new ArrayList<>((Collection<V>) getPayload(msg));
    //     }
    //     return Optional.ofNullable(value);
    // }

    @Override
    public Class<JsonPacket> getMessageClass() {
        return JsonPacket.class;
    }

    @Override
    public void setSn(JsonPacket msg, int sn) {
        msg.setSn(sn);
    }

    @Override
    public void setAck(JsonPacket msg, int ack) {
        msg.setAck(ack);
    }

    @Override
    public void setTime(JsonPacket msg, long timestamp) {
        msg.setTime(timestamp);
    }

    @Override
    public void setSync(JsonPacket msg, int sync) {
        msg.setSync(sync);
    }
}
