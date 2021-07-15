/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.net.json;

import static com.codedog.rainbow.world.net.NetConstants.SESSION_KEY;

import com.codedog.rainbow.world.Encrypts;
import com.codedog.rainbow.world.EventPublisher;
import com.codedog.rainbow.world.GameOptions;
import com.codedog.rainbow.world.net.DefaultSession;
import com.codedog.rainbow.world.net.MessageInterceptor;
import com.codedog.rainbow.world.net.Session;
import com.codedog.rainbow.world.net.SessionClosingEvent;
import com.codedog.rainbow.world.net.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractTcpServerHandler
 * 创建TcpServerHandler对象能不依赖具体类型吗
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Slf4j
public abstract class AbstractTcpServerHandler<T> extends SimpleChannelInboundHandler<T> {

    protected final GameOptions opts;
    private final EventPublisher eventPublisher;
    @Getter
    @Setter
    protected volatile boolean active;

    /**
     * 消息拦截器列表,供外部调用，动态添加interceptors用的
     */
    @Getter
    private final List<MessageInterceptor<T>> interceptorList = new ArrayList<>();
    /**
     * 消息拦截器列表，内部循环使用的
     */
    private MessageInterceptor<T>[] interceptors;

    AbstractTcpServerHandler(GameOptions opts, EventPublisher eventPublisher) {
        this.opts = opts;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.debug("TCP: New client connected: {}", channel);
        // 实例化Session放到ctx的自定义属性中
        channel.attr(SESSION_KEY).setIfAbsent(new DefaultSession(ctx, opts.getSessionMaxPendingRequestSize(),
                opts.getSessionMaxCacheResponseSize()) {
            @Override
            public boolean beforeWrite(@NonNull Object message) {
                JsonPacket msg = (JsonPacket) message;
                int seq = store.incrementSeq();
                long now = System.currentTimeMillis();
                msg.setSeq(seq);
                msg.setTime(now);
                msg.setSync(((JsonPacket) processingRequest).getSync());
                // 将该消息压缩后缓存起来
                store.getCachedResponses().write(new Object[]{seq, Encrypts.encrypt(message)});
                return true;
            }
        });
        super.channelActive(ctx);

        // add active connection
        SessionManager.getConnections().add(ctx.channel().attr(SESSION_KEY).get());
    }

    boolean isOverload() {
        // TODO 根据线程池或其他资源监测机制，判断当前服务器是否过载
        return false;
    }

    boolean isMaxConnectionsExceeded() {
        // 获取当前所有的连接总数(包括暂时离线的）
        int connCount = SessionManager.getConnectionCount() + SessionManager.getOfflineRoleCount();
        return connCount >= opts.getTcpMaxConnections();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("TCP: exceptionCaught: {}, {}", ctx.channel(), cause.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("TCP: Client disconnected：{}", ctx.channel());

        Session session = ctx.channel().attr(SESSION_KEY).get();
        // fire event
        eventPublisher.fireAsync(new SessionClosingEvent(session), true);
        // remove session from SessionManager
        SessionManager.getConnections().remove(session);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE || e.state() == IdleState.ALL_IDLE) {
                log.info("TCP: Connection timeout: {}", ctx.channel());
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 获得拦截器数组
     */
    @SuppressWarnings("unchecked")
    private MessageInterceptor<T>[] getInterceptors() {
        if (this.interceptors == null) {
            this.interceptors = new MessageInterceptor[interceptorList.size()];
            this.interceptors = interceptorList.toArray(interceptors);
        }
        return this.interceptors;
    }

    /**
     * 应用拦截器链中的所有拦截器的preHandle方法
     *
     * @param session Session对象
     * @param packet Packet对象
     * @return 拦截器链中的所有拦截器只要有一个拦截器返回false，则该方法返回false，所有拦截器全部成功则返回true
     */
    boolean applyPreHandle(Session session, T packet) {
        MessageInterceptor<T>[] interceptors = getInterceptors();
        if (interceptors == null || interceptors.length == 0) {
            return true;
        }
        return Arrays.stream(interceptors).allMatch(i -> i.preHandle(session, packet));
    }

    /**
     * 应用拦截器链中的所有拦截器的postHandle方法
     *
     * @param session Session object
     * @param request Packet object
     */
    void applyPostHandle(Session session, T request) {
        MessageInterceptor<T>[] interceptors = getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            // invoked in reverse order
            for (int i = interceptors.length - 1; i >= 0; i--) {
                MessageInterceptor<T> interceptor = interceptors[i];
                interceptor.postHandle(session, request);
            }
        }
    }

    /**
     * 发送消息后中断连接
     */
    void rejectConnection(ChannelHandlerContext ctx, T msg) {
        log.debug("TCP: Client connection was rejected: {}, {}", msg, ctx.channel().remoteAddress());
        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }
}
