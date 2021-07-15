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
     * 启动有生命周期的服务
     *
     * @throws IllegalStateException if already started
     * @throws LifecycleException    if interrupted or unable to bind
     */
    void start();

    /**
     * 停止有生命周期的服务
     *
     * @throws LifecycleException if failure
     */
    void stop();

    /**
     * 获取服务器启动的时间
     */
    long getStartTime();

    /**
     * 检查当前是否处于RUNNING状态
     *
     * @return 如果当前状态为RUNNING返回true，反之false
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
     * 判断当前状态如果不是{@linkplain State#NEW}则抛出{@link IllegalStateException}
     */
    default void requireNew() {
        if (getState() != State.NEW) {
            throw new IllegalStateException("Already started");
        }
    }

    /**
     * 判断如果当前状态不是{@linkplain State#RUNNING}则抛出{@link IllegalStateException}
     */
    default void requireRunning() {
        if (getState() != State.RUNNING) {
            throw new IllegalStateException("Not running");
        }
    }

    /**
     * 添加生命周期监听器，可以连续添加多个
     *
     * @param listener 监听器
     * @return this
     */
    Lifecycle addListener(Listener listener);

    enum State {
        /**
         * 未启动状态
         */
        NEW,

        /**
         * 正在启动中状态
         */
        STARTING,

        /**
         * 运行中状态
         */
        RUNNING,

        /**
         * 正在停止中状态
         */
        STOPPING,

        /**
         * 已终止状态
         */
        TERMINATED,

        /**
         * 失败状态
         */
        FAILED
    }

    class Listener {

        /**
         * 当状态从NEW变成STARTING时调用
         */
        public void starting() {
        }

        /**
         * 当状态从STARTING变成RUNNING时调用
         */
        public void running() {
        }

        /**
         * 当状态变成STOPPING时调用，变化之前的状态可能是STARTING、RUNNING
         * from参数表示变化之前的状态
         *
         * @param from 变化之前的状态
         */
        public void stopping(State from) {
        }

        /**
         * 当状态变成TERMINATED时调用，TERMINATED是什么周期的最终状态
         *
         * @param from 变化之前的状态
         */
        public void terminated(State from) {
        }

        /**
         * 当状态变化为FAILURE时调用，
         *
         * @param from    变化之前的状态
         * @param failure 导致失败的异常
         */
        public void failed(State from, Throwable failure) {
        }
    }
}
