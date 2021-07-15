/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.util;

import com.codedog.rainbow.world.net.Session;
import com.codedog.rainbow.world.net.SessionManager;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * @author https://github.com/gukt
 */
@Slf4j
public class Sessions {

    private static final String ATTR_ROLE_ID = "roleId";

    public static Serializable safeGetRoleId(Session session) {
        Serializable roleId = session.getAttr(ATTR_ROLE_ID);
        if (roleId == null) {
            throw new IllegalArgumentException("roleId not found in session attributes");
        }
        return roleId;
    }

    public static Optional<Serializable> getRoleId(Session session) {
        Serializable roleId = session.getAttr(ATTR_ROLE_ID);
        return Optional.ofNullable(roleId);
    }

    public static void broadcast(Object message, boolean flush, Iterable<Session> sessions,
                                 Iterable<Serializable> except) {
        Set<Serializable> exceptRoleIds = Sets.newHashSet(except);
        for (Session session : sessions) {
            Serializable roleId = safeGetRoleId(session);
            if (!exceptRoleIds.contains(roleId)) {
                session.write(message, flush);
            }
        }
    }

    public static void broadcast(Object message, boolean flush, Iterable<Serializable> roleIds) {
        roleIds.forEach(roleId -> SessionManager.getSessionById(roleId)
                .ifPresent(value -> value.write(message, flush)));
    }

    public static void broadcast(Object message, boolean flush, Iterable<Serializable> roleIds,
                                 Serializable... except) {
        if (except == null) {
            roleIds.forEach(id -> SessionManager.getSessionById(id)
                    .ifPresent(value -> value.write(message, flush)));
        } else {
            Set<Serializable> exceptRoleIds = Sets.newHashSet(except);
            for (Serializable roleId : roleIds) {
                if (!exceptRoleIds.contains(roleId)) {
                    SessionManager.getSessionById(roleId)
                            .ifPresent(value -> value.write(message, flush));
                }
            }
        }
    }

    public static void setRoleId(Session session, long roleId) {
        session.putAttr(ATTR_ROLE_ID, roleId);
    }
}
