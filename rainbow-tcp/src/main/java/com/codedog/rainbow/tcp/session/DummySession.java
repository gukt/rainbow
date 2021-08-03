/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.util.PeerInfo;
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
public class DummySession extends AbstractSession {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public DummySession(TcpProperties properties) {
        super(properties);
        this.peerInfo = PeerInfo.of(new InetSocketAddress(0));
        this.id = "Dummy Session - " + NEXT_ID.incrementAndGet();
    }

    @Override
    protected Object beforeWrite(Object message) {
        return message;
    }

    @Override
    public CompletableFuture<Session> write(Object message, boolean flush) {
        message = beforeWrite(message);
        if (message != null) {
            log.debug("TCP - Writing: {} -> {}", message, this);
        }
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<Void> close() {
        closeTimeMillis = System.currentTimeMillis();
        return CompletableFuture.completedFuture(null);
    }
}
