/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.controller;

import com.codedog.rainbow.util.CommonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codedog.rainbow.core.rest.ApiResult.*;

/**
 * 支付相关 API
 *
 * @author https://github.com/gukt
 */
@RestController
@RequestMapping("api")
@Slf4j
public class AdminController {

    /**
     *
     */
    @Value("classpath:/index.html")
    private Resource indexHtml;

    @RequestMapping("/")
    public Object index() {
        return ResponseEntity.ok().body(indexHtml);
    }

    // TODO 看看去掉 produces 是不是返回统一的 JSON 格式?
    // 如果是，说明上面的也可以被整合到这里
    @GetMapping(value = "", produces = "text/plain;charset=utf-8")
    public Object apiHome() {
        return "Hey man, what's up?";
    }

    // For GM System
    @GetMapping("gm/login")
    public Object login(String username, String password) {
        log.debug("login...");
        boolean match = "admin".equalsIgnoreCase(username)
                && "1234+aaaa".equalsIgnoreCase(password);
        if (match) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", CommonUtils.sha256(password));
            return success(resultMap);
        } else {
            return failed(1, "用户名或密码不正确");
        }
    }

    @GetMapping("gm/info")
    public Object info(String token) {
        List<String> roles = Lists.newArrayList("admin");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("roles", roles);
        resultMap.put("name", "admin");
        resultMap.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return success(resultMap);
    }

    @PostMapping("gm/logout")
    public Object logout(HttpSession session) {
        log.debug("GM: logout...");
        session.removeAttribute("user");
        return SUCCESS;
    }


}
