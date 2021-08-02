/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.util;

import lombok.Getter;

/**
 * BaseErrorCode class
 *
 * @author https://github.com/gukt
 */
public class BaseError {

    public static final BaseError HANDLER_NOT_FOUND = BaseError.of(0, "HANDLER_NOT_FOUND");
    public static final BaseError ILLEGAL_SEQUENCE_NUMBER = BaseError.of(1, "ILLEGAL_SEQUENCE_NUMBER");
    public static final BaseError SERVER_BUSYING = BaseError.of(2, "SERVER_BUSYING");
    public static final BaseError SERVER_INTERNAL_ERROR = BaseError.of(3, "SERVER_INTERNAL_ERROR");
    public static final BaseError HANDLER_MULTI_RESULT = BaseError.of(3, "HANDLER_MULTI_RESULT");
    public static final BaseError UNKNOWN_HANDLER_RESULT = BaseError.of(3, "UNKNOWN_HANDLER_RESULT");
    public static final BaseError SERVER_UNAVAILABLE = BaseError.of(4, "SERVER_UNAVAILABLE");
    public static final BaseError EXCEED_CONNECTIONS = BaseError.of(5, "EXCEED_CONNECTIONS");
    public static final BaseError EXCEED_SESSION_BACKLOGS = BaseError.of(6, "EXCEED_SESSION_BACKLOGS");
    public static final BaseError EXCEED_BAD_REQUEST_THRESHOLD = BaseError.of(7, "EXCEED_BAD_REQUEST_THRESHOLD");

    // ILLEGAL_SEQUENCE_NUMBER,
    // SERVER_BUSYING,
    // SERVER_INTERNAL_ERROR,
    // SERVER_UNAVAILABLE,
    // EXCEED_CONNECTIONS,
    // EXCEED_SESSION_BACKLOGS,
    // EXCEED_BAD_REQUEST_THRESHOLD

    @Getter private final int code;
    @Getter private final String msg;

    public BaseError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private static BaseError of(int code, String msg) {
        return new BaseError(code, msg);
    }
}
