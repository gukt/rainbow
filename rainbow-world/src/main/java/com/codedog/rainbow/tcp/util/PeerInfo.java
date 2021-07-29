/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.net.InetSocketAddress;

/**
 * TODO 今后视情况而定，如果没有几个参数就不要使用 builder 模式了
 *
 * @author https://github.com/gukt
 */
@Data
@ToString
@Builder
public class PeerInfo {

    private InetSocketAddress remoteAddress;
    private long next;
}
