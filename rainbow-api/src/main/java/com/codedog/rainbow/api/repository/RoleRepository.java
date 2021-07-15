/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.repository;

import com.codedog.rainbow.api.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author https://github.com/gukt
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,
        JpaSpecificationExecutor<Role> {
}
