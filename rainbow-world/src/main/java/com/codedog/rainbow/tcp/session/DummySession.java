/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import com.codedog.rainbow.tcp.util.PeerInfo;
import com.codedog.rainbow.world.config.TcpProperties.SessionProperties;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 该对象是用来方便测试的。
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class DummySession extends AbstractSession {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public DummySession(SessionProperties properties) {
        super(properties);
        this.peerInfo = PeerInfo.builder().remoteAddress(new InetSocketAddress(0)).build();
        this.id = "Dummy Session - " + NEXT_ID.incrementAndGet();
    }

    @Override
    public CompletableFuture<Void> write(Object message, boolean flush) {
        if (message != null) {
            log.debug("TCP - Writing: {} -> {}", message, this);
        }
        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> close() {
        closeTimeMillis = System.currentTimeMillis();
        return new CompletableFuture<>();
    }
}
