/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.world.net.json.JsonPacket;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-08-04 12:42
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class MessageResolver {

    public static int resolveSeq(Object message) {
        if (message instanceof JsonPacket) {
            return ((JsonPacket) message).getSeq();
        }
        throw new RuntimeException("Cannot resolve seq property");
    }
}
