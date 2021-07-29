/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.lang.NotImplementedException;
import com.codedog.rainbow.tcp.session.Session;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 消息处理器
 *
 * @author https://github.com/gukt
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
    default Object handle(Session session, T request) {
        throw new NotImplementedException();
    }

    class Error {
        public final static Error EMPTY = new Error();

        private final List<Object> errors = new ArrayList<>();

        /** Prevents to construct an instance. */
        private Error() {}

        // public static Error of() {
        //     return new Error();
        // }

        public static Error of(Object... errors) {
            Error instance = new Error();
            if (errors != null && errors.length > 0) {
                instance.errors.addAll(Arrays.asList(errors));
            }
            return instance;
        }

        public void add(Object error) {
            this.errors.add(error);
        }

        public boolean isEmpty() {
            return errors.isEmpty();
        }
    }
}
