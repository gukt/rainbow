/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.util.Assert;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 *
 * @param <T> 分发的消息的类型
 * @author https://github.com/gukt
 */
@Slf4j
public abstract class AbstractMessageDispatcher<T> implements MessageDispatcher {

    /**
     * Tcp server properties
     */
    protected final TcpProperties properties;
    /**
     * 业务处理线程池
     */
    protected final ThreadPoolExecutor executor;
    /**
     * 用以遍历当前所有在线 Session 中待处理请求，分发给业务处理线程池处理。
     */
    private final MessagePumper pumper;

    /**
     * 保存 '消息类型 -> MessageHandler' 的映射
     */
    private final Map<Serializable, MessageHandler<?>> handlersMap = new HashMap<>(16);

    public AbstractMessageDispatcher(TcpProperties properties) {
        Assert.notNull(properties, "properties");
        this.properties = properties;
        this.pumper = new MessagePumper();

        // TODO 将bizExec通过外部传入？
        // 自定义业务线程池对象

        // 初始化业务处理线程池，并提供RejectedExceptionHandler,
        // 当业务处理线程池忙，无法处理更多的请求时，新提交的任务会被rejected，
        // 此时暂停pumper线程一会以等待业务处理线程池有空闲的线程处理任务
        // 通过pumper线程暂停/唤醒机制，可动态控制生产速率，协调生产者消费者速率不一致的矛盾
        // 暂停期间，从客户端角度看：
        // 如果最后发送的是一个同步等待请求，由于该请求被拒绝，会一只存在于SessionStore中等待下次被调度
        // 此时，客户端由于一只没有等待到请求的响应，可以继续等待或做超时处理，
        // 如果等待期间，服务器的业务处理线程池迅速处理了请求，则满足预期，只是客户端看到的表现是响应比较慢，但合理（因为服务器忙）
        // 如果等待超时，客户端可尝试自动重试机制，继续发送刚刚处理超时的请求（注意：
        TcpProperties.ExecutorProperties bizExecProperties = properties.getExecutor();
        this.executor = new ThreadPoolExecutor(
                bizExecProperties.getCorePoolSize(),
                bizExecProperties.getMaxPoolSize(),
                bizExecProperties.getKeepAliveTimeoutSeconds(), TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(bizExecProperties.getQueueCapacity()),
                // new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat(bizExecProperties.getThreadPattern()).build(),
                (r, executor) -> pumper.onRequestRejected(r)
        );
    }

    @Override
    public void start() {
        log.info("TCP - Starting message dispatcher");
        pumper.start();
        log.info("TCP - Started the message dispatcher.");
    }

