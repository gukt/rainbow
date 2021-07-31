/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.domain.Role;
import com.codedog.rainbow.tcp.HandlerMapping;
import com.codedog.rainbow.tcp.session.DefaultSession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.session.Session.State;
import com.codedog.rainbow.world.generated.GameEnterRequest;
import com.codedog.rainbow.world.generated.GameEnterResponse;
import com.codedog.rainbow.world.net.ErrorCode;
import com.codedog.rainbow.world.net.Payload;
import com.codedog.rainbow.world.net.SessionService;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.json.MsgTypeEnum;
import com.codedog.rainbow.world.service.LoggingService;
import com.codedog.rainbow.world.service.RoleService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

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
    private final LoggingService loggingService;

    public GameController(RoleService roleService, LoggingService loggingService) {
        this.roleService = roleService;
        this.loggingService = loggingService;
    }

    /**
     * 灵活的参数绑定
     */
    @HandlerMapping(value = "GameEnterRequest")
    public Object gameEnter(Session session, GameEnterRequest request) {
        String openId = requireNonNull(request.getOpenId());
        Role role = roleService.findByOpenIdOrCreate(openId, request);

        // 写创建角色日志
        loggingService.recordRoleCreating(role, request);

        // TODO 检查玩家当前是否在其他服务器上上线, 如果是，则通知其他服务器存档后，此处才可以进入登录
        //
        // TODO 如果是新创建的角色，这里加载会加载空数据到redisDataService中
//            checkAndNotifySaveDataNow(role.getId());

        // TODO 临时方案，这里重新从数据库中加载到缓存，不然缓存和hibernate缓存数据不
//            role = roleService.getRoleById(role.getId());

        // TODO 之前这个 attach 是做什么的？
        // roleService.attach(role);
        session.put("role", role);
        SessionService.add(session);

        // TODO
        // doGameEnter(role, session, request);

        loggingService.recordRoleEntering(role, request);
        loggingService.recordRoleInfo(role);
        session.setState(State.ACTIVE);

        // TODO 设置在线状态为online
        // redisDataService.cacheRoleInfo(role.getId(), "online", true);

        roleService.save(role);

        // TODO 发送事件 GameEnteringEvent

        return GameEnterResponse.newBuilder()
                .setRoleId(role.getId())
                .setUid(role.getUid())
                .setOpenId(Strings.nullToEmpty(role.getOpenId()))
                .setNick(Strings.nullToEmpty(role.getNick()))
                .setServerTime(System.currentTimeMillis())
                .setCreatedAt(role.getCreatedAt().getTime())
                .build();
    }

    // /**
    //  * 灵活的参数绑定
    //  */
    // @HandlerMapping(value = "GameEnterRequest")
    // public Object gameEnter( GameEnterRequest request) {
    //
    //     // 这种情况，貌似和使用 MapHelper 比较起来，语句更短一点，语义上也差不多
    //     Long roleId01 = (Long) payload.get("roleId");
    //     Long roleId11 = MapHelper.getLong(payload, "roleId");
    //     // 那就只能这样了，
    //     Long roleId22 = ((Number) payload.get("roleId")).longValue();
    //     // 但是。。。
    //     // 如果为空就会报空指针异常
    //     Number roleId23 = (Number) payload.get("roleId");
    //     roleId23 = roleId23 != null ? roleId23.longValue() : null;
    //
    //     // 但是，JSON 序列化往往不像设想的那样，有时它会给你制造点麻烦，
    //     // 比如 roleId 你传的比较短，它会解析成 Integer，长了解析成 Long
    //     // 如果转出 Integer，但你要取 Long 型，这时强制转换为出错 ：
    //     // (Long) payload.get("roleId") 抛出 ClassCastException
    //
    //     Long roleId02 = (Long) payload.getOrDefault("roleId", 1L);
    //     Integer roleId12 = MapHelper.getInt(payload, "roleId");
    //
    //     // MapHelper mapHelper = MapHelper.of(payload);
    //     // Long roleId = mapHelper.getLong("roleId");
    //     // Integer rolId2 = mapHelper.getInt("roleId");
    //
    //     // TODO 从payload 中取出 roleId
    //     System.out.println(packet);
    //
    //     // Long roleId1 = (Long) payload.getOrDefault("roleId", 1L);
    //     //
    //     // long roleId = Payload.safeGet(payload, "roleId", 1L);
    //     // System.out.println(roleId);
    //
    //     // System.out.println(payload);
    //
    //     // Sessions.setRoleId(session, roleId);
    //     // SessionManager.addSession(session);
    //
    //     return JsonPacket.of("GameEnterResponse", Payload.of("foo", "bar"));
    //
    //     // 灵活的参数绑定
    //     // error.add("出错了");
    //     // return null;
    //
    //     // System.out.println(session);
    //     // return null;
    // }

    @HandlerMapping("ReconnectRequest")
    public Object reconnect(Session session, JsonPacket packet, Map<?, ?> payload) {
        Long roleId = (Long) payload.get("roleId");
        Session prev = SessionService.findById(roleId);
        if (prev == null) {
            ErrorCode.ERR_SESSION_EXPIRED.toJsonPaket().writeTo(session);
            // TODO 需要在上面发送完的监听中关闭连接
            session.close();
            return null;
        }
        DefaultSession current = (DefaultSession) session;

        // 使用新当前Session的物理连接替换旧Session的物理连接
        current.reuse((DefaultSession) prev);
        // 重新维护连接集合
        SessionService.remove(session);
        SessionService.add(prev);
        // 下发重连成功响应
        session.write(JsonPacket.of(MsgTypeEnum.RECONNECT_RESPONSE.getText(), Payload.of("toke", System.currentTimeMillis())));
        // 下发短线后客户端未接收成功的缓存消息
        List<Object> messages = prev.getStore().retrieveResponsesSince(packet.getAck());
        messages.forEach(session::write);
        return null;
    }
}
