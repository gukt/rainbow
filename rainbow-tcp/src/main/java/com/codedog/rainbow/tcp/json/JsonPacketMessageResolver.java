/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.json;

import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.MessageResolver;
import lombok.extern.slf4j.Slf4j;

/**
 * JsonPacketMessageResolver class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class JsonPacketMessageResolver implements MessageResolver<JsonPacket> {

    @Override
    public Object getRtd(JsonPacket message) {
        return message.getRtd();
    }

    @Override
    public String getType(JsonPacket message) {
        return message.getType();
    }

    @Override
    public String toString(JsonPacket message, boolean compact) {
        return String.format("JsonPacket#%d-%s-%s", message.getSn(), message.getType(), message.getPayload());
    }

    @Override
    public JsonPacket errorOf(int code, String msg) {
        return JsonPacket.errorOf(code, msg);
    }

    @Override
    public JsonPacket withRtd(JsonPacket message, Object rtd) {
        return message.withRtd(rtd);
    }

    // @SuppressWarnings("unchecked")
    // @Override
    // public JsonPacket wrap(Object obj) {
    //     if(!(obj instanceof JsonPacket)) {
    //         log.warn("不支持的对象");
    //     }
    //     return (JsonPacket) obj;
    // }

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
    public void setSn(JsonPacket message, int sn) {
        message.setSn(sn);
    }

    @Override
    public void setAck(JsonPacket message, int ack) {
        message.setAck(ack);
    }

    @Override
    public void setTime(JsonPacket message, long timestamp) {
        message.setTime(timestamp);
    }

    @Override
    public void setSync(JsonPacket message, int sync) {
        message.setSync(sync);
    }
}
