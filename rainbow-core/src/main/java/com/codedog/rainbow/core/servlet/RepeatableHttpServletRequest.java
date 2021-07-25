/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.servlet;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 创建一个可重复读取的HttpServletRequest，用于多次读取请求Body中的数据，
 *
 * <p>因为HttpServletRequest中的getInputStream()返回的流默认是仅支持读取一次的，这使得他不能满足需要多次读取body的场景。
 *
 * <p>比如在签名验证的场景中：
 *
 * <ol>
 *   <li>Interceptor先读取一次body数据，用来校验签名是否正确
 *   <li>如果签名正确，请求会交给相应的Handler处理，Handler参数映射时会再读取一次body数据。
 * </ol>
 *
 * <p>其基本原理是利用装饰者模式，将原始的HttpServletRequest的Body数据先读取出来放入body字节数组中，
 * 然后再重写getInputStream方法，调用该方法每次都返回一个新的ServletInputStream，
 * 该ServletInputStream的read委托ByteArrayInputStream.read()方法，从之前保存的body字节数组中读取数据
 *
 * @author https://github.com/gukt
 * @version 1.0
 * @date 2020/3/19 11:52
 */
@Slf4j
public class RepeatableHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] body;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    RepeatableHttpServletRequest(HttpServletRequest request) {
        super(request);

        // 读取请求的body数据并保存
        String data = getBodyString(request);
        body = data.getBytes(StandardCharsets.UTF_8);
    }

    private String getBodyString(final ServletRequest request) {
        try {
            Reader reader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
            return CharStreams.toString(reader);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException("读取请求的body数据出错", e);
        }
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}
        };
    }
}
