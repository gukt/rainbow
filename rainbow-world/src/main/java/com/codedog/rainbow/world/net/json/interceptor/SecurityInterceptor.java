/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.json.interceptor;

import com.codedog.rainbow.world.GameOptions;
import com.codedog.rainbow.world.net.ErrorCodeEnum;
import com.codedog.rainbow.world.net.MessageInterceptor;
import com.codedog.rainbow.world.net.Session;
import com.codedog.rainbow.world.net.SessionStore;
import com.codedog.rainbow.world.net.json.JsonPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对每个连接发来的包做一些安全检查
 * 如果有gateway，则该安全检查可以放到gateway上，gateway后面的进程选择完全信任
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Slf4j
public final class SecurityInterceptor implements MessageInterceptor<JsonPacket> {

    private final GameOptions opts;
    /**
     * IP地址黑名单
     */
    private final Set<String> banHosts = new HashSet<>();

    public SecurityInterceptor(GameOptions opts) {
        this.opts = opts;
    }

    @Override
    public boolean preHandle(Session session, JsonPacket request) {
        // 检查是否在黑名单中
        final InetSocketAddress peerAddress = session.getPeerInfo().getRemoteAddress();
        if (banHosts.contains(peerAddress.getHostString())) {
            session.close();
            return false;
        }
        // 如果启用包检查
        if (opts.isTcpPacketSeqCheckEnabled()) {
            return checkPacketSeq(session, request);
        }
        return true;
    }

    private boolean checkPacketSeq(Session session, JsonPacket request) {
        final SessionStore store = session.getStore();
        final int ack = store.getNextAck().get();
        final int seq = request.getSeq();
        // 收到的包序和期望的包序一致，放行,并递增ack
        if (seq == ack) {
            store.getNextAck().incrementAndGet();
            return true;
        }
        // 如果收到的包序大于期望的包序，则返回错误
        // 任何一个下发包都带有服务器期望的下一个包序号，对端可以通过下发包中的ack纠正发包错误
        if (seq > ack) {
            log.warn("TCP: 无效的包序号: want:{}, got:{}, session:{}", ack, request.getSeq(), session);
            // 累加"连续接收无效包计数器"
            store.incrementContinuousBadRequestCount();
            // 检查连续发送无效包是否超出阈值，超出则视为攻击，服务器主动掐断连并将该连接加入黑名单
            if (store.getContinuousBadRequestCount() > opts.getTcpContinuousBadPacketThreshold()) {
                log.warn("TCP: 检测到持续收到无效包，即将关闭对方的连接: {}", session);
                // 发送错误消息然后关闭连接
                JsonPacket.ofError(ErrorCodeEnum.ERR_EXCEED_CONTINUOUS_BAD_REQUEST_THRESHOLD)
                        .writeTo(session)
                        .whenComplete((v, e) -> {
                            log.warn("TCP: 已经关闭持续发送无效包的连接: {}", session);
                            session.close();
                        });
                // 将攻击IP地址加入黑名单
                banHosts.add(session.getPeerInfo().getRemoteAddress().getHostString());
            }
            // 下发错误代码
            JsonPacket.ofError(ErrorCodeEnum.ERR_UNEXCEPTED_PACKET_SEQ).writeTo(session);
            return false;
        } else {
            // 如果收到一个已经处理过的包序，则判断是否需要把缓存中的消息下发给客户端
            final int peerAck = request.getAck();
            List<Object> retrieved = store.retrieveResponsesSince(peerAck);
            retrieved.forEach(session::write);
            return false;
        }
    }
}
