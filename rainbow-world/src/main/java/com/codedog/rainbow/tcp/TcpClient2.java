/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.codec.json.JsonDecoder;
import com.codedog.rainbow.tcp.codec.json.JsonEncoder;
import com.codedog.rainbow.tcp.json.JsonPacketMessageResolver;
import com.codedog.rainbow.tcp.protobuf.ProtoPacketMessageResolver;
import com.codedog.rainbow.world.generated.CommonProto;
import com.google.common.math.IntMath;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gukaitong(29283212@qq.com) on 2015/11/12.
 * TODO 需进一步深入研究
 *
 * @author gukaitong
 * @since 1.0
 */
@Slf4j
@Deprecated
public class TcpClient2<T> {

    private final String host;
    private final int port;
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final ConcurrentHashMap<Integer, CompletableFuture<T>> futuresBySyncId = new ConcurrentHashMap<>();
    private final AtomicInteger nextSeq = new AtomicInteger(0);
    private final ChannelHandler channelHandler = new ClientChannelHandler();
    private final Bootstrap b = new Bootstrap();
    private Channel channel;
    private final AtomicInteger nextSync = new AtomicInteger(0);
    private final AtomicInteger nextAck = new AtomicInteger(1);
    private final MessageResolver<T> messageResolver;

    public TcpClient2(String host, int port, Class<T> messageType) {
        this.host = host;
        this.port = port;
        if (messageType.equals(CommonProto.ProtoPacket.class)) {
            messageResolver = (MessageResolver<T>) new ProtoPacketMessageResolver();
        } else {
            messageResolver = (MessageResolver<T>) new JsonPacketMessageResolver();
        }
        this.init();
    }

//    public TcpClient(int port) {
//        this("localhost", port, CommonProto.ProtoPacket.class);
//    }

    public TcpClient2(int port, Class<T> messageType) {
        this("localhost", port, messageType);
    }

    private void init() {
        b.group(group).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new JsonObjectDecoder());
                        p.addLast(new JsonDecoder());
                        p.addLast(new JsonEncoder());
                        p.addLast(channelHandler);
                    }
                });
    }

    private void connect() {
        int retryCount = 0;
        int maxRetryCount = 3;
        do {
            try {
                b.connect(host, port).sync().channel();
                return;
            } catch (Exception e) {
                log.error("CLIENT: {}", e.getMessage());
            }
            ++retryCount;
            try {
                TimeUnit.SECONDS.sleep(Math.min(8, IntMath.pow(2, retryCount)));
            } catch (InterruptedException ignored) {
            }

            log.info("CLIENT: 正在尝试连接到服务器({}:{}) ... [{}/{}]", host, port, retryCount, maxRetryCount);
        } while (retryCount <= maxRetryCount);

        log.info("CLIENT: 连接服务器失败，请检查服务器是否已启动！");
    }

    public void write(T request) {
        requireConnected();

        messageResolver.setSn(request, nextSeq.incrementAndGet());
        messageResolver.setAck(request, nextAck.get());
        messageResolver.setTime(request, System.currentTimeMillis());

        log.debug("TCP CLIENT - Sending: {}", request);
        channel.writeAndFlush(request);
    }

    public CompletableFuture<T> writeSync(T request) {
        requireConnected();

        CompletableFuture<T> future = new CompletableFuture<>();
        messageResolver.setSn(request, nextSeq.incrementAndGet());
        messageResolver.setAck(request, nextAck.get());
        messageResolver.setTime(request, System.currentTimeMillis());
        messageResolver.setSync(request, nextSync.incrementAndGet());
        futuresBySyncId.put(messageResolver.getSync(request), future);
        log.debug("CLIENT: Sending: {}", request);
        channel.writeAndFlush(request);
        return future;
    }

    private void requireConnected() {
        if (channel == null || !channel.isOpen()) {
            this.connect();
        }
    }

    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    @Sharable
    private final class ClientChannelHandler extends SimpleChannelInboundHandler<T> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, T response) {
            log.debug("CLIENT: Receiving: {}", response);
            // 递增ack序号
            nextAck.incrementAndGet();
            int syncId = messageResolver.getSync(response);
            CompletableFuture<T> future = futuresBySyncId.get(syncId);
            if (future != null) {
                futuresBySyncId.remove(syncId);
                future.complete(response);
            }
        }
    }
}
