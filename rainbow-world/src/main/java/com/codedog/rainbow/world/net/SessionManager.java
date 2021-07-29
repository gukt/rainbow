/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.util.Sessions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author https://github.com/gukt
 */
public class SessionManager {

    /**
     * 保存当前所有roleId->session实例的映射
     */
    private static final Map<Serializable, Session> SESSIONS_BY_ROLE_ID = new ConcurrentHashMap<>(16);

    /**
     * 当前所有已接收的连接
     */
    private static final List<Session> CONNECTIONS = new CopyOnWriteArrayList<>();

    public static Optional<Session> getSessionById(Serializable key) {
        return Optional.ofNullable(SESSIONS_BY_ROLE_ID.get(key));
    }

    /**
     * 添加一个已登录进游戏的Session，
     * 是否已登录由session对象中是否包含roleId自定义属性决定，如果该属性不存在则会抛出异常
     *
     * @param session 包含roleId自定义属性的session对象
     * @throws IllegalArgumentException 如果session未登陆游戏
     */
    public static void addSession(Session session) {
        Serializable roleId = Sessions.safeGetRoleId(session);
        SESSIONS_BY_ROLE_ID.put(roleId, session);
    }

    /**
     * 获取当前所有连接(进游戏的 + 未进游戏的)
     *
     * @return return current active connections
     */
    public static List<Session> getConnections() {
        return CONNECTIONS;
    }

    /**
     * 获取当前所有连接数，返回的是一个近似值
     *
     * @return return current active connection count
     */
    public static int getConnectionCount() {
        return CONNECTIONS.size();
    }

    public static int getAllRoleCount() {
        return SESSIONS_BY_ROLE_ID.size();
    }

    // /**
    //  * 获得指定范围的当前角色数，返回的是一个近似值
    //  */
    // public static int getRoleCount(CountScopeEnum scope) {
    //     switch (scope) {
    //         case ROLE_ALL:
    //             return SESSIONS_BY_ROLE_ID.size();
    //         case ROLE_ONLINE:
    //             return (int) SESSIONS_BY_ROLE_ID.values().stream().filter(s -> !s.isClosed()).count();
    //         case ROLE_OFFLINE:
    //             return (int) SESSIONS_BY_ROLE_ID.values().stream().filter(Session::isClosed).count();
    //         default:
    //             return -1;
    //     }
    // }

    public static int getOnlineRoleCount() {
        return (int) SESSIONS_BY_ROLE_ID.values().stream().filter(s -> !s.isClosed()).count();
    }

    public static int getOfflineRoleCount() {
        return (int) SESSIONS_BY_ROLE_ID.values().stream().filter(Session::isClosed).count();
    }

    /**
     * 移除一个已经登陆进游戏的Session对象
     *
     * @param session 已经登陆进游戏的Session对象
     */
    public void removeSession(Session session) {
        Serializable roleId = Sessions.safeGetRoleId(session);
        SESSIONS_BY_ROLE_ID.remove(roleId);
    }

    // public enum CountScopeEnum {
    //     /**
    //      * 当前所有角色个数（在线+离线）
    //      */
    //     ROLE_ALL,
    //     /**
    //      * 当前所有在线角色个数
    //      */
    //     ROLE_ONLINE,
    //     /**
    //      * 当前所有离线角色个数
    //      */
    //     ROLE_OFFLINE
    // }
}
