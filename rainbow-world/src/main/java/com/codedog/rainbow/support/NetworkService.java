/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.support;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-30 18:18
 *
 * @author gukt <gukaitong@gmail.com>
 */
public interface NetworkService extends Lifecycle {

    /**
     * 获得网络服务的真实端口号（当使用随机端口的时候需要）
     *
     * @return 返回随机的端口号
     * @throws IllegalStateException if service is not running
     */
    int getRealPort();
}
