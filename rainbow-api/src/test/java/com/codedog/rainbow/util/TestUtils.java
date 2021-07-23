/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * TestUtils class
 *
 * @author https://github.com/gukt
 */
public class TestUtils {

    public static ResultMatcher apiResultIsOk() {
        return jsonPath("$.code").value(0);
    }

    public static ResultMatcher apiResultCodeIs(int value) {
        return jsonPath("$.code").value(value);
    }
}
