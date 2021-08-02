/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net.json;

import lombok.Getter;

/**
 * @author https://github.com/gukt
 */
public enum JsonMsgType {
    /**
     * KeepAlive
     */
    KEEP_ALIVE(0, "KeepAlive"),
    ERROR(0, "Error"),
    PING(1, "Ping"),
    PONG(2, "Pong"),

    GAME_ENTER_REQUEST(101, "GAME_ENTER_REQUEST"),
    GAME_ENTER_RESPONSE(102, "GAME_ENTER_RESPONSE"),
    RECONNECT_REQUEST(103, "RECONNECT_REQUEST"),
    RECONNECT_RESPONSE(104, "RECONNECT_RESPONSE"),

    // write more
    ;

    @Getter
    private final int value;
    // TODO 改成 text()
    @Getter
    private final String text;

    JsonMsgType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    // JsonPacket toJsonPacket() {
    //     return JsonPacket.of(text);
    // }
    //
    // public JsonPacket toJsonPacket(Object payload) {
    //     JsonPacket packet = JsonPacket.of(text);
    //     packet.setPayload(payload);
    //     return packet;
    // }
}
