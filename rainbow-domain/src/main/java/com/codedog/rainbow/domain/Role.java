/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@Getter
@Setter
@ToString
public class Role {

    /**
     * 主键，由程序提供，需保证全局唯一
     */
    @Id
    private Long id;
    /** User Id */
    private Long uid;
    /** Server Id */
    private Long sid;
    private String nick;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 子渠道
     */
    private String subChannel;
    /**
     * 平台：ios/android
     */
    private String platform;
    /** Blocked Until */
    private Date blockedUntil;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;
}
