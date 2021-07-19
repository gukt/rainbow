/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author https://github.com/gukt
 * @version 1.0
 * @date 2020/1/26 00:31
 */
@Data
@Accessors(fluent = true)
@JsonView(ApiResultView.class)
@JsonInclude(Include.NON_NULL)
public class ApiResult {

    @JsonView(ApiResultView.class) private int code;
    @JsonView(ApiResultView.class) private int status = 200;
    @JsonView(ApiResultView.class) private String error;
    @JsonView(ApiResultView.class) private Object data;

    /** 默认成功 */
    public static final ApiResult OK = success("ok");

    /** 默认失败 */
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

    private static ApiResult of(int code, String error, Object data, int status) {
        ApiResult result = new ApiResult();
        result.code = code;
        result.data = data;
        result.error = error;
        result.status = status;
        return result;
    }

    public ApiException toException() {
        return new ApiException(code, error);
    }
}
