/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import lombok.Getter;

/**
 * UnsupportedJsonViewException class
 *
 * @author https://github.com/gukt
 */
public class BadJsonViewException extends RuntimeException {
    @Getter
    private String view;

    BadJsonViewException(String view) {
        super("Unsupported view: " + view);
    }
}
