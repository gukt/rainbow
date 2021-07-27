/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.service;

import com.codedog.rainbow.lang.NotImplementedException;
import com.codedog.rainbow.api.criteria.ServerQueryCriteria;
import com.codedog.rainbow.domain.Server;
import com.codedog.rainbow.repository.ServerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

/**
 * ServerService class
 *
 * @author https://github.com/gukt
 */
@Service
@Slf4j
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    /**
     * 搜索满足条件的 Server(s), 分页参数必须指定。
     *
     * @param criteria 搜索条件，可以为 null
     * @param page     分页条件，不可以为 null
     * @return 分页的 Server(s)，结果不会为 null。
     */
    public Page<Server> search(ServerQueryCriteria criteria, Pageable page) {
        throw new NotImplementedException();
    }

    /**
     * 根据 ID 查找 Server 对象，由于服务器基本信息也随时可能发生变化，很多场景需要查看无延时的信息，所以该方法不能启用缓存。
     *
     * @param id ID, 不能为 null
     * @return 指定 ID 对应的 Server 对象
     */
    public Server getById(Integer id) {
        return serverRepository.getById(id);
    }

    /**
     * 根据 ID 删除指定的 Server 记录，force 为 false 的情况下表示逻辑删除，如果要物理删除，请指定 force 为 true。
     *
     * @param ids   server id(s)
     * @param force false: 逻辑删除；true: 物理删除。
     */
    public void removeByIds(Set<Integer> ids, boolean force) {
        if (force) {
            // 物理删除
            serverRepository.deleteByIds(ids);
            return;
        }
        // 逻辑删除
        ids.forEach(id -> {
            Server server = getById(id);
            server.setState(-1);
            server.setUpdatedAt(new Date());
            serverRepository.save(server);
        });
    }
}
