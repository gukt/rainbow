package com.codedog.rainbow.api.common;

import lombok.Getter;

/**
 * @author https://github.com/gukt
 * @version 1.0
 * @date 2020/2/15 03:36
 */
public class ApiException extends RuntimeException {

    @Getter
    private int code;

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
