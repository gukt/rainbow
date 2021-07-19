/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import lombok.Getter;

/**
 * ViewNotFoundException class
 *
 * @author https://github.com/gukt
 */
public class ViewNotFoundException extends RuntimeException {

    @Getter private String view;

    ViewNotFoundException(String view) {
        super("View not found: " + view);
    }
}
