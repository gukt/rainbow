/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.support.AbstractAttributeSupport;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractSession
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Slf4j
public abstract class AbstractSession extends AbstractAttributeSupport implements Session {

    @Getter
    protected final SessionStore store;
    @Getter
    protected Serializable id;
    /**
     * 上一次掉线时间，默认为0表示正常连接，当断线重连时应该将该值重置为0
     */
    @Getter
    protected volatile long closeTimeMillis;
    /**
     * 表示与此连接关联的peer端的信息
     */
    @Getter
    protected PeerInfo peerInfo;
    /**
     * 当前正在处理的请求
     */
    @Getter
    @Setter
    protected volatile Object processingRequest;

    AbstractSession(int maxPendingRequestSize, int maxCacheResponseSize) {
        this.store = new SessionStore(maxPendingRequestSize, maxCacheResponseSize);
    }

    @Override
    public CompletableFuture<Void> write(@NonNull Object message) {
        return write(message, true);
    }

    @Override
    public boolean isClosed() {
        return closeTimeMillis > 0;
    }

    @Override
    public String toString() {
        return String.format("%s#%s%s", getClass().getSimpleName(), id, (isClosed() ? " [closed]" : ""));
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
    public boolean isProcessing() {
        return processingRequest != null;
    }

    @Override
    public void completeRequest() {
        // dequeue from buffer
        store.getPendingRequests().read();
        // 更新标记位，以便MessagePumper可以继续调度下一条请求，该机制保证了单个Session请求的串行执行
        processingRequest = null;
    }

    @Override
    public void completeRequestExceptionally() {
        // 更新标记位，以便MessagePumper可以继续调度下一条请求，该机制保证了单个Session请求的串行执行
        processingRequest = null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public void reopen() {
        closeTimeMillis = 0;
    }
}
