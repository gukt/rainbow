/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.core.Tag;
import com.codedog.rainbow.core.rest.ApiResultView;

/**
 * JsonViews class
 *
 * @author https://github.com/gukt
 */
public class JsonViews {

    @Tag({"user-basic", "user-simple"})
    public interface UserBasicView extends ApiResultView {}

    @Tag({"user-detailed", "user-detail"})
    public interface UserDetailView extends ApiResultView {}
}
