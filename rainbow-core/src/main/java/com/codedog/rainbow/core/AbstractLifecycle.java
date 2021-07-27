/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core;

import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * AbstractLifecycle
 *
 * @author https://github.com/gukt
 */
public abstract class AbstractLifecycle implements Lifecycle {

    private final List<Listener> listeners = new ArrayList<>();
    /**
     * 启动时间，单位：毫秒
     */
    @Getter protected long startTime;
    /**
     * 状态
     */
    @Getter protected volatile State state = State.NEW;
    /**
     * 失败原因
     */
    @Getter protected LifecycleException failureCause;

    @Override
    public void start() {
        requireStateNew();
        startTime = System.currentTimeMillis();
        setState(State.STARTING);
    }

    /**
     * 获得总运行时长。
     */
    public Duration uptime() {
        return Duration.ofMillis(System.currentTimeMillis() - startTime);
    }

    @Override
    public Lifecycle addListener(Listener listener) {
        Objects.requireNonNull(listener, "listener is null (expected: not null)");
        listeners.add(listener);
        return this;
    }

    @Override
    public Lifecycle addListener(Listener... listeners) {
        Collections.addAll(this.listeners, listeners);
        return this;
    }

    /**
     * 设置状态。当状态成功变更后，所有注册的 {@link Listener listeners} 会被逐一调用。
     *
     * @param state new {@link State State}
     */
    protected void setState(State state) {
        // TODO 加状态变更检查，不是所有状态之间都可以互转
        State from = this.state;
        this.state = state;

        listeners.forEach(e -> {
            if (state == State.STARTING) {
                e.starting();
            } else if (state == State.RUNNING) {
                e.running();
            } else if (state == State.STOPPING) {
                e.stopping(from);
            } else if (state == State.TERMINATED) {
                e.terminated(from);
            } else if (state == State.FAILED) {
                e.failed(from, failureCause);
            }
        });
    }

    /**
     * 创建并抛出 {@link LifecycleException } 异常。
     *
     * @param message 错误消息
     */
    protected void fail(String message) {
        fail(message, null);
    }

    /**
     * 创建并抛出 {@link LifecycleException } 异常。
     *
     * @param message 错误消息
     * @param cause   引起异常的 {@link Throwable 原因}
     */
    protected void fail(String message, Throwable cause) {
        failureCause = new LifecycleException(message, cause);
        setState(State.FAILED);
        throw failureCause;
    }
}
