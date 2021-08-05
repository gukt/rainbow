/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.util.Assert;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session Service
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
public class SessionService {

    public static String ROLE_ID = "roleId";

    /**
     * 用以保存所有 “roleId -> {@link Session}” 的映射
     * TODO 有没有必要同时多维护一个集合？
     */
    private static final Map<Serializable, Session> SESSIONS_BY_ROLE_ID = new ConcurrentHashMap<>(16);


    @Nullable
    public static <V extends Serializable> V getRoleId(Session session) {
        Assert.notNull(session, "session");
        V roleId = session.get(ROLE_ID);
        SESSIONS_BY_ROLE_ID.putIfAbsent(roleId, session);
        return roleId;
    }

    @Nullable
    public static Session getSessionByRoleId(Serializable roleId) {
        return SESSIONS_BY_ROLE_ID.get(roleId);
    }

    // /**
    //  * 添加一个 Session 对象到集合中。
    //  *
    //  * @param session session 对象，不能为 null
    //  * @throws IllegalArgumentException 如果 session 为 null
    //  */
    // public static void add(Session session) {
    //     Assert.notNull(session, "session");
    //     SESSIONS.add(session);
    //     Serializable roleId = SessionUtils.requireRoleId(session);
    //     SESSIONS_BY_ROLE_ID.put(roleId, session);
    // }
    //
    // /**
    //  * 从 {@link #SESSIONS Session 集合} 中移除指定的 {@link Session} 对象。
    //  *
    //  * @param session Session 对象，不能为 null
    //  * @throws IllegalArgumentException 如果 session 为 null
    //  */
    // public static void remove(Session session) {
    //     Assert.notNull(session, "session");
    //     SESSIONS.remove(session);
    //     Serializable roleId = SessionUtils.requireRoleId(session);
    //     SESSIONS_BY_ROLE_ID.remove(roleId);
    // }

    // public static Optional<Session> findById2(Serializable key) {
    //     return Optional.ofNullable(SESSIONS_BY_ROLE_ID.get(key));
    // }

    @SuppressWarnings("unchecked")
    public static <E extends Session> E findById3(Serializable key) {
        return (E) SESSIONS_BY_ROLE_ID.get(key);
    }

    @Nullable
    public static Session findById(Serializable id) {
        return SESSIONS_BY_ROLE_ID.get(id);
    }

    // Private methods
}
