/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.support.AbstractLifecycle;
import com.codedog.rainbow.support.LifecycleException;
import com.codedog.rainbow.support.NetworkService;
import com.codedog.rainbow.world.GameOptions;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RpcServer
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class RpcServer extends AbstractLifecycle implements NetworkService {

    private final GameOptions opts;
    private final List<BindableService> services = new ArrayList<>();
    private Server grpcServer;
    private int realPort = -1;

    public RpcServer(GameOptions opts) {
        this.opts = opts;
    }

    public void registerService(BindableService service) {
        services.add(service);
    }

    /**
     * 启动RpcServer,如果启动过程没有异常，则该方法会阻塞直到被中断
     *
     * @throws IllegalStateException if already started
     * @throws LifecycleException    if interrupted or unable to bind
     */
    @Override
    public void start() {
        requireNew();

        log.info("RPC: Starting RpcServer");
        startTime = System.currentTimeMillis();
        setState(State.STARTING);

        ServerBuilder builder = ServerBuilder.forPort(opts.getRpcPort());
        if (services.isEmpty()) {
            log.warn("RPC: No any services provided, double check please.");
        } else {
            services.forEach(builder::addService);
        }
        try {
            grpcServer = builder.build().start();
        } catch (IOException e) {
            fail("Unable to bind", e);
        }
        realPort = grpcServer.getPort();

        setState(State.RUNNING);
        log.info("RPC: Started RpcServer, listening on {}", realPort);

        try {
            // 阻塞，直到被中断
            grpcServer.awaitTermination();
        } catch (InterruptedException e) {
            fail("RpcServer was Interrupted", e);
        }
    }

    @Override
    public void stop() {
        requireRunning();

        log.info("RPC: Stopping RpcServer");
        setState(State.STOPPING);

        grpcServer.shutdown();

        setState(State.TERMINATED);
        log.info("RPC: RpcServer terminated.");
    }

    @Override
    public int getRealPort() {
        requireRunning();
        return realPort;
    }
}
