/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.json;

import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.TcpServerChannelHandler;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * JsonPacketTcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class JsonPacketTcpServerChannelHandler extends TcpServerChannelHandler<JsonPacket> {

    public JsonPacketTcpServerChannelHandler(TcpProperties properties) {
        super(properties);
    }

    protected Session newSession(ChannelHandlerContext delegate) {
        return new DefaultSession(delegate, properties) {
            @Override
            public boolean beforeWrite( Object message) {
                if(message == null) return false;
                if (!(message instanceof JsonPacket)) {
                    log.warn("Unsupported message type: {} (expected: {}), message={}", message.getClass(), JsonPacket.class, message);
                    return false;
                }
                JsonPacket msg = (JsonPacket) message;
                int sn = store.incrSequenceNumberAndGet();
                msg.setSn(sn);
                msg.setAck(store.getAckNumber().get());
                msg.setSync(((JsonPacket) processingRequest).getSync());
                msg.setTime(System.currentTimeMillis());
                // 缓存起来
                store.cacheResponse(sn, message);
                return true;
            }
        };
    }
}
