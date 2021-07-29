/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.net.json.interceptor;

import com.codedog.rainbow.tcp.MessageInterceptor;
import com.codedog.rainbow.tcp.MessageResolver;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.session.SessionStore;
import com.codedog.rainbow.world.config.TcpProperties;
import com.codedog.rainbow.world.net.json.JsonPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对每个发来的包做一些安全检查
 * 如果有 Gateway，则该安全检查可以放到 Gateway 上，Gateway 后面的进程选择完全信任
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class TcpSecurityMessageInterceptor implements MessageInterceptor<JsonPacket> {

    private final TcpProperties properties;
    private final MessageResolver<?> messageResolver;
    /**
     * IP 地址黑名单
     */
    private final Set<String> blockedRemoteAddresses = new HashSet<>();

    public TcpSecurityMessageInterceptor(TcpProperties properties) {
        this.properties = properties;
        messageResolver = properties.getMessageResolver();
    }

    @Override
    public boolean preHandle(Session session, JsonPacket request) {
        // 检查是否在黑名单中
        final InetSocketAddress remoteAddress = session.getPeerInfo().getRemoteAddress();
        if (blockedRemoteAddresses.contains(remoteAddress.getHostString())) {
            session.close();
            return false;
        }
        // 如果启用包检查
        if (properties.isSeqCheckEnabled()) {
            return checkPacketSeq(session, request);
        }
        return true;
    }

    private boolean checkPacketSeq(Session session, JsonPacket request) {
        final SessionStore store = session.getStore();
        final int ack = store.getNextAck().get();
        final int seq = request.getSn();
        // 收到的包序和期望的包序一致，放行并递增 ACK
        if (seq == ack) {
            store.getNextAck().incrementAndGet();
            return true;
        }
        // 如果收到的包序大于期望的包序，则返回错误
        // 任何一个下发包都带有服务器期望的下一个包序号，对端可以通过下发包中的ack纠正发包错误
        if (seq > ack) {
            log.warn("TCP: Received a bad packet: want={}, got={}, session={}", ack, request.getSn(), session);
            // 累加 “收到的无效包” 计数
            store.incrBadPacketCount();
            // 检查连续发送无效包是否超出阈值。超出视为攻击服务器主动掐断连接并将该连接加入黑名单
            if (store.getBadPacketCount() > properties.getBadPacketThreshold()) {
                log.warn("TCP: 检测到该连接持续发送无效包，即将关闭对方的连接: {}", session);
                Object errorPacket = messageResolver.EXCEED_CONTINUOUS_BAD_REQUEST_THRESHOLD_ERROR();
                // 下发错误代码，然后关闭连接
                session.write(errorPacket).whenComplete((v, e) -> {
                    log.warn("TCP: 已经关闭持续发送无效包的连接: {}", session);
                    session.close();
                });
                //
                // JsonPacket.ofError(ErrorCode.ERR_EXCEED_CONTINUOUS_BAD_REQUEST_THRESHOLD)
                //         .writeTo(session)
                //         .whenComplete((v, e) -> {
                //             log.warn("TCP: 已经关闭持续发送无效包的连接: {}", session);
                //             session.close();
                //         });
                // 将攻击IP地址加入黑名单
                blockedRemoteAddresses.add(session.getPeerInfo().getRemoteAddress().getHostString());
            }
            // 下发错误代码
            Object errorPacket = messageResolver.ERR_ILLEGAL_SN();
            // 下发错误代码，然后关闭连接
            session.write(errorPacket);
        } else {
            // 如果收到一个已经处理过的包，则判断是否需要把缓存中的消息下发给客户端
            final int peerAck = request.getAck();
            List<Object> retrieved = store.retrieveResponsesSince(peerAck);
            retrieved.forEach(session::write);
        }
        return false;
    }
}
