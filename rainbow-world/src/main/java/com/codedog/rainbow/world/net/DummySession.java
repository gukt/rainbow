/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public final class DummySession extends AbstractSession {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public DummySession() {
        super(10, 0);
        this.peerInfo = PeerInfo.builder().remoteAddress(new InetSocketAddress(0)).build();
        this.id = "dummy-" + NEXT_ID.incrementAndGet();
    }

    @Override
    public CompletableFuture<Void> write(Object message, boolean flush) {
        if (message != null) {
            log.debug("TCP: Writing: {} -> {}", message, this);
        }
        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> close() {
        closeTimeMillis = System.currentTimeMillis();
        return new CompletableFuture<>();
    }
}
