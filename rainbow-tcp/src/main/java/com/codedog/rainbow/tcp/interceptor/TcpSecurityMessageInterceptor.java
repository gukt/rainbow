/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.interceptor;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.session.SessionStore;
import com.codedog.rainbow.tcp.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.codedog.rainbow.tcp.util.BaseError.EXCEED_BAD_REQUEST_THRESHOLD;
import static com.codedog.rainbow.tcp.util.BaseError.ILLEGAL_SEQUENCE_NUMBER;

/**
 * 对每个发来的包做一些安全检查
 * 如果有 Gateway，则该安全检查可以放到 Gateway 上，Gateway 后面的进程选择完全信任
 *
 * @author https://github.com/gukt
 */
@Slf4j
public abstract class TcpSecurityMessageInterceptor<T> implements MessageInterceptor<T> {

    private final TcpProperties properties;
    /**
     * IP 地址黑名单
     */
    private final Set<String> blockedAddresses = new HashSet<>();

    public TcpSecurityMessageInterceptor(TcpProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(Session session, T request) {
        // 检查是否在黑名单中
        if (blockedAddresses.contains(session.getPeerInfo().getAddressString())) {
            session.close();
            return false;
        }
        // 如果启用包检查
        if (properties.isSeqCheckEnabled()) {
            return checkSequenceNumber(session, request);
        }
        return true;
    }

    private boolean checkSequenceNumber(Session session, T request) {
        final SessionStore store = session.getStore();
        final int expectedSn = store.getAckNumber().get();
        final int sn = getSn(request);
        // 包序和期望的序号一致，放行并递增 ACK
        if (sn == expectedSn) {
            store.getAckNumber().incrementAndGet();
            return true;
        }
        // 收到的包序比期望的序号小，表示该请求之前已经处理过了，可以直接从缓存中返回。
        if (sn < expectedSn) {
            final int peerAck = getAck(request);
            Collection<Object> cached = store.getCachedResponsesFrom(peerAck);
            cached.forEach(session::write);
            return false;
        }
        // 包序比期望的大
        incrementBadCountOrReject(session);
        return false;
    }

    private void incrementBadCountOrReject(Session session) {
        SessionStore store = session.getStore();
        // 检查连续发送无效包是否超出阈值。超出视为攻击服务器主动掐断连接并将该连接加入黑名单
        if (store.getBadCount().get() > properties.getBadRequestThreshold()) {
            log.warn("TCP: 检测到该连接持续发送无效包，即将关闭对方的连接: {}", session);
            Object err = MessageUtils.errorOf(EXCEED_BAD_REQUEST_THRESHOLD);
            // 下发错误代码，然后关闭连接
            session.write(err).whenComplete((v, e) -> {
                log.warn("TCP: 已经关闭持续发送无效包的连接: {}", session);
                session.close();
            });
            // 将攻击IP地址加入黑名单
            blockedAddresses.add(session.getPeerInfo().getAddressString());
        }
        // 下发错误代码，然后关闭连接
        session.write(MessageUtils.errorOf(ILLEGAL_SEQUENCE_NUMBER)).whenComplete(Session.CLOSE);
    }

    protected abstract int getSn(T message);

    protected abstract int getAck(T message);
}
