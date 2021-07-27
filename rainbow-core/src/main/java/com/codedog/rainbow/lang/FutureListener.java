/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.lang;

import java.util.EventListener;
import java.util.concurrent.Future;

/**
 * @author https://github.com/gukt
 */
public interface FutureListener<F extends Future<?>> extends EventListener {

    /**
     * 当Future完成时要执行的行为
     */
    void complete(F future);
}
