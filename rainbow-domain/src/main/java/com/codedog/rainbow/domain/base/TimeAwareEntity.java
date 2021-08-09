/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * BaseEntity class
 *
 * @author https://github.com/gukt
 */
@Getter
@Setter
@MappedSuperclass
public class TimeAwareEntity extends IdAwareEntity {

    @Column(nullable = false)
    protected Date createdAt;

    @Column(nullable = false)
    protected Date updatedAt;
}