    @Override
    public void stop() {
        // 关闭消息泵, 不再往业务线程池派发请求
        // 每条连接积压的请求不再处理
        log.info("TCP - Stopping message dispatcher");
        pumper.stop();
        log.info("TCP - Stopped the message dispatcher");

        // 关闭业务处理线程池
        log.info("TCP - Shutting down the executor of dispatcher");
        executor.shutdown();
        try {
            // 等待线程池现有任务全部执行完成
            if (!executor.awaitTermination(properties.getWaitTerminationTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                log.info("TCP - 等待业务处理线程池关闭超时，将强制关闭！");
                // 如果等待超时，强制关闭
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // 如果当前关闭线程收到中断请求，也强制关闭业务处理线程池
            executor.shutdownNow();
        }
        log.info("TCP - 业务处理线程池已关闭.");
    }

    @Override
    public final void registerHandler(MessageHandler<?> handler) {
        if (handlersMap.containsKey(handler.getType())) {
            throw new RuntimeException("Could not register the message handler, key exists: " + handler.getType());
        }
        handlersMap.put(handler.getType(), handler);
    }

    /**
     * 根据消息类型找到可以处理该条消息的MessageHandler,如果找不到返回null。
     * 这里使用延迟方式获取MessageHandler，好处是启动进程速度会加快，但暴露问题也会滞后
     *
     * @param type 消息类型
     * @return 返回type对应的MessageHandler对象，如果找不到返回null
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected <V> MessageHandler<V> getHandlerByType(String type) {
        return (MessageHandler<V>) this.handlersMap.get(type);
    }

    /**
     * 打印慢处理日志（如果处理时间超出 {@link TcpProperties#getSlowProcessingThreshold()} 设定的阈值）。
     *
     * @param message   处理中的消息
     * @param startTime 开始时间，单位：毫秒
     */
    private void writeLogIfSlow(T message, long startTime) {
        if (startTime <= 0) {
            return;
        }
        long duration = System.currentTimeMillis() - startTime;
        if (duration > properties.getSlowProcessingThreshold()) {
            log.warn("TCP - Found a slow process, it takes {} millis, {}", duration, message);
        }
    }

    /**
     * 分发指定 {@link Session 客户端连接} 的请求（分发消息）。
     *
     * @param session 表示客户端连接的对象
     * @param request 待处理的消息
     * @throws IllegalArgumentException 如果 <code>session</code> 或 <code>request</code> 为 null
     */
    protected abstract void dispatch0(Session session, T request);

    protected void postHandle(Session session, T request, long startTime) {
        writeLogIfSlow(request, startTime);
        session.completeRequest();
    }

    protected static class RequestTask<T> implements Runnable {

        private final Session session;
        private final T request;

        protected RequestTask(Session session, T request) {
            this.session = session;
            this.request = request;
        }

        @Override
        public void run() {
            throw new NotImplementedException();
        }
    }

    /**
     * 消息泵，用于不断提供就绪的请求，
     * 内部运行一个无限循环，用于遍历当前所有在线连接上的backlog请求，如果某连接当前有请求未处理完，
     * 则pumper不会继续提供该连接上的新请求，直到上一个请求被处理完。
     * 此机制保证了单个session请求的串行执行，单条连接上请求的串行执行机制有很多好处，同时可让我们避免掉很多并发问题，简化了编程
     * 如果请求的消费者来不及消费请求，MessagePumper会自动协调生产者-消费者速率不匹配的矛盾。
     */
    class MessagePumper {

        /**
         * 消息Pumping线程池，该线程池中只有一个工作线程
         */
        private final ThreadPoolExecutor pumpExec;
        /**
         * 暂停到未来的某个时间点, 单位: Millis
         */
        private long pauseUntil;
        /**
         * 如果小于此字段值就是用自旋代替等待，单位:毫秒
         */
        private final long spinForTimeoutThreshold = 1000L;

        MessagePumper() {
            // 初始化消息泵线程，只需要一个线程，内部运行一个可暂停可中断的无限循环任务
            this.pumpExec = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(1),
                    r -> new Thread(r, properties.getPumperExecThreadPattern())
            );
        }

        void onRequestRejected(Runnable r) {
            try {
                // pause message pumper for a while
                pumper.pause(properties.getPumperWaitMillisOnRejected());

                // 更新processing状态，以便下一轮循环可以被调度
                RequestTask<?> task = (RequestTask<?>) r;
                task.session.setProcessingRequest(null);
                log.info("TCP - Task rejected by biz executor: {}, session:{}", task.request, task.session);
            } catch (Exception e) {
                // TODO refines the log
                log.error("exception:", e);
            }
        }

        private boolean needPause() {
            return System.currentTimeMillis() < pauseUntil;
        }

        /**
         * 暂停指定的毫秒数，该方法只会由pumper线程调用，当提及任务无法被线程池接受时触发
         * 因此，当线程池已经无法接受更多任务时，此时若连续提交新任务，则会连续调用该方法
         */
        private void pause(long timeoutMillis) {
            this.pauseUntil = System.currentTimeMillis() + timeoutMillis;
        }

        /**
         * 启动MessagePumper，实际上是提交一个无限循环任务，对当前所有在线玩家接收消息队列进行遍历轮询
         * 将轮询得到的消息交由"业务处理线程池"去处理
         * 循环内部也可以响应暂停和中断
         * 如果外部设置了暂停，则pumping工作线程会等待指定的暂停时间后再继续遍历轮询
         */
        private void start() {
            log.info("TCP - Starting message pumper.");
            pumpExec.execute(() -> {
                try {
                    while (!Thread.interrupted()) {
                        if (needPause()) {
                            try {
                                long timeout = pauseUntil - System.currentTimeMillis();
                                if (timeout > spinForTimeoutThreshold) {
                                    log.info("TCP - Message pumper was suspended for {} millis", timeout);
                                    Thread.sleep(timeout);
                                }
                                log.info("TCP - Message pumper awake.");
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                        for (Session session : TcpServer.getSessions()) {
                            if (session.isProcessing() || session.isClosed()) {
                                continue;
                            }
                            // 检查是否有就绪的请求，如果有，取出交由线程池去处理，
                            // 因为线程池可能会拒绝该任务的执行，所以先使用peek读取请求但并不将元素出列
                            @SuppressWarnings("unchecked")
                            T request = (T) session.getStore().getPendingRequests().peek();
                            if (request != null) {
                                dispatch0(session, request);
                                // doDispatch(request, s);
                                // doDispatch(request, s);
                            }
                            // 如果上面提交的任务失败，则立即跳出循环，因为此时若连续提交新任务会连续失败
                            if (needPause()) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("TCP - Message pumping loop error: ", e);
                    // TODO 停止服务还是退出系统？
                } finally {
                    log.error("TCP - Message pumping loop terminated.");
                }
            });
            log.info("TCP - Started message pumper.");
        }

        private void stop() {
            log.info("TCP - Stopping message pumper.");
            pumpExec.shutdownNow();
            try {
                log.info("TCP - Waiting pumping loop terminated.");
                // TODO magic number
                if (!pumpExec.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("TCP - Waiting for pumping loop terminated timeout");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("TCP - Message pumper terminated.");
        }
    }
}
