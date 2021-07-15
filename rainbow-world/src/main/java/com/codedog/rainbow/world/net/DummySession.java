/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
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
