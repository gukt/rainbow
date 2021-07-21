/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.codedog.rainbow.JsonViews.UserLoginView;
import com.codedog.rainbow.core.rest.ApiResultView.IdOnly;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_users")
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    /** 主键，由程序提供，需保证全局唯一 */
    @Id
    @JsonView(IdOnly.class)
    private Long id;

    /** 用户名，仅用于内部账号登陆 */
    @Column(nullable = false, length = 50)
    @JsonView(UserLoginView.class)
    private String name;

    /** 密码，仅用于内部账号系统 */
    @Column(nullable = false, length = 100)
    private String password;

    /** 用户类型 */
    @Column(nullable = false, columnDefinition = "tinyint default 0")
    @JsonView(UserLoginView.class)
    private Integer type;

    /** 1表示删除，0 表示正常, NOTE: active 是 mysql 保留关键字 */
    @Column(nullable = false, columnDefinition = "bit default 0") private
    Boolean inactive = false;

    @Column(nullable = false, columnDefinition = "tinyint default 1")
    private Integer state;

    @Temporal(TemporalType.TIMESTAMP)
    private Date blockedUntil;

    @Column(nullable = false)
    private Date loginTime;

    @Column(nullable = false, columnDefinition = "varchar(50) default ''")
    private String loginIp;

    /**
     * 创建时间, NOTE: MYSQL 的日期类型中，只有 timestamp 支持设置默认值，其他如: datetime, date, time, year 都不支持。
     * <pre>
     * 1. ts TIMESTAMP NOT NULL DEFAULT NOW()
     * 2. ts TIMESTAMP NOT NULL default CURRENT_TIMESTAMP()
     * </pre>
     */
    @Column(nullable = false)
    @JsonView(UserLoginView.class)
    private Date createdAt;

    @Column(nullable = false)
    @JsonView(UserLoginView.class)
    private Date updatedAt;
}
