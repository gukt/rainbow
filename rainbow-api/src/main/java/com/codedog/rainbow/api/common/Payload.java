/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.common;

import java.util.HashMap;
import java.util.Objects;

/**
 * Payload class
 *
 * @author https://github.com/gukt
 */
public class Payload extends HashMap<Object, Object> {

    public static Payload of() {
        return new Payload();
    }

    public static Payload of(Object... pairs) {
        Objects.requireNonNull(pairs);
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Number of pairs must be even, got:" + pairs.length);
        }
        Object key, value;
        Payload payload = new Payload();
        for (int i = 0; i < pairs.length; i += 2) {
            key = pairs[i];
            // ignores null key
            if (key != null) {
                value = pairs[i + 1];
                payload.put(key, value);
            }
        }
        return payload;
    }
}
