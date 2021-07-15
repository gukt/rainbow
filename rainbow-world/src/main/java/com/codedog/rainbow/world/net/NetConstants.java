/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import io.netty.util.AttributeKey;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-21 23:37
 *
 * @author gukt <gukaitong@gmail.com>
 */
public class NetConstants {

    /**
     * SessionKey常量
     */
    public static final AttributeKey<Session> SESSION_KEY = AttributeKey.newInstance("session");
}
