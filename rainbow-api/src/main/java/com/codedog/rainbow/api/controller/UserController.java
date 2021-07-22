/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.controller;

import com.codedog.rainbow.JsonViews.UserLoginView;
import com.codedog.rainbow.api.common.Errors;
import com.codedog.rainbow.api.criteria.UserQueryCriteria;
import com.codedog.rainbow.api.service.ServerService;
import com.codedog.rainbow.api.service.UserService;
import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.repository.RoleRepository;
import com.codedog.rainbow.repository.UserRepository;
import com.codedog.rainbow.util.JsonUtils;
import com.codedog.rainbow.util.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

import static com.codedog.rainbow.api.common.Errors.ERR_BAD_PARAMETER;
import static com.codedog.rainbow.core.rest.ApiResult.SUCCESS;
import static com.codedog.rainbow.core.rest.ApiResult.success;

/**
 * 用户相关 API
 *
 * @author https://github.com/gukt
 */
@RestController
@RequestMapping("api")
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(RoleRepository roleRepository, ServerService serverService, UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * 用户注册，仅限内部测试使用
     *
     * @param user
     * @return
     */
    @PostMapping("user/register")
    @JsonView(UserLoginView.class)
    public Object register(@RequestBody User user) {
        if (user == null) {
            return ERR_BAD_PARAMETER;
        }
        return userService.save(user, true);
    }

    /**
     * 用户登陆
     * NOTE: 偶尔使用一些快捷方式命名比完全按 Restful 规范命名更简单直观
     *
     * @param name     用户名，可以是用户名、手机号或邮箱地址
     * @param password 密码，必须是加密过的
     */
    @PostMapping("user/login")
    @JsonView(UserLoginView.class)
    public Object login(String name, String password) {
        User user = userService.getByName(name);
        if (user == null) {
            return Errors.ERR_ENTITY_NOT_FOUND;
        }
        if (!Objects.equal(user.getPassword(), password)) {
            return Errors.ERR_INVALID_NAME_OR_PASSWORD;
        }
        return user;
    }

    /**
     * 用户登出
     *
     * @param session
     * @return
     */
    @PostMapping("user/logout")
    public Object logout(HttpSession session) {
        return SUCCESS;
    }

    // 根据条件查询
    @GetMapping("users")
    public Object search(UserQueryCriteria criteria, @PageableDefault Pageable page) {
        return userService.search(criteria, page);
    }

    @GetMapping("users/{id}")
    public Object findById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PatchMapping("users/{id}")
    public Object update(@PathVariable long id, @RequestBody User entity) {
        User updating = userRepository.getById(id);
        // TODO 拷贝属性
        return userService.save(updating, false);
    }

    @DeleteMapping("users/{id}")
    public Object removeById(@PathVariable long id) {
        int rows = userService.removeByIds(Sets.newHashSet(id), false);
        return success(rows);
    }

    @GetMapping("users/exists")
    public Object exists(String name) {
        return false; // TODO
    }

    // 获得一个随机的名称，现代游戏中大多有这种功能需求
    @GetMapping("users/random-name")
    public Object randomName(String name) {
        // TODO 判断用户名是否存在
        return SUCCESS;
    }

    // 修改密码
    // 后台和玩家都需要使用该功能
//    @PostMapping("users/{id}")
//    public Object changePassword(@PathVariable long id, String old, @RequestParam(name = "new") String newPwd) {
//        return SUCCESS;
//    }

    @Data
    static class BatchRequestBody<T, ID> {

        @JsonProperty("delete")
        private Set<ID> deleteIds;
        @JsonProperty("update")
        private List<T> updatingEntities;
        @JsonProperty("add")
        private List<T> addingEntities;
    }

    // 批量更新或删除
    @PostMapping("users/batch")
    public Object batch(@RequestBody BatchRequestBody<User, Long> body) {
        log.info("BatchRequestBody: {}", body);
        // Batch deleting
        if (!ObjectUtils.isNullOrEmpty(body.getDeleteIds())) {
            userService.removeByIds(body.getDeleteIds(), false);
        }
        // Batch updating
        if (!ObjectUtils.isNullOrEmpty(body.getUpdatingEntities())) {
            body.getUpdatingEntities().forEach(entity -> {
                try {
                    userService.save(entity, false);
                } catch (EntityNotFoundException e) {
                    log.warn("Batch updating: Unable to find an user with id {}, body={}", entity.getId(), JsonUtils.toJson(body));
                }
            });
        }
        // Batch adding
        if (!ObjectUtils.isNullOrEmpty(body.getAddingEntities())) {
            body.getAddingEntities().forEach(entity -> {
                userService.save(entity, true);
            });
        }
        return SUCCESS;
    }
}
