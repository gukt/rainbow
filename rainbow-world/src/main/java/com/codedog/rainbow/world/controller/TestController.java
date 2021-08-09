/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.controller;

import com.codedog.rainbow.tcp.message.HandlerMapping;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.world.service.LoggingService;
import com.codedog.rainbow.world.service.RedisService;
import com.codedog.rainbow.world.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/first")
    public Object first() {
        return "first controller";
    }

    @GetMapping("/doError")
    public Object error() {
        return 1 / 0;
    }

    @HandlerMapping(value = "GameEnterRequest")
    public Object gameEnterRequest(Session session) throws Exception {
        if (true) {
            // return BaseError.BAD_REQUEST;
            // return BaseError.BAD_REQUEST.text("xxx");
            // return Payload.EMPTY;
            // return JsonPacket.of("Foo");
            // return new Object();
            // return new MessageHandlerException(1,"error1");
            // throw new MessageHandlerException(2, "error2");
            return new RuntimeException("This is a runtime exception (return directly)");
            // throw new RuntimeException("This is a runtime exception");
            // throw new Exception("This is a checked exception");
        }
        return null;
    }
}
