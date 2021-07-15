/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.world.PayloadDataAccessException;
import com.codedog.rainbow.world.net.json.JsonPacket;

import java.util.Map;

/**
 * @author https://github.com/gukt
 */
public class Payloads {

    public static int intValue(JsonPacket packet, String key) {
        return Integer.parseInt(getValue(packet, key).toString());
    }

    public static long longValue(JsonPacket packet, String key) {
        return Long.parseLong(getValue(packet, key).toString());
    }

    private static Object getValue(JsonPacket packet, String key) {
        Map<String, Object> payload = packet.getPayload();
        Object value = payload.get(key);
        if (value == null) {
            throw new PayloadDataAccessException("key not found: " + key);
        }
        return value;
    }

    public static String textValue(JsonPacket packet, String key) {
        return getValue(packet, key).toString();
    }
}
