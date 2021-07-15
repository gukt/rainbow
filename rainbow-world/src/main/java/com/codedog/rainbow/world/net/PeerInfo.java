/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.net.InetSocketAddress;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Data
@ToString
@Builder
public class PeerInfo {

    private InetSocketAddress remoteAddress;
    private long next;
}
