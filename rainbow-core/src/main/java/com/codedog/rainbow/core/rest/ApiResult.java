/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
     * 错误代码
     */
    @JsonView(ApiResultView.class)
    private int code;
    /**
     * HTTP 状态码
     */
    @JsonView(ApiResultView.class)
    private int status = 200;
    /**
     * 具体的错误信息
     */
    @JsonView(ApiResultView.class)
    private String error;
    /**
     * 响应数据
     */
    @JsonView(ApiResultView.class)
    private Object data;

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

    public static ApiResult success(Object data) {
        return success(data, 200);
    }

    static ApiResult success(Object data, int status) {
        return of(0, null, data, status);
    }

    public static ApiResult failed(int code, String error) {
        return failed(code, error, 200);
    }

    static ApiResult failed(int code, String error, int status) {
        return of(code, error, null, status);
    }

    private static ApiResult of(int code, String error, Object data) {
        ApiResult result = new ApiResult();
        result.code = code;
        result.data = data;
        result.error = error;
        return result;
    }

    private static ApiResult of(int code, String error, Object data, int status) {
        ApiResult result = of(code, error, data);
        result.status = status;
        return result;
    }

    public ApiException toException() {
        if(code == 0) {
            log.warn("It doesn't seem to be a failed result. code: " + code);
        }
        return new ApiException(code, error);
    }
}
