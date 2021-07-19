/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import lombok.Getter;

/**
 * @author https://github.com/gukt
 */
public class ApiException extends RuntimeException {

    @Getter private final int code;

    private ApiException(int code, String error, Throwable cause) {
        super(error, cause);
        this.code = code;
    }

    public ApiException(int code, String error) {
        this(code, error, null);
    }

    private ApiException(ApiResult result, Throwable cause) {
        super(result.error(), cause);
        this.code = result.code();
    }

    public ApiException(ApiResult result) {
        this(result, null);
    }

    ApiResult toApiResult() {
        return ApiResult.failed(code, getMessage());
    }
}
