/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.tcp.HandlerMapping;
import com.codedog.rainbow.tcp.MessageHandler;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.world.net.Payload;
import com.codedog.rainbow.world.net.json.JsonPacket;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * RoleController
 *
 * @author https://github.com/gukt
 */
@Controller
public class GameController {

    /**
     * 灵活的参数绑定
     */
    @HandlerMapping(value = "GameEnterRequest")
    public Object gameEnter(Session session, JsonPacket packet,
                            // List<Object> list,
                            // Set<String> set1,
                            Map<String, Object> payload,
                            Payload payload2,
                            MessageHandler.Error error) {
        // TODO 从payload 中取出 roleId
        System.out.println(packet);

        Long roleId1 = (Long) payload.getOrDefault("roleId", 1L);

        long roleId = Payload.safeGet(payload, "roleId", 1L);
        System.out.println(roleId);

        // System.out.println(payload);

//         Sessions.setRoleId(session, roleId);
//         SessionManager.addSession(session);

        return JsonPacket.of("GameEnterResponse", Payload.of("foo", "bar"));

        // 灵活的参数绑定
        // error.add("出错了");
        // return null;

        // System.out.println(session);
        // return null;
    }

//    @HandlerMapping(value = "GameEnterRequest", types = {GameEnterRequest_VALUE})
//    public Object gameEnter(Session session, JsonPacket request) {
//        long roleId = Payloads.intValue(request, "roleId");
//
//        Sessions.setRoleId(session, roleId);
//        SessionManager.addSession(session);
//
//        return JsonPacket.of("GameEnterResponse", Payload.of("foo", "bar"));
//    }
//
//    @HandlerMapping("ReconnectRequest")
//    public Object reconnect(Session session, JsonPacket request) {
//        long roleId = Payloads.intValue(request, "roleId");
//
//        Optional<Session> old = SessionManager.getSessionById(roleId);
//        if (!old.isPresent()) {
//            JsonPacket.ofError(ErrorCode.ERR_SESSION_EXPIRED).writeTo(session);
//            // TODO 需要在上面发送完的监听中关闭连接
//            session.close();
//        } else {
//            DefaultSession revivedSession = (DefaultSession) old.get();
//            DefaultSession currentSession = (DefaultSession) session;
//
//            // 使用新当前Session的物理连接替换旧Session的物理连接
//            currentSession.attachTo(revivedSession);
//            revivedSession.reopen();
//            // 重新维护连接集合
//            SessionManager.getConnections().remove(session);
//            SessionManager.getConnections().add(revivedSession);
//            // 下发重连成功响应
//            session.write(JsonPacket.of(MsgTypeEnum.RECONNECT_RESPONSE.getText(), Payload.of("toke", System.currentTimeMillis())));
//            // 下发短线后客户端未接收成功的缓存消息
//            List<Object> messages = revivedSession.getStore().retrieveResponsesSince(request.getAck());
//            messages.forEach(session::write);
//        }
//        return null;
//    }
}
