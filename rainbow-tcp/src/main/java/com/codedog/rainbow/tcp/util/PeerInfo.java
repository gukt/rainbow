/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.util.Assert;
import lombok.ToString;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 客户端信息
 *
 * @author https://github.com/gukt
 */
@ToString
public class PeerInfo {

    private InetSocketAddress address;

    /**
     * 创建一个 PeerInfo 实例。
     *
     * @param address 客户端 IP 地址，不能为 null
     * @return PeerInfo 对象
     */
    public static PeerInfo of(InetSocketAddress address) {
        Assert.notNull(address, "address");
        PeerInfo instance = new PeerInfo();
        instance.address = address;
        return instance;
    }

    /**
     * 创建一个 PeerInfo 实例。
     *
     * @param address 客户端 IP 地址，不能为 null
     * @return PeerInfo 对象
     */
    public static <T extends SocketAddress> PeerInfo of(T address) {
        return of((InetSocketAddress) address);
    }

    /**
     * 获得客户端地址的字符串表示形式。
     *
     * @return 地址字符串
     */
    public String getAddressString() {
        return address.getHostString();
    }
}
