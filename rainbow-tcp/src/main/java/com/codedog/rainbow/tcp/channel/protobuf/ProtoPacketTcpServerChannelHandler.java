/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.channel.protobuf;

import com.codedog.rainbow.lang.TypeMismatchException;
import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.channel.TcpServerChannelHandler;
import com.codedog.rainbow.tcp.message.MessageHandler;
import com.codedog.rainbow.tcp.message.MessageHandler.Error;
import com.codedog.rainbow.tcp.message.MessageHandlerException;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacketOrBuilder;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * ProtoPacketTcpServerChannelHandler
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class ProtoPacketTcpServerChannelHandler extends TcpServerChannelHandler<ProtoPacket> {

    public ProtoPacketTcpServerChannelHandler(TcpProperties properties) {
        super(properties);
    }

    @Override
    protected Session newSession(ChannelHandlerContext delegate) {
        return new DefaultSession(delegate, properties) {
            @Override
            public Object beforeWrite(Object message) {
                message = super.beforeWrite(message);
                if (!isTypeSupported(message)) {
                    throw new TypeMismatchException(message, MessageHandler.Error.class, MessageHandlerException.class);
                }
                // 将 “错误或异常” 转换一下
                if (message instanceof MessageHandler.Error) {
                    message = ProtoUtils.errorOf((Error) message);
                }
                MessageLiteOrBuilder msg = ProtoUtils.requireMessageLiteOrBuilder(message, "message");
                if (!(msg instanceof ProtoPacketOrBuilder)) {
                    msg = ProtoUtils.wrapPacket(msg);
                }
                ProtoPacket.Builder builder = ProtoUtils.safeGetBuilder(msg);
                int sn = store.incrSequenceNumberAndGet();
                builder.setSn(sn);
                builder.setTime(System.currentTimeMillis());
                if (processingRequest != null) {
                    builder.setSync(((ProtoPacket) processingRequest).getSync());
                    builder.setRtd(((ProtoPacket) processingRequest).getRtd());
                }
                // 缓存起来
                store.cacheResponse(sn, message);
                return msg;
            }
        };
    }

    private boolean isTypeSupported(Object msg) {
        return msg != null && (msg instanceof MessageHandler.Error || msg instanceof MessageLiteOrBuilder);
    }
}
