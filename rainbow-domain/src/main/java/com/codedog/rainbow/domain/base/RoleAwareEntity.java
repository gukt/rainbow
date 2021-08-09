/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain.base;

import com.codedog.rainbow.domain.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * RoleRelated class
 *
 * @author https://github.com/gukt
 */
@Getter
@Setter
@MappedSuperclass
public abstract class RoleAwareEntity extends TimeAwareEntity {

    /**
     * 角色 ID，关联 {@link Role#getId()}
     */
    @Column(nullable = false)
    protected Long roleId;

}
