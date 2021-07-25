/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author https://github.com/gukt
 * @version 1.0
 * @date 2020/3/19 12:02
 */
//@Component
//@WebFilter(urlPatterns = "/api/**", filterName = "replaceStreamFilter1")
//@Order(1)
//@Slf4j
public class ReplaceStreamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletRequest requestWrapper = new RepeatableHttpServletRequest((HttpServletRequest) request);
        chain.doFilter(requestWrapper, response);
    }
}
