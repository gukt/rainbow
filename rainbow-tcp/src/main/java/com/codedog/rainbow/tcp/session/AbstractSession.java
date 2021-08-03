/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.util.AttributeSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * AbstractSession
 *
 * @author https://github.com/gukt
 */
@Slf4j
public abstract class AbstractSession extends AttributeSupport implements Session {

    @Getter protected final SessionStore store;
    /**
     * 会话标识
     */
    @Getter protected Serializable id;
    /**
     * 掉线时间（单位：毫秒）。默认为 0，表示正常连接，断线重连时应该将该值重置为 0。
     */
    @Getter protected volatile long closeTimeMillis;
    /**
     * 与此连接关联 {@link PeerInfo 对端（客户端）} 信息。
     */
    @Getter protected PeerInfo peerInfo;
    /**
     * 连接的状态，默认为 {@link com.codedog.rainbow.tcp.session.Session.State#NEW}
     */
    @Getter
    @Setter
    private State state = State.NEW;
    /**
     * 当前正在处理的请求。
     */
    @Getter
    @Setter
    protected volatile Object processingRequest;

    /**
     * 构造函数
     *
     * @param properties SessionProperties
     */
    AbstractSession(TcpProperties properties) {
        this.store = new SessionStore(properties.getSession());
    }

    @Override
    public CompletableFuture<Session> write(Object message) {
        return write(message, true);
    }

    @Override
    public boolean isClosed() {
        return closeTimeMillis > 0;
    }

    /**
     * 表示当前连接是否有请求仍在处理中。
     *
     * @return 如果有，返回 true；反之 false
     */
    @Override
    public boolean isProcessing() {
        return processingRequest != null;
    }

    @Override
    public void completeRequest() {
        // Dequeue from buffer
        store.getPendingRequests().read();
        // 更新标记位，以便 MessagePumper 可以继续调度下一条请求，该机制保证了单个Session请求的串行执行
        processingRequest = null;
    }

    protected abstract Object beforeWrite(Object message);

    @Override
    public void completeRequestExceptionally() {
        // 更新标记位，以便MessagePumper可以继续调度下一条请求，该机制保证了单个Session请求的串行执行
        processingRequest = null;
    }

    @Override
    public void reopen() {
        closeTimeMillis = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractSession that = (AbstractSession) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id + (isClosed() ? " [closed]" : "");
    }
}
