/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.json;

import lombok.Getter;

/**
 * @author https://github.com/gukt
 */
public enum MsgTypeEnum {
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
    @Getter
    private final String text;

    MsgTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
