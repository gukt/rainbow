/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_roles")
@RequiredArgsConstructor
@Data
public class Role {

    /**
     * ID，主键，由程序提供，需保证是全局唯一 ID
     */
    @Id
    private Long id;
    private Long uid;
    private Date blockedUntil;
    private Date createdAt;
    private Date updatedAt;
}
