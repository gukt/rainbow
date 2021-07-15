/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import java.util.EventListener;
import java.util.concurrent.Future;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-19 16:51
 *
 * @author gukt <gukaitong@gmail.com>
 */
public interface FutureListener<F extends Future<?>> extends EventListener {

    /**
     * 当Future完成时要执行的行为
     */
    void complete(F future);
}
