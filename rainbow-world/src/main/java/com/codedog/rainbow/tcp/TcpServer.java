/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.core.AbstractLifecycle;
import com.codedog.rainbow.core.LifecycleException;
import com.codedog.rainbow.core.NetworkService;
import com.codedog.rainbow.world.config.TcpProperties;
import com.codedog.rainbow.tcp.codec.json.JsonDecoder;
import com.codedog.rainbow.tcp.codec.json.JsonEncoder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * TcpServer表示一个Tcp服务
 * // TODO 添加服务器相关统计处理handler
 * // TODO 添加服务器流量整形handler
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class TcpServer extends AbstractLifecycle implements NetworkService {

    private final TcpProperties tcpProperties;
    private final AbstractTcpServerHandler<?> tcpServerHandler;
    private final MessageDispatcher dispatcher;
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private int realPort = -1;

    public TcpServer(TcpProperties tcpProperties, AbstractTcpServerHandler<?> channelHandler, MessageDispatcher dispatcher) {
        this.tcpProperties = tcpProperties;
        this.tcpServerHandler = channelHandler;
        this.dispatcher = dispatcher;
    }

    /**
     * 启动服务器，启动成功后会一直阻塞直到关闭
     * TODO 需仔细设计这里的异常及退出机制
     *
     * @throws IllegalStateException 如果已经启动
     * @throws LifecycleException    如果被中断
     */
    @Override
    public void start() {
        requireStateNew();

        log.info("TCP: Starting TcpServer ...");
        startTime = System.currentTimeMillis();
        setState(State.STARTING);
        String protocol = tcpProperties.getProtocol();
        // 检查协议格式是否设置正确
        if (Arrays.stream(MessageProtocolType.values()).noneMatch(p -> p.name().equalsIgnoreCase(protocol))) {
            fail("Invalid protocol format: " + tcpProperties.getProtocol() + ", Expected: json/protobuf", null);
        }

        final EventLoopGroup bossGroup = new NioEventLoopGroup(1, new ThreadFactoryBuilder()
                .setNameFormat(tcpProperties.getBossThreadName()).build());
        final EventLoopGroup workerGroup = new NioEventLoopGroup(0, new ThreadFactoryBuilder()
                .setNameFormat(tcpProperties.getWorkerThreadPattern()).build());
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // TODO 了解并添加其他TCP控制选项
                .option(ChannelOption.TCP_NODELAY, true)
                // .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (MessageProtocolType.JSON.name().equalsIgnoreCase(protocol)) {
                            p.addLast(new JsonObjectDecoder());
                            p.addLast(new JsonDecoder());
                            p.addLast(new JsonEncoder());
                        } else if (MessageProtocolType.PROTOBUF.name().equalsIgnoreCase(protocol)) {
                            // TODO 放开它们
                            // p.addLast(new ProtobufFixed32FrameDecoder());
                            // p.addLast(new ProtobufDecoder(ProtoPacket.getDefaultInstance()));
                            // p.addLast(new ProtobufFixed32LengthFieldPrepender());
                            // p.addLast(new ProtobufEncoder());
                        }
                        // 心跳设置
                        p.addLast(new IdleStateHandler(tcpProperties.getKeepAliveTimeout(), 0, 0));
                        p.addLast(tcpServerHandler);
                    }
                });
        // 绑定并监听指定的端口，如果端口设置为 0 则会分配随机端口
        ChannelFuture future = bootstrap.bind(tcpProperties.getPort())
                .addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        log.error("TCP: 启动 TcpServer 时发生绑定异常");
                    }
                    dispatcher.start();
                    this.realPort = ((InetSocketAddress) f.channel().localAddress()).getPort();
                    // 允许接收请求
                    tcpServerHandler.setActive(true);
                    // 设状态为 Running
                    setState(State.RUNNING);
                    log.info("TCP: Started TcpServer in {} seconds , listening on {}",
                            ((double) (System.currentTimeMillis() - startTime)) / 1000, getRealPort());
                });
        try {
            // Block until channel closed
            future.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            fail("TcpServer was interrupted", e);
        }
    }

    /**
     * 停止 TcpServer，如果停止成功，则可以保证如下几点:
     * <ul>
     * <li>1. 拒绝接收新连接</li>
     * <li>2. 拒绝接收新连接发来的请求</li>
     * <li>3. 停止 MessagePumper，积压的后续请求不再处理</li>
     * <li>4. 停止业务线程池，并且等待所有正在执行的任务执行完毕</li>
     * <li>5. 退出 Netty 网络服务</li>
     * </ul>
     */
    @Override
    public void stop() {
        requireStateRunning();
        log.info("TCP: Stopping TcpServer");

        setState(State.STOPPING);
        // 首先设置tcpServerHandler为inactive
        // 此时，tcpServerHandler将拒绝接受新连接，以及已有连接发送的新请求
        this.tcpServerHandler.setActive(false);
        log.info("TCP: Tcp server handler was disabled, any incoming connections or requests will be rejected.");
        // 停止消息派发，每个连接积压的请求将不再继续派发
        // 正在处理的请求会等待他们执行完毕
        dispatcher.stop();
        // stop netty
        bootstrap.config().group().shutdownGracefully();
        bootstrap.config().childGroup().shutdownGracefully();
        setState(State.TERMINATED);

        log.info("TCP: TcpServer terminated.");
    }

    @Override
    public int getRealPort() {
        requireStateRunning();
        return realPort;
    }
}
