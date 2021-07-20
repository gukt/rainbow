/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author https://github.com/gukt
 * @version 1.0
 * @date 2020/1/26 00:31
 */
@Data
@Accessors(fluent = true)
@JsonView(ApiResultView.class)
@JsonInclude(Include.NON_NULL)
@Slf4j
public class ApiResult {

    /**
     * 错误代码，0 表示成功，非零表示失败。错误码不建议使用负数。
     *
     * 由于该对象启用了链式访问（见: {@link Accessors#fluent()}）,所以属性方法是没有 get 和 set 前缀的。
     * 然而，Jackson 默认的序列化/反序列化机制是使用带 get 和 set 前缀的方法，所以这里要加上 {@link JsonProperty} 注解。
     */
    @JsonProperty private final int code;
    /**
     * HTTP 状态码
     */
    @JsonProperty private int status = 200;
    /**
     * 详细的错误描述
     */
    @JsonProperty private String error;
    /**
     * 响应数据
     */
    @JsonProperty private Object data;

    /**
     * TODO 解决 Errors 里静态变量append 一直被改变的问题
     * "请求参数有问题:id:id:id:id:id"
     */
    public ApiResult error(String s, boolean append, String delimiter) {
        if (append) {
            this.error += delimiter + s;
        } else {
            this.error = s;
        }
        return this;
    }

    public ApiResult error(String s, boolean append) {
        return error(s, append, ":");
    }

    public ApiResult error(String s) {
        return error(s, true, ":");
    }

    /**
     * 成功
     */
    public static final ApiResult SUCCESS = success("ok");
    /**
     * 失败
     */
    public static final ApiResult FAILED = failed(-1, "failed");

    /**
     * Constructs an {@link ApiResult} object that represents success.
     *
     * @param data payload
     * @return {@link ApiResult} object that represents success.
     */
    public static ApiResult success(Object data) {
        return new ApiResult(0).data(data);
    }

    /**
     * Constructs an {@link ApiResult} object that represents success.
     *
     * @param data   payload
     * @param status standard http status.
     * @return {@link ApiResult} object that represents success.
     */
    static ApiResult success(Object data, int status) {
        return new ApiResult(0).data(data).status(status);
    }

    /**
     * Constructs an {@link ApiResult} object that represents an error.
     *
     * @param code  the code. negative is not recommended.
     * @param error error description. empty string (or null) is not recommended.
     * @return {@link ApiResult result} object that represents an error.
     */
    public static ApiResult failed(int code, String error) {
        return new ApiResult(code).error(error);
    }

    /**
     * Constructs a {@link ApiResult} object that represents an error.
     *
     * @param code   the code. negative is not recommended.
     * @param error  error description. empty string (or null) is not recommended.
     * @param status standard http status.
     * @return {@link ApiResult result} object that represents an error.
     */
    static ApiResult failed(int code, String error, int status) {
        return new ApiResult(code).error(error).status(status);
    }

    /**
     * Transforms {@link ApiResult this} to be an {@link ApiException}.
     *
     * @return an {@link ApiException#ApiException(int, String)} object.
     */
    public ApiException toException() {
        if (code == 0) {
            log.warn("It doesn't seem to be a failed result. code: " + code);
        }
        return new ApiException(code, error);
    }
}
