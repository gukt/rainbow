/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.tcp.HandlerMapping;
import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.MessageHandler.Error;
import com.codedog.rainbow.tcp.MessageHandlerException;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.world.generated.GameEnterRequest;
import com.codedog.rainbow.tcp.util.Payload;
import com.codedog.rainbow.world.service.LoggingService;
import com.codedog.rainbow.world.service.RedisService;
import com.codedog.rainbow.world.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

/**
 * RoleController
 *
 * @author https://github.com/gukt
 */
@Controller
@Slf4j
public class TestController {

    private final RoleService roleService;
    private final LoggingService loggingService;

    public TestController(RoleService roleService, RedisService redisService, LoggingService loggingService) {
        this.roleService = roleService;
        this.loggingService = loggingService;
    }

    @HandlerMapping(value = "A")
    public Object A(Session session) {
        // MessageLite
        // MessageLite.Builder
        // MessageLiteOrBuilder
        // ProtoPacket -> ProtoPacketOrBuilder -> Message, Message.Builder - MessageLite, MessageLite.Builder -> MessageLiteOrBuilder
        //
        GameEnterRequest.getDefaultInstance();
        Payload payload = Payload.of("k1", "v1");
        session.write(payload);
        session.write(JsonPacket.of("A1", payload));
        return null;
    }

    @HandlerMapping(value = "B")
    public Object B(Session session) {
        return new MessageHandlerException(1, "aaa");
    }

    @HandlerMapping(value = "C")
    public Object C(Session session, Error error) {
        List<String> list = Arrays.asList("a", "B");
        List<String> list2 = Arrays.asList("c", "d");
        error.add(list);
        error.add(list2);
        // throw new RuntimeException("aaa");
        return null;
    }
}
