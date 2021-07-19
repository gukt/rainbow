/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 从查询字符串（Query String）中查找指定名称参数的值
     * TODO 移到 Https 里
     *
     * @param qs    query string, can be null.
     * @param param parameter name, can be null.
     * @return parameter value, can be null.
     */
    @Nullable
    public static String getParam(String qs, String param) {
        if (qs == null || param == null) {
            return null;
        }
        Pattern regex = Pattern.compile(".*" + param + "=([^&]+)&?.*");
        Matcher m = regex.matcher(qs);
        return m.find() ? m.group(1) : null;
    }

    @Nullable
    public static String getParam(ServerHttpRequest request, String param) {
        if (request == null || param == null) {
            return null;
        }
        return getParam(request.getURI().getQuery(), param);
    }
}
