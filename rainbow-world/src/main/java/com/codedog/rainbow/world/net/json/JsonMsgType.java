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

    // ============================
    // You can add more types here.
    // ============================

    UNRECOGNIZED(-1, "UNRECOGNIZED");

    @Getter private final int value;
    @Getter private final String text;

    JsonMsgType(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
