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
 * 标准的 API 响应结构。
 *
 * NOTE: 由于该对象启用了链式访问（见: {@link Accessors#fluent()}）,所以 lombok 生成的属性方法是没有 get 和 set 前缀的。
 * 然而，Jackson 默认的序列化/反序列化机制是使用带 get 和 set 前缀的方法，所以这里要加上 {@link JsonProperty} 注解。
 * 或者将 {@link JsonView @JsonView} 写在每一个需要序列化的字段上。
 *
 * @author https://github.com/gukt
 */
@JsonInclude(Include.NON_NULL)
@Accessors(fluent = true)
@Data
@Slf4j
public class ApiResult {

    /**
     * 错误代码，0 表示成功，非零表示失败。错误码不建议使用负数。
     */
    @JsonView(ApiResultView.class) private final int code;
    /**
     * 详细的错误描述
     */
    @JsonView(ApiResultView.class) private String error;
    /**
     * 响应数据
     */
    @JsonView(ApiResultView.class) private Object data;
    /**
     * HTTP 状态码
     */
    private int status = 200;

//    /**
//     * TODO 解决 Errors 里静态变量append 一直被改变的问题
//     * "请求参数有问题:id:id:id:id:id"
//     */
//    public ApiResult error(String s, boolean append, String delimiter) {
//        if (append) {
//            this.error += delimiter + s;
//        } else {
//            this.error = s;
//        }
//        return this;
//    }
//
//    public ApiResult error(String s, boolean append) {
//        return error(s, append, ":");
//    }
//
//      TODO 这个会覆盖默认的 error，会导致 null: xxx 的结果
//    public ApiResult error(String s) {
//        return error(s, true, ":");
//    }

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
