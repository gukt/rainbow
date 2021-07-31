/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.core.AbstractLifecycle;
import com.codedog.rainbow.core.concurrent.Once;
import com.codedog.rainbow.core.concurrent.WaitGroupWrapper;
import com.codedog.rainbow.world.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * 表示一个游戏服务器进程，它可以不依赖 Spring 独立启动
 *
 * @author https://github.com/gukt
 */
@Component
@Slf4j
public class GameWorld extends AbstractLifecycle {

    /**
     * 退出标记，启动过程可能会启动多个服务，比如 TcpServer、RpcServer 等。
     * <p>只有当所有服务都正常启动完成，才表示整个 GameWorld 启动成功。
     * <p>如果任何一个服务启动失败，则立即退出，并执行一次退出函数。
     */
    private final CountDownLatch exit = new CountDownLatch(1);
    /**
     * 用以控制退出函数仅运行一次，因为依赖服务是并行启动的，启动过程中的退出可能存在多线程竞争。
     */
    private final Once once = new Once();
    private final WaitGroupWrapper waitGroup = new WaitGroupWrapper();
    private final AppProperties properties;

    public GameWorld(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void start() {
        super.start();

        log.info("🚀 Starting GameWorld.");
        log.debug("{}", properties);

        // 初始化 GameContext 对象，并设置相关配置属性
//        GameWorldContext context = new GameWorldContext();
//        context.setApp(this);

        addShutdownHook();
        setState(State.RUNNING);
        log.info("Started GameWorld.");

        try {
            exit.await(); // 等待退出信号
        } catch (InterruptedException e) {
            log.error("GameWorld was terminated.");
        } finally {
            stop();
        }
    }

//    private void startTcpServerIfEnabled() {
//        if (appProperties.isTcpServerEnabled() && tcpServer != null) {
//            waitGroup.run(() -> {
//                try {
//                    // 启动并阻塞，直到有错误发生
//                    tcpServer.start();
//                } catch (Exception e) {
//                    tcpServer.stop();
//                    exit(e);
//                }
//            }, tcpProperties.getBootstrapThreadName());
//        }
//    }

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

    public WaitGroupWrapper getWaitGroup() {
        return waitGroup;
    }

    @Override
    public void stop() {
        log.info("Stopping GameWorld...");
        setState(State.STOPPING);
//        // Stop the event publisher
//        // TODO 这里会有个问题，异步事件线程中不能再派发异步线程。否则异步事件处理线程池理论上存在将可能永远关不掉的情况
//        if (eventPublisher != null) {
//            eventPublisher.stop();
//        }
        // 等待所有启动或正在启动的服务停止
        waitGroup.await();
        setState(State.TERMINATED);
        log.info("Stopped GameWorld! Bye!");
    }

    private void addShutdownHook() {
        log.info("Adding a shutdown hook.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("将要关闭应用程序，因为 JVM 正在被关闭");
                System.err.println("*** 将要关闭应用程序，因为 JVM 正在被关闭 ***");
                stop();
            } catch (Exception e) {
                log.error("关闭服务器时发生异常", e);
            }
        }, properties.getShutdownHookThreadPattern()));
    }

    public void exit(Throwable e) {
        once.run(() -> {
            if (e != null) {
                log.error("系统服务异常，即将退出", e);
            }
            exit.countDown();
        });
    }
}
