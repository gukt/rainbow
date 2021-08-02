/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp;

import com.codedog.rainbow.core.AbstractLifecycle;
import com.codedog.rainbow.core.LifecycleException;
import com.codedog.rainbow.core.NetworkService;
import com.codedog.rainbow.tcp.TcpProperties.WebSocketProperties;
import com.codedog.rainbow.tcp.codec.WebSocketFrameHandler;
import com.codedog.rainbow.tcp.codec.json.JsonDecoder;
import com.codedog.rainbow.tcp.codec.json.JsonEncoder;
import com.codedog.rainbow.tcp.codec.protobuf.ProtobufEncoder;
import com.codedog.rainbow.tcp.codec.protobuf.ProtobufFixed32FrameDecoder;
import com.codedog.rainbow.tcp.codec.protobuf.ProtobufFixed32LengthFieldPrepender;
import com.codedog.rainbow.tcp.json.JsonPacketMessageResolver;
import com.codedog.rainbow.tcp.json.JsonPacketTcpServerChannelHandler;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.world.generated.CommonProto;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TcpServer表示一个Tcp服务
 * // TODO 添加服务器相关统计处理handler
 * // TODO 添加服务器流量整形handler
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class TcpServer extends AbstractLifecycle implements NetworkService {

    private final TcpProperties properties;
    private final TcpServerChannelHandler<?> channelHandler;
    private final MessageDispatcher dispatcher;
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private int realPort = -1;
    /**
     * 用以保存所有 {@link Session} 的集合。
     */
    private static final List<Session> SESSIONS = new CopyOnWriteArrayList<>();

    public TcpServer(TcpProperties properties, TcpServerChannelHandler<?> channelHandler, MessageDispatcher dispatcher) {
        Assert.notNull(properties, "properties");
        Assert.notNull(channelHandler, "channelHandler");
        Assert.notNull(dispatcher, "dispatcher");

        this.properties = properties;
        this.channelHandler = channelHandler;
        this.dispatcher = dispatcher;
    }

    /**
     * 启动服务器，启动成功后会一直阻塞直到关闭。
     * TODO 需仔细设计这里的异常及退出机制
     *
     * @throws IllegalStateException 如果已经启动
     * @throws LifecycleException    如果被中断
     */
    @Override
    public void start() {
        requireStateNew();

        log.info("TCP - 🚀 Starting TcpServer");
        startTime = System.currentTimeMillis();
        setState(State.STARTING);
        MessageProtocol messageProtocol = properties.getMessageProtocol();
        if (messageProtocol == null) {
            fail("TCP - Message protocol: null (expected: json/protobuf)");
        }
        final EventLoopGroup bossGroup = new NioEventLoopGroup(1,
                new ThreadFactoryBuilder().setNameFormat(properties.getBossThreadName()).build());
        final EventLoopGroup workerGroup = new NioEventLoopGroup(0,
                new ThreadFactoryBuilder().setNameFormat(properties.getWorkerThreadPattern()).build());
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws CertificateException, SSLException {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(properties.getKeepAliveTimeout(), 0, 0));
                        // WebSocket enabled?
                        if (properties.isWebSocketEnabled()) {
                            WebSocketProperties wsConfig = properties.getWebsocket();
                            // 配置 SSL
                            if (wsConfig.isSslEnabled()) {
                                SelfSignedCertificate ssc = new SelfSignedCertificate();
                                SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(wsConfig.getMaxContentLen()));
                            //      pipeline.addLast(new ChunkedWriteHandler());
                            p.addLast(new WebSocketServerCompressionHandler());
                            p.addLast(new WebSocketServerProtocolHandler(wsConfig.getPath(), null, true, wsConfig.getMaxFrameSize()));
                            p.addLast(new WebSocketFrameHandler());
                        }

                        if (messageProtocol == MessageProtocol.JSON) {
                            p.addLast(new JsonObjectDecoder());
                            p.addLast(new JsonDecoder());
                            p.addLast(new JsonEncoder(properties.isWebSocketEnabled()));
                        } else if (messageProtocol == MessageProtocol.PROTOBUF) {
                            p.addLast(new ProtobufFixed32FrameDecoder());
                            p.addLast(new ProtobufDecoder(CommonProto.ProtoPacket.getDefaultInstance()));
                            p.addLast(new ProtobufFixed32LengthFieldPrepender());
                            p.addLast(new ProtobufEncoder());
                        }
                        p.addLast(channelHandler);
                    }
                });
        // 绑定并监听指定的端口，如果端口设为 0，则会分配随机端口
        ChannelFuture future = serverBootstrap.bind(properties.getPort())
                .addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        log.error("TCP - 启动 TcpServer 时发生绑定异常");
                        fail("启动 TcpServer 时发生绑定异常");
                    }
                    // 获得真实绑定的端口
                    this.realPort = ((InetSocketAddress) f.channel().localAddress()).getPort();
                    // 启动 Message dispatcher
                    // TODO 是否需要返回 Future，根据 Future 设定 channelHandler.active
                    dispatcher.start();
                    channelHandler.setActive(true);
                    setState(State.RUNNING);

                    log.info("TCP - Started TcpServer in {} seconds, listening on {}.",
                            ((double) (System.currentTimeMillis() - startTime)) / 1000, getRealPort());
                });
        try {
            // 阻塞，直到 channel 关闭
            future.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            fail("TCP - TcpServer was interrupted", e);
        }
    }

    /**
     * 停止 TcpServer，如果停止成功，则可以保证如下几点:
     * <p> 1. 拒绝接收新连接
     * <p> 2. 拒绝接收新连接发来的请求
     * <p> 3. 停止 MessagePumper，积压的后续请求不再处理
     * <p> 4. 停止业务线程池，并且等待所有正在执行的任务执行完毕
     * <p> 5. 退出 Netty 网络服务
     */
    @Override
    public void stop() {
        requireStateRunning();
        log.info("TCP - Stopping TcpServer");

        setState(State.STOPPING);
        // 首先设置tcpServerHandler为inactive
        // 此时，tcpServerHandler将拒绝接受新连接，以及已有连接发送的新请求
        this.channelHandler.setActive(false);
        log.info("TCP - Tcp server handler was disabled, any incoming connections or requests will be rejected.");
        // 停止消息派发，每个连接积压的请求将不再继续派发
        // 正在处理的请求会等待他们执行完毕
        dispatcher.stop();
        // Shutdown the netty gracefully
        serverBootstrap.config().group().shutdownGracefully();
        serverBootstrap.config().childGroup().shutdownGracefully();
        setState(State.TERMINATED);

        log.info("TCP - TcpServer terminated.");
    }

    @Override
    public int getRealPort() {
        requireStateRunning();
        return realPort;
    }

    /**
     * 获取当前所有 {@link Session} 的集合（即包含：{@link Session.State} 中定义的所有状态的 Session）。
     *
     * @return 当前所有 {@link Session} 的集合（即包含：{@link Session.State} 中定义的所有状态的 Session）
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getSessions() {
        return SESSIONS;
    }

    /**
     * 获取当前处于某 {@link Session.State 状态} 的所有 {@link Session} 的集合。
     *
     * @param state {@link Session.State 状态}
     * @return 当前处于某 {@link Session.State 状态} 的所有 {@link Session} 的集合
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getSessions(Session.State state) {
        return null;
    }

    /**
     * 获取当前处于 {@link Session.State#ACTIVE} 状态的所有 {@link Session} 的集合。
     *
     * @return 当前处于 {@link Session.State#ACTIVE} 状态的所有 {@link Session} 的集合
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getActiveSessions() {
        return getSessions(Session.State.ACTIVE);
    }

    /**
     * 获取当前处于某 {@link Session.State#DISCONNECTED} 状态的所有 {@link Session} 个数。
     *
     * @return 当前处于某 {@link Session.State#DISCONNECTED} 状态的所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static Collection<Session> getDisconnectedSessions() {
        return getSessions(Session.State.DISCONNECTED);
    }

    // getSessionCount

    /**
     * 获取当前所有 {@link Session} 的个数（即：{@link Session.State} 中定义的所有状态的 Session）。
     * <p>如果你希望获取处于某个 {@link Session.State 状态} 的 Session 个数，请使用 {@link #getSessionCount(Session.State)}
     *
     * @return 当前所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getSessionCount() {
        return SESSIONS.size();
    }

    /**
     * 获取当前处于某 {@link Session.State 状态} 的所有 {@link Session} 个数。
     * <p>如果你希望获取所有 {@link Session.State 状态} 的 Session 个数，请使用 {@link #getSessionCount()}
     *
     * @param state {@link Session.State 状态}，不能为 null
     * @return 当前所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getSessionCount(Session.State state) {
        Assert.notNull(state, "state");
        switch (state) {
            case ACTIVE:
                return SESSIONS.stream().filter(Session::isActive).count();
            case DISCONNECTED:
                return SESSIONS.stream().filter(Session::isDisconnected).count();
        }
        return 0;
    }

    /**
     * 获取当前处于 {@link Session.State#ACTIVE} 状态的所有 {@link Session} 个数。
     *
     * @return 当前处于 {@link Session.State#ACTIVE} 状态的所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getActiveSessionCount() {
        return getSessionCount(Session.State.ACTIVE);
    }

    /**
     * 获取当前处于某 {@link Session.State#DISCONNECTED} 状态的所有 {@link Session} 个数。
     *
     * @return 当前处于某 {@link Session.State#DISCONNECTED} 状态的所有 {@link Session} 个数
     * @apiNote 因为会不断有新连接的产生、以及旧连接的过期清理，所以这里返回的是一个近似值。
     */
    public static long getDisconnectedSessionCount() {
        return getSessionCount(Session.State.DISCONNECTED);
    }

    /**
     * 添加一个 Session 对象到集合中。
     *
     * @param session session 对象，不能为 null
     * @throws IllegalArgumentException 如果 session 为 null
     */
    public static void addSession(Session session) {
        Assert.notNull(session, "session");
        SESSIONS.add(session);
    }

    /**
     * 从 {@link #SESSIONS Session 集合} 中移除指定的 {@link Session} 对象。
     *
     * @param session Session 对象，不能为 null
     * @throws IllegalArgumentException 如果 session 为 null
     */
    public static void removeSession(Session session) {
        Assert.notNull(session, "session");
        SESSIONS.remove(session);
    }

    public static void main(String[] args) {
        TcpProperties properties = new TcpProperties();
        // TODO 是不是应该将 MessageResolver 弄到 TcpServerContext 中去？
        properties.setMessageResolver(new JsonPacketMessageResolver());
        TcpServerChannelHandler<JsonPacket> channelHandler = new JsonPacketTcpServerChannelHandler(properties);
        MessageDispatcher dispatcher = new DefaultMessageDispatcher<>(properties);
        TcpServer tcpServer = new TcpServer(properties, channelHandler, dispatcher);
        tcpServer.start();
    }
}
