/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.controller;

import com.codedog.rainbow.JsonViews.UserLoginView;
import com.codedog.rainbow.api.common.BatchRequestBody;
import com.codedog.rainbow.api.common.BodyData;
import com.codedog.rainbow.api.criteria.UserQueryCriteria;
import com.codedog.rainbow.api.service.ServerService;
import com.codedog.rainbow.api.service.UserService;
import com.codedog.rainbow.core.rest.ApiResult;
import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.repository.RoleRepository;
import com.codedog.rainbow.repository.UserRepository;
import com.codedog.rainbow.util.IdGenerator;
import com.codedog.rainbow.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static com.codedog.rainbow.api.common.Errors.ERR_BAD_PARAMETER;
import static com.codedog.rainbow.api.common.Errors.ERR_INVALID_NAME_OR_PASSWORD;
import static com.codedog.rainbow.core.rest.ApiResult.OK;
import static com.codedog.rainbow.core.rest.ApiResult.success;
import static com.codedog.rainbow.util.ObjectUtils.isNotEmpty;

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

//    static class RequestBodyValueResolver {
//
//        @SuppressWarnings("unchecked")
//        public static <K, V> V resolve(Map<K, ?> map, K key) {
//            V value = (V) map.get(key);
//            if (value == null) {
//                System.out.println("Key not found in given map. key: " + key);
//            }
//            return value;
//        }
//    }

    /**
     * 用户注册，仅限内部测试使用
     *
     * @return
     */
    @PostMapping("user/register")
    @JsonView(UserLoginView.class)
    public Object register(@RequestBody BodyData body, HttpServletRequest request) {
        Optional<String> name = body.find("name");
        if (!name.isPresent()) {
            return ERR_BAD_PARAMETER;
        }
        Optional<String> password = body.find("password");
        if (!password.isPresent()) {
            return ERR_BAD_PARAMETER;
        }

//        String name1 = body.get("name");
//        String password1 = body.getOrThrow("", ERR_BAD_PARAMETER);

//        String name = body.get("name");
//        String password = body.get("password");
//        if (isNullOrEmpty(name) || isNullOrEmpty(password)) {
//            return ERR_BAD_PARAMETER;
//        }
        User user = new User();
        user.setId(IdGenerator.nextId());
        user.setName(name.get());
        user.setPassword(password.get());
        // 注册成功即表示登陆，所以登陆时间和 IP 要记录一下
        user.setLoginIp(request.getRemoteAddr());
        return userService.save(user, true);
    }

    /**
     * 用户登陆
     *
     * <p>NOTE: 偶尔使用一些快捷方式命名比完全按 Restful 规范命名更简单直观。
     */
    @PostMapping("user/login")
    @JsonView(UserLoginView.class)
    public Object login(@RequestBody BodyData body) {
        Optional<String> name = body.find("name");
        Optional<String> password = body.find("password");
        // 用户名和密码不能为空
        if (!name.isPresent() || !password.isPresent()) {
            return ERR_BAD_PARAMETER;
        }
        Optional<User> user = userService.findByName(name.get());
        // 用户名没找到或密码不匹配
        if (!user.isPresent() || !Objects.equal(user.get().getPassword(), password.get())) {
            return ERR_INVALID_NAME_OR_PASSWORD;
        }
        return user;
    }

    /**
     * 用户登出
     *
     * @param session represents the current user
     * @return {@link ApiResult#OK} if success.
     */
    @PostMapping("user/logout")
    public Object logout(HttpSession session) {
        return OK;
    }

    /**
     * 多条件查询
     *
     * @param criteria 查询条件
     * @param page     分页参数
     * @return list of user with pagination info.
     */
    @GetMapping("users")
    public Page<User> search(UserQueryCriteria criteria, @PageableDefault Pageable page) {
        return userService.search(criteria, page);
    }

    @GetMapping("users/{id}")
    public Object getById(@PathVariable long id) {
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

//    /**
//     * 重置密码
//     */
//    @PostMapping("user/change-password")
//    @JsonView(UserLoginView.class)
//    public Object changePassword(@RequestBody BodyData body) {
//        Long uid = body.get0("id");
//        String old = body.get0("old");
//        String newPwd = body.get0("new");
//        User user = userService.getById(uid);
//        if(!user.getPassword().equals(old)) {
//            return ERR_BAD_PARAMETER;
//        }
//        user.setPassword(newPwd);
//        userService.save(user, false);
//        return SUCCESS;
//    }

    // 批量更新或删除
    @PostMapping("users/batch")
    public Object batch(@RequestBody BatchRequestBody<User, Long> body) {
        log.info("BatchRequestBody: {}", body);
        // Batch deleting
        if (isNotEmpty(body.getDeleteIds())) {
            userService.removeByIds(body.getDeleteIds(), false);
        }
        // Batch updating
        if (isNotEmpty(body.getUpdatingEntities())) {
            body.getUpdatingEntities().forEach(entity -> {
                try {
                    userService.save(entity, false);
                } catch (EntityNotFoundException e) {
                    log.warn("Batch updating: Unable to find an user with id {}, body={}", entity.getId(), JsonUtils.toJson(body));
                }
            });
        }
        // Batch adding
        if (isNotEmpty(body.getAddingEntities())) {
            body.getAddingEntities().forEach(entity -> {
                userService.save(entity, true);
            });
        }
        return OK;
    }
}
