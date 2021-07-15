/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net;

import lombok.Getter;
import lombok.ToString;

/**
 * @author https://github.com/gukt
 */
@ToString
public class SessionClosingEvent {

    @Getter
    private final Session session;

    public SessionClosingEvent(Session session) {
        this.session = session;
    }
}
