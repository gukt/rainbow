/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.domain;

import com.codedog.rainbow.support.AbstractAttributeSupport;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@EqualsAndHashCode(callSuper = true)
public class Role extends AbstractAttributeSupport {

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
