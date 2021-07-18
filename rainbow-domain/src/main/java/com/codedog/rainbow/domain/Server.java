/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

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
@Table(name = "t_servers")
@RequiredArgsConstructor
@Data
public class Server {

    /**
     * ID，主键，由程序提供，需保证是全局唯一 ID
     */
    @Id
    private Integer id;
    /**
     * 类型，0: pre-publish, 1: backup, 2: published
     */
    private Integer type;
    /**
     * 名称
     */
    private String name;
    /**
     * 状态，-1: deleted, 0: offline, 1: online, 2: trouble
     */
    private Integer state;
    /**
     * 上线服务时间
     */
    private Date servedAt;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;
}
