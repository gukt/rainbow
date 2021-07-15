/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.support.NotImplementedException;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 消息处理程序
 *
 * @author gukt <gukaitong@gmail.com>
 */
public interface MessageHandler<T> {

    /**
     * 可处理消息的类型
     *
     * @return 返回消息类型
     */
    default Serializable getType() {
        throw new NotImplementedException();
    }

    /**
     * 处理指定连接发来的请求
     *
     * @param session session object
     * @param request request from session
     * @return 返回处理结果，null表示没有下发消息
     */
    @Nullable
    default Object handle(@NonNull Session session, @NonNull T request) {
        throw new NotImplementedException();
    }
}
