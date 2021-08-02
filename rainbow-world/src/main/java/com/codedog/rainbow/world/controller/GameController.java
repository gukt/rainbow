/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.domain.Role;
import com.codedog.rainbow.tcp.HandlerMapping;
import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.session.Session.State;
import com.codedog.rainbow.util.RandomUtils;
import com.codedog.rainbow.world.SessionService;
import com.codedog.rainbow.world.generated.GameEnterRequest;
import com.codedog.rainbow.world.generated.GameEnterResponse;
import com.codedog.rainbow.world.net.Payload;
import com.codedog.rainbow.world.service.LoggingService;
import com.codedog.rainbow.world.service.RedisService;
import com.codedog.rainbow.world.service.RoleService;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.Map;

import static com.codedog.rainbow.world.net.json.JsonMsgType.RECONNECT_RESPONSE;
import static java.util.Objects.requireNonNull;

/**
 * RoleController
 *
 * @author https://github.com/gukt
 */
@Controller
@Slf4j
public class GameController {

    private final RoleService roleService;
    private final RedisService redisService;
    private final LoggingService loggingService;

    public GameController(RoleService roleService, RedisService redisService, LoggingService loggingService) {
        this.roleService = roleService;
        this.redisService = redisService;
        this.loggingService = loggingService;
    }

    /**
     * 进入游戏
     */
    // @HandlerMapping(value = "GameEnterRequest")
    public Object gameEnter(Session session, GameEnterRequest request) {
        String openId = requireNonNull(request.getOpenId());
        Role role = roleService.findByOpenIdOrCreate(openId, request);

        // 写创建角色日志
        loggingService.recordRoleCreating(role, request);
        // 阻塞直到数据同步完成
        waitIfDataSyncRequired();
        // checkAndNotifySaveDataNow(role.getId());

        // TODO 之前这个 attach 是做什么的？
        // roleService.attach(role);
        session.put("role", role);
        // SessionService.add(session);

        // TODO
        // doGameEnter(role, session, request);
        loggingService.writeRoleEnteringLog(role, request);
        loggingService.writeRoleInfoLog(role);
        session.setState(State.ACTIVE);

        // TODO 记录玩家所在服务器
        // TODO 下线逻辑处，存档完成后，移除玩家-服务器对应消息

        // TODO 这里要保存什么？
        roleService.save(role);

        // TODO 发送事件 GameEnteringEvent

        // 处理完毕，返回响应
        return GameEnterResponse.newBuilder()
                .setRoleId(role.getId())
                .setUid(role.getUid())
                .setOpenId(Strings.nullToEmpty(role.getOpenId()))
                .setNick(Strings.nullToEmpty(role.getNick()))
                .setServerTime(System.currentTimeMillis())
                .setCreatedAt(role.getCreatedAt().getTime())
                .build();
    }

    // JSON

    /**
     * 进入游戏
     */
    @HandlerMapping(value = "GameEnterRequest")
    public Object gameEnter2(Session session, Map<String, Object> payload) {
        String openId = (String) payload.get("openId");
        Role role = roleService.findByOpenIdOrCreate2(openId, payload);
        // 阻塞直到数据同步完成
        waitIfDataSyncRequired();
        session.put("role", role);
        // SessionService.add(session);
        session.setState(State.ACTIVE);

        // TODO 记录玩家所在服务器
        // TODO 下线逻辑处，存档完成后，移除玩家-服务器对应消息

        // TODO 这里要保存什么？
        roleService.save(role);

        // TODO 发送事件 GameEnteringEvent

        // 处理完毕，返回响应
        return GameEnterResponse.newBuilder()
                .setRoleId(role.getId())
                .setUid(role.getUid())
                .setOpenId(Strings.nullToEmpty(role.getOpenId()))
                .setNick(Strings.nullToEmpty(role.getNick()))
                .setServerTime(System.currentTimeMillis())
                .setCreatedAt(role.getCreatedAt().getTime())
                .build();
    }


    @HandlerMapping("ReconnectRequest")
    public Object reconnect2(DefaultSession session, Map<?, ?> payload) {
        Long roleId = (Long) payload.get("roleId");
        DefaultSession prev = SessionService.findById3(roleId);
        if (prev == null) {
            // 给当前连接发送消息，完成后关闭该连接
            // TODO
            // ERR_SESSION_NOT_FOUND.toJsonPaket().writeTo(session).whenComplete((unused, throwable) -> session.close());
            return null;
        }
        // 如果找到旧的连接（Session），则重用它
        prev.reuseBy(session);

        session.write(JsonPacket.of(RECONNECT_RESPONSE.getText(), Payload.EMPTY));
        // TODO 下发短线后客户端未接收成功的缓存消息
        // List<Object> messages = prev.getStore().retrieveResponsesSince(packet.getAck());
        // messages.forEach(session::write);
        return null;
    }

    // Private methods

    /**
     * 检查玩家此前是否在其他服务器上登陆，且数据还没落地
     * 如果是，则通知其他服务器存档,阻塞直到其他服务器存档完成。
     */
    @SneakyThrows
    private void waitIfDataSyncRequired() {
        // TODO 从 redis 中找出玩家所在的 id，如果还有，就继续等。
        log.info("正在等待其他服务器数据落地...");
        // 模拟数据落地时间
        Thread.sleep(RandomUtils.nextInt(3000, 5000));
        log.info("其他服务器数据已落地成功...");
    }
}
