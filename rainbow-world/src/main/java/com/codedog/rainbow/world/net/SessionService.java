/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.session.Session.State;
import com.codedog.rainbow.tcp.util.SessionUtils;
import com.codedog.rainbow.util.Assert;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Session Service
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
public class SessionService {

    /**
     * 用以保存所有 “roleId -> {@link Session}” 的映射
     * TODO 有没有必要同时多维护一个集合？
     */
    private static final Map<Serializable, Session> SESSIONS_BY_ROLE_ID = new ConcurrentHashMap<>(16);
    /**
     * 用以保存所有 {@link Session} 的集合。
     */
    private static final List<Session> SESSIONS = new CopyOnWriteArrayList<>();

    /**
     * 获取当前所有 {@link Session} 的集合（即包含：{@link State} 中定义的所有状态的 Session）。
     *
     * @return 当前所有 {@link Session} 的集合（即包含：{@link State} 中定义的所有状态的 Session）
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getSessions() {
        return SESSIONS;
    }

    /**
     * 获取当前处于某 {@link State 状态} 的所有 {@link Session} 的集合。
     *
     * @param state {@link State 状态}
     * @return 当前处于某 {@link State 状态} 的所有 {@link Session} 的集合
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getSessions(State state) {
        return null;
    }

    /**
     * 获取当前处于 {@link State#ACTIVE ACTIVE} 状态的所有 {@link Session} 的集合。
     *
     * @return 当前处于 {@link State#ACTIVE ACTIVE} 状态的所有 {@link Session} 的集合
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getActiveSessions() {
        return getSessions(State.ACTIVE);
    }

    /**
     * 获取当前处于某 {@link State#DISCONNECTED DISCONNECTED} 状态的所有 {@link Session} 个数。
     *
     * @return 当前处于某 {@link State#DISCONNECTED DISCONNECTED} 状态的所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getDisconnectedSessions() {
        return getSessions(State.DISCONNECTED);
    }

    // getSessionCount

    /**
     * 获取当前所有 {@link Session} 的个数（即：{@link State} 中定义的所有状态的 Session）。
     * <p>如果你希望获取处于某个 {@link State 状态} 的 Session 个数，请使用 {@link #getSessionCount(State)}
     *
     * @return 当前所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getSessionCount() {
        return SESSIONS.size();
    }

    /**
     * 获取当前处于某 {@link State 状态} 的所有 {@link Session} 个数。
     * <p>如果你希望获取所有 {@link State 状态} 的 Session 个数，请使用 {@link #getSessionCount()}
     *
     * @param state {@link State 状态}，不能为 null
     * @return 当前所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getSessionCount(State state) {
        Assert.notNull(state, "state");
        switch (state) {
            case ACTIVE:
                return SESSIONS.stream().filter(Session::isActive).count();
            case DISCONNECTED:
                return SESSIONS.stream().filter(Session::isDisconnected).count();
        }
        return 0;
    }

    /**
     * 获取当前处于 {@link State#ACTIVE ACTIVE} 状态的所有 {@link Session} 个数。
     *
     * @return 当前处于 {@link State#ACTIVE ACTIVE} 状态的所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getActiveSessionCount() {
        return getSessionCount(State.ACTIVE);
    }

    /**
     * 获取当前处于某 {@link State#DISCONNECTED DISCONNECTED} 状态的所有 {@link Session} 个数。
     *
     * @return 当前处于某 {@link State#DISCONNECTED DISCONNECTED} 状态的所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getDisconnectedSessionCount() {
        return getSessionCount(State.DISCONNECTED);
    }

    /**
     * 添加一个 Session 对象到集合中。
     *
     * @param session session 对象，不可为 null
     * @throws IllegalArgumentException 如果 session 为 null
     */
    public static void add(Session session) {
        Assert.notNull(session, "session");
        SESSIONS.add(session);
        Serializable roleId = SessionUtils.requireRoleId(session);
        SESSIONS_BY_ROLE_ID.put(roleId, session);
    }

    /**
     * 从 {@link #SESSIONS Session 集合} 中移除指定的 {@link Session} 对象。
     *
     * @param session Session 对象，不可为 null
     * @throws IllegalArgumentException 如果 session 为 null
     */
    public static void remove(Session session) {
        Assert.notNull(session, "session");
        SESSIONS.remove(session);
        Serializable roleId = SessionUtils.requireRoleId(session);
        SESSIONS_BY_ROLE_ID.remove(roleId);
    }

    public static Optional<Session> findById2(Serializable key) {
        return Optional.ofNullable(SESSIONS_BY_ROLE_ID.get(key));
    }

    @Nullable
    public static Session findById(Serializable id) {
        return SESSIONS_BY_ROLE_ID.get(id);
    }

    // Private methods
}
