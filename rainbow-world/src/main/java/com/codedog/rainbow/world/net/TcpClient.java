/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net;

import com.codedog.rainbow.world.net.codec.json.JsonDecoder;
import com.codedog.rainbow.world.net.codec.json.JsonEncoder;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.google.common.math.IntMath;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by gukaitong(29283212@qq.com) on 2015/11/12.
 * TODO 需进一步深入研究
 *
 * @author gukaitong
 * @since 1.0
 */
@Slf4j
public class TcpClient {

    private String host;
    private int port;
    private EventLoopGroup group = new NioEventLoopGroup();
    private ConcurrentHashMap<Integer, CompletableFuture<JsonPacket>> futuresBySyncId = new ConcurrentHashMap<>();
    private AtomicInteger nextSeq = new AtomicInteger(0);
    private ChannelHandler channelHandler = new ClientChannelHandler();
    private Bootstrap b = new Bootstrap();
    private Channel channel;
    private AtomicInteger nextSync = new AtomicInteger(0);
    private AtomicInteger nextAck = new AtomicInteger(1);

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.init();
    }

    public TcpClient(int port) {
        this("localhost", port);
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

    public void write(JsonPacket request) {
        requireConnected();

        request.setSeq(nextSeq.incrementAndGet());
        request.setAck(nextAck.get());
        request.setTime(System.currentTimeMillis());

        log.debug("CLIENT: Sending: {}", request);
        channel.writeAndFlush(request);
    }

    public CompletableFuture<JsonPacket> writeSync(JsonPacket request) {
        requireConnected();

        CompletableFuture<JsonPacket> future = new CompletableFuture<>();
        request.setSeq(nextSeq.incrementAndGet());
        request.setAck(nextAck.get());
        request.setTime(System.currentTimeMillis());
        request.setSync(nextSync.incrementAndGet());
        futuresBySyncId.put(request.getSync(), future);
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
    private final class ClientChannelHandler extends SimpleChannelInboundHandler<JsonPacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, JsonPacket response) {
            log.debug("CLIENT: Receiving: {}", response);
            // 递增ack序号
            nextAck.incrementAndGet();

            int syncId = response.getSync();
            CompletableFuture<JsonPacket> future = futuresBySyncId.get(syncId);
            if (future != null) {
                futuresBySyncId.remove(syncId);
                future.complete(response);
            }
        }
    }
}
