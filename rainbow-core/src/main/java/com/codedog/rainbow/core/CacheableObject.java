/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * CacheableObject class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public abstract class CacheableObject {

    static final Map<Class<?>, Set<Serializable>> deletedIdsByType = new HashMap<>();
    private State state = State.PERSISTENT;
    private final Object[] transitionLock = {};

    public abstract Serializable getId();

    public void transitionState(State newState) {
        final State old = state;
        synchronized (transitionLock) {
            if (state == State.NEW && newState == State.NEW) {
                fail(newState);
                return;
            }
            if (state == State.PERSISTENT && newState == State.NEW) {
                fail(newState);
                return;
            }
            if (state == State.UPDATE && newState == State.NEW) {
                fail(newState);
                return;
            }
            if (state == State.DELETE) {
                fail(newState);
                return;
            }
            state = newState;
            if (newState == State.DELETE) {
                deletedIdsByType.putIfAbsent(getClass(), new HashSet<>());
                deletedIdsByType.get(getClass()).add(getId());
            }
        }
        boolean changed = old != newState;
        log.info("Cache - State transient: {}>{} ({}) ({}#{})",
                old, newState,
                changed ? "changed" : "unchanged",
                getClass().getSimpleName(), getId());
    }

    private void fail(State newState) {
        log.error("Cache - Failed to transient state: {}>{}", state, newState);
    }

    public enum State {
        NEW,
        PERSISTENT,
        UPDATE,
        DELETE
    }
}
