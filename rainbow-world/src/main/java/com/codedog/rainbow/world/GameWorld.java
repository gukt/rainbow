/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.core.concurrent.Once;
import com.codedog.rainbow.core.concurrent.WaitGroupWrapper;
import com.codedog.rainbow.core.AbstractLifecycle;
import com.codedog.rainbow.tcp.RpcServer;
import com.codedog.rainbow.tcp.TcpServer;
import com.codedog.rainbow.world.config.AppProperties;
import com.codedog.rainbow.world.config.TcpProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * 表示一个游戏服务器进程，它可以不依赖 Spring 独立启动
 *
 * TODO Add RpcServer, 用以接受RPC请求
 * TODO Add HttpServer
 *
 * @author https://github.com/gukt
 */
@Component
@Slf4j
public class GameWorld extends AbstractLifecycle {

    /**
     * 退出标记，启动过程可能会启动多个服务，比如 TcpServer、HttpServer 等
     * 只有当所有服务都启动完成没有出错，才表示启动成功
     * 如果任何一个服务启动失败，则立即退出，并执行一次退出函数。
     */
    private final CountDownLatch exit = new CountDownLatch(1);
    /**
     * 用以控制退出函数仅运行一次，因为依赖服务是并行启动的，启动过程中的退出可能存在多线程竞争。
     */
    private final Once once = new Once();
    private final WaitGroupWrapper waitGroup = new WaitGroupWrapper();
    private final AppProperties appProperties;
    private final TcpProperties tcpProperties;
    @Setter
    private TcpServer tcpServer;
    @Setter
    private RpcServer rpcServer;


    public GameWorld(AppProperties appProperties,  TcpProperties tcpProperties) {
        this.appProperties = appProperties;
        this.tcpProperties = tcpProperties;
    }

    @Override
    public void start() {
        requireStateNew();

        log.info("Starting the game world...");
        startTime = System.currentTimeMillis();
        setState(State.STARTING);

        // 初始化 GameContext 对象，并设置相关配置属性
        GameContext context = new GameContext();
        context.setApp(this);

        // 启动必须的服务，服务可以通过配置项禁止
        // 也可以通过不设置相关属性禁止，这在测试时非常有用
        startTcpServerIfEnabled();
//        startRpcServerIfEnabled();

        addShutdownHook();
        setState(State.RUNNING);
        log.info("Started GameApp.");

        // Waiting for a quit signal
        try {
            exit.await();
        } catch (InterruptedException e) {
            log.error("GameWorld was terminated.");
        } finally {
            stop();
        }
    }

    /**
     * 获得总运行时长，单位：毫秒
     */
    @SuppressWarnings("unused")
    public long uptime() {
        return System.currentTimeMillis() - startTime;
    }

    private void startTcpServerIfEnabled() {
        if (appProperties.isTcpServerEnabled() && tcpServer != null) {
            waitGroup.run(() -> {
                try {
                    // 启动并阻塞，直到有错误发生
                    tcpServer.start();
                } catch (Exception e) {
                    tcpServer.stop();
                    exit(e);
                }
            }, tcpProperties.getBootstrapThreadName());
        }
    }

//    private void startRpcServerIfEnabled() {
//        if (opts.getRpc().isEnabled() && rpcServer != null) {
//            waitGroup.run(() -> {
//                try {
//                    // 启动并阻塞，直到有错误发生
//                    rpcServer.start();
//                } catch (Exception e) {
//                    exit(e);
//                }
//            }, tcpProperties.getrp().getBootstrapThreadName());
//        }
//    }

    @Override
    public void stop() {
        log.info("Stopping the game world...");
        setState(State.STOPPING);

//        // Stop the event publisher
//        // TODO 这里会有个问题，异步事件线程中不能再派发异步线程。否则异步事件处理线程池理论上存在将可能永远关不掉的情况
//        if (eventPublisher != null) {
//            eventPublisher.stop();
//        }
        // Waiting for all starting/started services to stop
        waitGroup.await();
        // Set as terminated
        setState(State.TERMINATED);
        log.info("Stopped the game world!");
    }

    private void addShutdownHook() {
        log.info("Adding a shutdown hook...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("将要关闭应用程序，因为 JVM 正在被关闭");
                System.err.println("*** 将要关闭应用程序，因为 JVM 正在被关闭 ***");
                stop();
            } catch (Exception e) {
                log.error("关闭服务器时发生异常", e);
            }
        }, appProperties.getShutdownHookThreadPattern()));
    }

    private void exit(Throwable e) {
        once.run(() -> {
            if (e != null) {
                log.error("系统服务异常，即将退出", e);
            }
            exit.countDown();
        });
    }
}
