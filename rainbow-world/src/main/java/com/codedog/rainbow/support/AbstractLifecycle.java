/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.support;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractLifeCycleAware
 * TODO 换成列表，可添加多个listener
 *
 * @author https://github.com/gukt
 */
public abstract class AbstractLifecycle implements Lifecycle {

    private final List<Listener> listeners = new ArrayList<>();
    @Getter
    protected long startTime;
    @Getter
    protected volatile Lifecycle.State state = State.NEW;
    @Getter
    protected LifecycleException failureCause;

    @Override
    public Lifecycle addListener(Listener listener) {
        listeners.add(listener);
        return this;
    }

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

    protected void fail(String message, Throwable cause) {
        failureCause = new LifecycleException(message, cause);
        setState(State.FAILED);
        throw failureCause;
    }
}
