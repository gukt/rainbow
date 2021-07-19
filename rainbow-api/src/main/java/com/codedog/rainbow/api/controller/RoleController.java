/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.controller;

import com.codedog.rainbow.domain.Role;
import com.codedog.rainbow.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.codedog.rainbow.core.rest.ApiResult.OK;

/**
 * 用户角色相关 API
 *
 * @author https://github.com/gukt
 */
@RestController
@Slf4j
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping("roles")
    public Object search(@PageableDefault Pageable page) {
        return roleRepository.findAll(page);
    }

    @GetMapping("roles/{id}")
    public Object getById(@PathVariable long id) {
        return roleRepository.getById(id);
    }

    @DeleteMapping("roles/{id}")
    public Object removeById(@PathVariable long id) {
        roleRepository.deleteById(id);
        return OK;
    }

    @PatchMapping("roles/{id}")
    public Object update(@PathVariable long id, @RequestBody Role entity) {
        Role updating = roleRepository.getById(id);
        // TODO 实现一个不依赖 Spring 的 copyProperties 方法
//        Beans.copyProperties(entity, updating, true);

        return roleRepository.save(updating);
    }

}
