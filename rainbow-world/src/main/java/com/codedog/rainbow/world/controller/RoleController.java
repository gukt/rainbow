/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.world.net.ErrorCodeEnum;
import com.codedog.rainbow.world.net.json.JsonPacket;
import com.codedog.rainbow.world.net.json.MsgTypeEnum;
import com.codedog.rainbow.world.net.DefaultSession;
import com.codedog.rainbow.world.net.HandlerMapping;
import com.codedog.rainbow.world.net.Payload;
import com.codedog.rainbow.world.net.Payloads;
import com.codedog.rainbow.world.net.Session;
import com.codedog.rainbow.world.net.SessionManager;
import com.codedog.rainbow.world.util.Sessions;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;

/**
 * RoleController
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Controller
public class RoleController {

    @HandlerMapping("GameEnterRequest")
    public Object gameEnter(Session session, JsonPacket request) {
        long roleId = Payloads.intValue(request, "roleId");

        Sessions.setRoleId(session, roleId);
        SessionManager.addSession(session);

        return JsonPacket.of("GameEnterResponse", Payload.of("foo", "bar"));
    }

    @HandlerMapping("ReconnectRequest")
    public Object reconnect(Session session, JsonPacket request) {
        long roleId = Payloads.intValue(request, "roleId");

        Optional<Session> old = SessionManager.getSessionById(roleId);
        if (!old.isPresent()) {
            JsonPacket.ofError(ErrorCodeEnum.ERR_RECONNECT_EXPIRED).writeTo(session);
            // TODO 需要在上面发送完的监听中关闭连接
            session.close();
        } else {
            DefaultSession revivedSession = (DefaultSession) old.get();
            DefaultSession currentSession = (DefaultSession) session;

            // 使用新当前Session的物理连接替换旧Session的物理连接
            currentSession.attachTo(revivedSession);
            revivedSession.reopen();
            // 重新维护连接集合
            SessionManager.getConnections().remove(session);
            SessionManager.getConnections().add(revivedSession);
            // 下发重连成功响应
            session.write(JsonPacket.of(MsgTypeEnum.RECONNECT_RESPONSE.getText(), Payload.of("toke", System.currentTimeMillis())));
            // 下发短线后客户端未接收成功的缓存消息
            List<Object> messages = revivedSession.getStore().retrieveResponsesSince(request.getAck());
            messages.forEach(session::write);
        }
        return null;
    }
}
