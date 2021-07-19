/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.core.Tag;
import com.codedog.rainbow.core.rest.ApiResultView.IdOnly;

/**
 * JsonViews class
 *
 * @author https://github.com/gukt
 */
public final class JsonViews {

    @Tag({"user-basic", "user-simple"})
    public interface UserBasicView extends IdOnly {}
}
