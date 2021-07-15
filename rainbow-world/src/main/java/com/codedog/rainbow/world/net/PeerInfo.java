/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.net.InetSocketAddress;

/**
 * @author https://github.com/gukt
 */
@Data
@ToString
@Builder
public class PeerInfo {

    private InetSocketAddress remoteAddress;
    private long next;
}
