/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.support;

/**
 * @author https://github.com/gukt
 */
public interface NetworkService extends Lifecycle {

    /**
     * 获得网络服务的真实端口号（当使用随机端口的时候需要）
     *
     * @return 真实的端口号
     * @throws IllegalStateException if service is not running
     */
    int getRealPort();
}
