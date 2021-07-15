/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-22 22:21
 *
 * @author gukt <gukaitong@gmail.com>
 */
@ToString
public class SessionClosingEvent {

    @Getter
    private Session session;

    public SessionClosingEvent(Session session) {
        this.session = session;
    }
}
