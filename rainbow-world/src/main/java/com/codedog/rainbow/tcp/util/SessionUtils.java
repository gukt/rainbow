/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import com.codedog.rainbow.tcp.session.Session;

import java.io.Serializable;

import static com.codedog.rainbow.tcp.session.SessionConstants.ATTR_ROLE_ID;

/**
 * SessionUtils class
 *
 * @author https://github.com/gukt
 */
public class SessionUtils {

    public static Serializable requireRoleId(Session session) {
        Serializable roleId = session.get(ATTR_ROLE_ID);
        if (roleId == null) {
            throw new IllegalStateException("Cannot found the attribute '"
                    + ATTR_ROLE_ID + "' in: " + session);
        }
        return roleId;
    }
}
