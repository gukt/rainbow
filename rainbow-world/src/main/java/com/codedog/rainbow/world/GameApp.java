/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.core.concurrent.Once;
import com.codedog.rainbow.core.concurrent.WaitGroupWrapper;
import com.codedog.rainbow.support.AbstractLifecycle;
import com.codedog.rainbow.support.Lifecycle;
import com.codedog.rainbow.world.net.RpcServer;
import com.codedog.rainbow.world.net.TcpServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * 表示一个GameServer进程
 * GameApp可以不依赖Spring独立启动
 * // TODO Add RpcServer, 用以接受RPC请求
 * // TODO Add HttpServer
 *
 * @author https://github.com/gukt
 */
@Slf4j
@Component
public class GameApp extends AbstractLifecycle implements Lifecycle {

    @Getter
    private final GameOptions opts;
    private final EventPublisher eventPublisher;
    /**
     * 退出标记，启动过程可能会启动多个服务，比如：TcpServer, HttpServer等
     * 只有当所有服务都启动完成没有出错才表示GameApp启动成功，如果任何一个服务启动失败，
     * 则立即退出并执行一次退出函数
     */
    private final CountDownLatch exit = new CountDownLatch(1);
    /**
     * 用以控制退出函数仅运行一次，因为依赖服务是并行启动的，
     * 启动过程中的退出可能存在多线程竞争
     */
    private final Once once = new Once();
    private final WaitGroupWrapper waitGroup = new WaitGroupWrapper();
    @Setter
    private TcpServer tcpServer;
    @Setter
    private RpcServer rpcServer;

    public GameApp(GameOptions opts, EventPublisher eventPublisher) {
        this.opts = opts;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void start() {
        requireNew();

        log.info("Starting GameApp.");
        startTime = System.currentTimeMillis();
        setState(State.STARTING);

        // 初始化context对象，并设置相关属性
        GameContext context = new GameContext();
        context.setApp(this);
        context.setOptions(opts);

        // 启动必须的服务，服务可以通过配置项禁止
        // 也可以通过不设置相关属性禁止，这在测试时非常有用
        startTcpServerIfEnabled();
        startRpcServerIfEnabled();

        // add shutdown hook
        addShutdownHook();

        // transit to running
        setState(State.RUNNING);
        log.info("Started GameApp.");

        // waiting for quit signal
        try {
            exit.await();
        } catch (InterruptedException e) {
            log.error("GameApp was terminated.");
        } finally {
            // stop application
            stop();
        }
    }

    /**
     * 获得App运行总在线时长，单位：毫秒
     */
    @SuppressWarnings("unused")
    public long uptime() {
        return System.currentTimeMillis() - startTime;
    }

    private void startTcpServerIfEnabled() {
        if (opts.isTcpServerEnabled() && tcpServer != null) {
            waitGroup.run(() -> {
                try {
                    // 启动并阻塞，直到有错误发生
                    tcpServer.start();
                } catch (Exception e) {
                    tcpServer.stop();
                    exit(e);
                }
            }, opts.getTcpServerBootstrapThreadPattern());
        }
    }

    private void startRpcServerIfEnabled() {
        if (opts.isRpcServerEnabled() && rpcServer != null) {
            waitGroup.run(() -> {
                try {
                    // 启动并阻塞，直到有错误发生
                    rpcServer.start();
                } catch (Exception e) {
                    exit(e);
                }
            }, opts.getRpcServerBootstrapThreadPattern());
        }
    }

    @Override
    public void stop() {
        log.info("Stopping GameApp...");
        setState(State.STOPPING);

        // 将eventPublisher停止掉
        // TODO 这里会有个问题，异步事件线程中不能再派发异步线程。否则异步事件处理线程池理论上存在将可能永远关不掉的情况
        if (eventPublisher != null) {
            eventPublisher.stop();
        }

        // 等待系统所有启动的依赖组件都停止运行
        waitGroup.await();

        setState(State.TERMINATED);
        log.info("GameApp stopped.");
    }

    private void addShutdownHook() {
        log.info("Adding shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("将要关闭应用程序,因为JVM正在被关闭");
                System.err.println("*** 将要关闭应用程序,因为JVM正在被关闭 ***");
                stop();
            } catch (Exception e) {
                log.error("关闭服务器时发生异常: ", e);
            }
        }, opts.getShutdownHookThreadPattern()));
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
