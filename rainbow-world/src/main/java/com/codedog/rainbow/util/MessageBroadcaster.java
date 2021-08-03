/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class MessageBroadcaster {

    // /**
    //  * 将指定的消息广播给指定的 {@link Iterable<Session> Sessions}, 可以指定排除哪些。
    //  *
    //  * @param message  将要广播的消息
    //  * @param sessions 目标连接
    //  * @param excepted 除了...
    //  * @param flush    是否立即发送
    //  */
    // public static void broadcast(Object message,
    //                              Iterable<Session> sessions,
    //                              Iterable<Serializable> excepted,
    //                              boolean flush) {
    //     final Set<Serializable> ids = Sets.newHashSet(excepted);
    //     for (Session session : sessions) {
    //         Serializable roleId = SessionUtils.requireRoleId(session);
    //         if (!ids.contains(roleId)) {
    //             session.write(message, flush);
    //         }
    //     }
    // }
    //
    // public static void broadcast(Object message, Iterable<Serializable> roleIds,  boolean flush) {
    //     roleIds.forEach(roleId -> SessionService.findById2(roleId)
    //             .ifPresent(value -> value.write(message, flush)));
    // }
    //
    // public static void broadcast(Object message, boolean flush,
    //                              Iterable<Serializable> roleIds,
    //                              Serializable... except) {
    //     if (except == null) {
    //         roleIds.forEach(id -> SessionService.findById2(id)
    //                 .ifPresent(value -> value.write(message, flush)));
    //     } else {
    //         Set<Serializable> exceptRoleIds = Sets.newHashSet(except);
    //         for (Serializable roleId : roleIds) {
    //             if (!exceptRoleIds.contains(roleId)) {
    //                 SessionService.findById2(roleId).ifPresent(value -> value.write(message, flush));
    //             }
    //         }
    //     }
    // }
}
