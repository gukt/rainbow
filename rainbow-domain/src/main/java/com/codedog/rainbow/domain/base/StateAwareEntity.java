/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * StateAwareEntity class
 *
 * @author https://github.com/gukt
 */
@Getter
@Setter
@MappedSuperclass
public abstract class StateAwareEntity extends TimeAwareEntity {

    @Column(nullable = false, columnDefinition = "tinyint default 0")
    protected Integer state;
}
