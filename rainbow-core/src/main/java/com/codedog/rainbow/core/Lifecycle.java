/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core;

import java.time.Duration;

/**
 * 生命周期接口
 *
 * @author https://github.com/gukt
 */
public interface Lifecycle {

    /**
     * 启动服务
     *
     * @throws IllegalStateException 如果服务当前已经启动或正则启动中
     * @throws LifecycleException    如果被其他线程中断；或绑定端口失败（对于 TCP 服务来说）
     */
    void start();

    /**
     * 停止服务
     *
     * @throws LifecycleException if failure
     */
    void stop();

    /**
     * 获取服务的启动时间，单位：毫秒。
     */
    long getStartTime();

    Duration uptime();

    /**
     * 当前是否处于 {@link State#RUNNING RUNNING} 状态。
     *
     * @return 如果是，返回 <code>true</code>; 反之 <code>false</code>
     */
    default boolean isRunning() {
        return getState() == State.RUNNING;
    }

    /**
     * 返回生命周期 {@link State 状态}。
     */
    State getState();

    /**
     * 返回引起失败的 {@link Throwable 原因}。
     *
     * @return 返回引起失败的 {@link Throwable 原因}
     */
    Throwable getFailureCause();

    /**
     * 检查服务的当前状态是不是 {@link State#NEW NEW}。
     *
     * @throws IllegalStateException if {@link #getState()} is not {@link State#NEW NEW}
     */
    default void requireStateNew() {
        if (getState() != State.NEW) {
            throw new IllegalStateException("Already started");
        }
    }

    /**
     * 检查服务的当前状态是不是 {@link State#RUNNING RUNNING}。
     *
     * @throws IllegalStateException if {@link #getState()} is not {@link State#RUNNING RUNNING}
     */
    default void requireStateRunning() {
        if (getState() != State.RUNNING) {
            throw new IllegalStateException("Not running");
        }
    }

    /**
     * 添加 {@link Listener 监听器}。
     *
     * @param listener {@link Listener 监听器}
     * @return 当前对象自身
     * @throws NullPointerException 如果参数 listener 为 null
     */
    Lifecycle addListener(Listener listener);

    /**
     * 添加多个 {@link Listener 监听器}。
     *
     * @param listeners {@link Listener 监听器} 数组
     * @return 当前对象自身
     * @throws NullPointerException 如果参数 listener 为 null
     */
    Lifecycle addListener(Listener... listeners);

    /**
     * 用来表示状态的通用枚举。
     */
    enum State {

        /**
         * 未启动
         */
        NEW,

        /**
         * 正在启动中
         */
        STARTING,

        /**
         * 运行中
         */
        RUNNING,

        /**
         * 正在停止中
         */
        STOPPING,

        /**
         * 已终止
         */
        TERMINATED,

        /**
         * 失败
         */
        FAILED
    }

    /**
     * {@link Lifecycle 生命周期对象} 的 {@link State 状态} 变更监听器。
     */
    class Listener {

        /**
         * 当状态从 {@link State#NEW NEW} 变成 {@link State#STARTING STARTING} 时调用。
         */
        public void starting() { }

        /**
         * 当状态从 {@link State#STARTING STARTING} 变成 {@link State#RUNNING RUNNING} 时调用。
         */
        public void running() { }

        /**
         * 当状态变成 {@link State#STOPPING STOPPING} 时调用，变化之前的状态可能是 STARTING、RUNNING
         *
         * @param from 变化之前的状态
         */
        public void stopping(State from) { }

        /**
         * 当状态变成 {@link State#TERMINATED TERMINATED} 时调用。TERMINATED 是最终状态。
         *
         * @param from 变化之前的状态
         */
        public void terminated(State from) { }

        /**
         * 当发生异常时调用。
         *
         * @param from    变化之前的状态
         * @param failure 导致失败的原因
         */
        public void failed(State from, Throwable failure) { }
    }
}
