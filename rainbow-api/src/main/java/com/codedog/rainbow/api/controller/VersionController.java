/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.controller;

import com.codedog.rainbow.api.common.ServerSearchCriteria;
import com.codedog.rainbow.domain.Role;
import com.codedog.rainbow.domain.Server;
import com.codedog.rainbow.repository.RoleRepository;
import com.codedog.rainbow.api.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.codedog.rainbow.api.common.ApiResult.OK;

/**
 * 版本相关
 *
 * @author https://github.com/gukt
 */
@RestController
public class VersionController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ServerService serverService;

    /**
     * 获取或查询满足条件的服务器，当指定 uid 时，会携带该用户所有创建过的角色（在分区分服服务器上，角色意味着和某个服务器是绑定的）
     *
     * @param criteria 搜索条件，可以为 null。
     * @param page     分页条件，不可以为 null。
     * @return 满足条件的服务器列表，如果搜索条件中给定了 uid，则同时返回玩家创建的所有角色所在的服务器
     */
    @GetMapping("servers")
    public Object search(ServerSearchCriteria criteria, @PageableDefault Pageable page) {
        Map<String, Object> retMap = new HashMap<>();
        Page<Server> servers = serverService.search(criteria, page);
        retMap.put("servers", servers);
        if (criteria.getUid() != null) {
            // TODO 获取用户在哪些服务器上创建过角色并按 updatedAt 排序（表示最后登陆时间）
            List<Object> roleServers = new ArrayList<>();
            retMap.put("roleServers", roleServers);
        }
        return retMap;
    }

    @GetMapping("servers/{id}")
    public Object getById(@PathVariable long id) {
        return roleRepository.getById(id);
    }

    @DeleteMapping("servers/{id}")
    public Object removeById(@PathVariable long id) {
        roleRepository.deleteById(id);
        return OK;
    }

    // 获得所有的服务器信息，包括统计信息，状态信息等。
    @GetMapping("servers/info")
    public Object multiServerInfo(ServerSearchCriteria criteria, @PageableDefault Pageable page) {
        Page<Server> servers = serverService.search(criteria, page);
        // TODO 获取每个服务器的信息
        servers.forEach(server -> {
            // TODO 获取每个服务器的信息
        });
        return OK;
    }

    @GetMapping("servers/{id}/info")
    public Object singleServerInfo(@PathVariable int id) {
        Server server = serverService.getById(id);
        // TODO 获取单个服务器信息
        return OK;
    }

    @GetMapping("servers/{id}/health")
    public Object serverHealth(@PathVariable int id) {
        Server server = serverService.getById(id);
        // TODO 获取单个服务器信息
        return OK;
    }

    /**
     * 批量操作，比如：删除、更新、新增
     * 删除: { delete: [1, 2, 3] }
     * 更新：{ update: { 1: { state = 1}, 2: { state = 1} }}
     * 新增：{ add: [ {id: 1, name: 'xxx' }, {id: 2, name: 'xxx' } ]
     *
     * @param ids
     * @param body
     * @return
     */
    @PostMapping("servers/batch")
    public Object batch(Set<Long> ids, @RequestBody Map<String, Object> body) {
        // TODO servers/batch
        return OK;
    }

    @PatchMapping("servers/{id}")
    public Object update(@PathVariable long id, @RequestBody Role entity) {
        Role updating = roleRepository.getById(id);
        // TODO 实现一个不依赖 Spring 的 copyProperties 方法
//        Beans.copyProperties(entity, updating, true);

        return roleRepository.save(updating);
    }

}
