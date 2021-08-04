/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.channel.json;

import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.channel.TcpServerChannelHandler;
import com.codedog.rainbow.tcp.message.JsonPacket;
import com.codedog.rainbow.tcp.message.MessageHandlerException;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.BaseError;
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
            public Object beforeWrite(Object message) {
                message = super.beforeWrite(message);
                // 将 “错误或异常” 转换一下
                if (message instanceof BaseError) {
                    message = JsonPacket.errorOf((BaseError) message);
                } else if (message instanceof MessageHandlerException) {
                    message = JsonPacket.errorOf((MessageHandlerException) message);
                }
                if (!(message instanceof JsonPacket)) {
                    log.warn("Ignored - Unable to write the message: {}, Type mismatch (expected: JsonPacket, actual: {})",
                            message, message.getClass().getName());
                    return null;
                }
                JsonPacket msg = (JsonPacket) message;
                int sn = store.incrSequenceNumberAndGet();
                msg.setSn(sn);
                msg.setAck(store.getAckNumber().get());
                msg.setSync(((JsonPacket) processingRequest).getSync());
                msg.setTime(System.currentTimeMillis());
                // 缓存起来
                store.cacheResponse(sn, message);
                return msg;
            }
        };
    }
}
