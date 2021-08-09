/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.repository;

import com.codedog.rainbow.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * ServerRepository Class
 *
 * @author https://github.com/gukt
 */
@Repository
public interface ServerRepository extends    JpaRepository<Server, Integer>,
        JpaSpecificationExecutor<Server> {

    @Query("DELETE FROM Server WHERE id = ?1")
    void deleteByIds(Set<Integer> ids);
}
