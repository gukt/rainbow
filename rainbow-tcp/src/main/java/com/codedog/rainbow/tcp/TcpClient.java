/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.tcp.codec.json.JsonDecoder;
import com.codedog.rainbow.tcp.codec.json.JsonEncoder;
import com.codedog.rainbow.tcp.codec.protobuf.ProtobufEncoder;
import com.codedog.rainbow.tcp.codec.protobuf.ProtobufFixed32FrameDecoder;
import com.codedog.rainbow.tcp.codec.protobuf.ProtobufFixed32LengthFieldPrepender;
import com.codedog.rainbow.tcp.message.JsonPacket;
import com.codedog.rainbow.tcp.message.MessageProtocol;
import com.codedog.rainbow.tcp.util.ProtoUtils;
import com.codedog.rainbow.world.generated.CommonProto;
import com.codedog.rainbow.world.generated.CommonProto.ProtoPacket;
import com.google.common.math.IntMath;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TcpClient
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class TcpClient {

    private final String host;
    private final int port;
    private final ConcurrentHashMap<Integer, CompletableFuture<Object>> futuresBySyncId = new ConcurrentHashMap<>();
    private final Bootstrap clientBootstrap = new Bootstrap();
    private Channel channel;
    private final MessageProtocol messageProtocol = MessageProtocol.PROTOBUF;
    private final AtomicInteger nextSeq = new AtomicInteger(0);
    private final AtomicInteger nextSync = new AtomicInteger(0);
    private final AtomicInteger nextAck = new AtomicInteger(1);
    private final static int MAX_RETRY_COUNT = 3;

    public TcpClient(int port) {
        this("localhost", port);
    }

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.init();
    }

    private void init() {
        clientBootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (messageProtocol == MessageProtocol.JSON) {
                            p.addLast(new JsonObjectDecoder());
                            p.addLast(new JsonDecoder());
                            p.addLast(new JsonEncoder());
                        } else if (messageProtocol == MessageProtocol.PROTOBUF) {
                            p.addLast(new ProtobufFixed32FrameDecoder());
                            p.addLast(new ProtobufDecoder(CommonProto.ProtoPacket.getDefaultInstance()));
                            p.addLast(new ProtobufFixed32LengthFieldPrepender());
                            p.addLast(new ProtobufEncoder());
//                        }
                            p.addLast(new ClientChannelHandler());
                        }
                    }
                });
    }

    private void connect() {
        int retryCount = 0;
        do {
            try {
                // 尝试连接到指定的主机
                channel = clientBootstrap.connect(host, port).sync().channel();
                log.info("TCP CLIENT - 连接成功: {}", channel);
                return;
            } catch (Exception e) {
                log.error("TCP CLIENT - 连接异常:", e);
            }
            ++retryCount;
            try {
                TimeUnit.SECONDS.sleep(Math.min(8, IntMath.pow(2, retryCount)));
            } catch (InterruptedException ignored) {
                log.error("TCP CLIENT - 终止连接!");
            }
            log.info("TCP CLIENT - [{}/{}] 正在尝试连接到服务器({}:{})", retryCount, MAX_RETRY_COUNT, host, port);
        } while (retryCount <= MAX_RETRY_COUNT);
        log.info("TCP CLIENT - 超出最大重连次数，请检查服务器是否已启动.");
    }

    public CompletableFuture<Object> send(Object request) {
        return send(request, false);
    }

    public CompletableFuture<Object> send(Object request, boolean sync) {
        requireConnected();
        CompletableFuture<Object> future = new CompletableFuture<>();
        if (request == null) {
            future.complete(null);
        } else {
            log.info("TCP CLIENT - 正在发送请求: {} - {}", request.getClass().getSimpleName(), request);
            channel.writeAndFlush(beforeWrite(request, sync)).addListener(f -> future.complete(request));
        }
        return future;
    }

    public Object beforeWrite(Object message, boolean sync) {
        if (message instanceof JsonPacket) {
            JsonPacket msg = (JsonPacket) message;
            msg.setSn(nextSeq.incrementAndGet());
            msg.setTime(System.currentTimeMillis());
            if (sync) {
                int syncId = nextSync.incrementAndGet();
                msg.setSync(syncId);
                futuresBySyncId.put(syncId, new CompletableFuture<>());
            }
            return msg;
        } else if (message instanceof MessageLiteOrBuilder) {
            MessageLiteOrBuilder msg = (MessageLiteOrBuilder) message;
            if (!(msg instanceof ProtoPacket)) {
                msg = ProtoUtils.wrapPacket(message);
            }
            ProtoPacket.Builder builder = ProtoUtils.safeGetBuilder(msg);
            builder.setSn(nextSeq.incrementAndGet()).setTime(System.currentTimeMillis());
            if (sync) {
                int syncId = nextSync.incrementAndGet();
                builder.setSync(syncId);
                futuresBySyncId.put(syncId, new CompletableFuture<>());
            }
            return builder.build();
        }
        throw new RuntimeException("Unsupported protocol");
    }

    public Integer getSyncId(Object message) {
        if (messageProtocol == MessageProtocol.JSON) {
            return ((JsonPacket) message).getSync();
        } else {
            return ((ProtoPacket) message).getSync();
        }
    }

    /**
     * 确保客户端到服务器的连通性。如果还没有连接或已断开，会自动连接或重连。
     */
    private void requireConnected() {
        if (channel == null || !channel.isOpen()) {
            this.connect();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (channel != null && channel.isOpen()) {
            log.info("TCP CLIENT - Disconnecting {}", channel);
            channel.close();
        }
        clientBootstrap.config().group().shutdownGracefully();
    }

    @Sharable
    private final class ClientChannelHandler extends SimpleChannelInboundHandler<Object> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object response) {
            log.debug("TCP CLIENT - Receiving: {}", response);
            nextAck.incrementAndGet();
            int syncId = getSyncId(response);
            CompletableFuture<Object> future = futuresBySyncId.get(syncId);
            if (future != null) {
                futuresBySyncId.remove(syncId);
                future.complete(response);
            }
        }
    }
}
