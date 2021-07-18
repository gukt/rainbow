/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.support;

/**
 * 生命周期接口
 *
 * @author https://github.com/gukt
 */
public interface Lifecycle {

    /**
     * 启动服务
     *
     * @throws IllegalStateException if already started
     * @throws LifecycleException    if interrupted or unable to bind
     */
    void start();

    /**
     * 停止服务
     *
     * @throws LifecycleException if failure
     */
    void stop();

    /**
     * 获取服务的启动时间
     */
    long getStartTime();

    /**
     * 当前是否处于 RUNNING 状态
     *
     * @return true if running，otherwise false.
     */
    default boolean isRunning() {
        return getState() == State.RUNNING;
    }

    /**
     * 返回生命周期状态
     */
    State getState();

    /**
     * 引起失败的原因
     *
     * @return 返回引起失败的原因
     */
    Throwable getFailureCause();

    /**
     * 检查服务的当前状态是不是 {@link State#NEW}
     *
     * @throws IllegalStateException if {@link #getState()} is not {@link State#NEW}
     */
    default void requireStateNew() {
        if (getState() != State.NEW) {
            throw new IllegalStateException("Already started");
        }
    }

    /**
     * 检查服务的当前状态是不是 {@link State#RUNNING}
     *
     * @throws IllegalStateException if {@link #getState()} is not {@link State#RUNNING}
     */
    default void requireStateRunning() {
        if (getState() != State.RUNNING) {
            throw new IllegalStateException("Not running");
        }
    }

    /**
     * 添加生命周期监听器，可以连续添加多个
     *
     * @param listener 监听器
     * @return {@linkplain Lifecycle this}
     */
    Lifecycle addListener(Listener listener);

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

    class Listener {

        /**
         * 当状态从 NEW 变成 STARTING 时调用
         */
        public void starting() {
        }

        /**
         * 当状态从 STARTING 变成 RUNNING 时调用
         */
        public void running() {
        }

        /**
         * 当状态变成 STOPPING 时调用，变化之前的状态可能是 STARTING、RUNNING
         * from 参数表示变化之前的状态
         *
         * @param from 变化之前的状态
         */
        public void stopping(State from) {
        }

        /**
         * 当状态变成 TERMINATED 时调用，TERMINATED 是最终状态
         *
         * @param from 变化之前的状态
         */
        public void terminated(State from) {
        }

        /**
         * 当状态变化为 FAILURE 时调用，
         *
         * @param from    变化之前的状态
         * @param failure 导致失败的异常
         */
        public void failed(State from, Throwable failure) {
        }
    }
}
