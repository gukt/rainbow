/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.controller;

import com.codedog.rainbow.api.domain.Role;
import com.codedog.rainbow.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.codedog.rainbow.api.common.ApiResult.OK;

/**
 * RoleController class
 *
 * @author https://github.com/gukt
 */
@Controller
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

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
