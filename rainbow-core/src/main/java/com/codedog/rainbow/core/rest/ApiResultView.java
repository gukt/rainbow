/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.codedog.rainbow.core.Tag;

/**
 * ApiBasicView class
 *
 * @author https://github.com/gukt
 */
public interface ApiResultView {

    @Tag("id-only")
    interface IdOnly extends ApiResultView {}
}
