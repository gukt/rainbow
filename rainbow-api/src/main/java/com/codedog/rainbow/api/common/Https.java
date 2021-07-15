/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.common;

import org.springframework.http.server.ServerHttpRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Https class
 *
 * @author https://github.com/gukt
 */
public class Https {

    public static String getRemoteAddress(HttpServletRequest request) {
        String realIp = request.getHeader("X-Real-IP");
        if (realIp == null) {
            realIp = request.getRemoteAddr();
        }
        return realIp;
    }

    public static String getRemoteAddress(ServerHttpRequest request) {
        List<String> values = request.getHeaders().get("X-Real-IP");
        String realIp;
        if (values != null && !values.isEmpty()) {
            realIp = values.get(0);
        } else {
            realIp = request.getRemoteAddress().getHostName();
        }
        return realIp;
    }
}
