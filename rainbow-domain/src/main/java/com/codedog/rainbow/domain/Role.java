/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_roles")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role {

    /**
     * 主键，由程序提供，需保证全局唯一
     */
    @Id
    private Long id;

    /** User Id */
    @Column(nullable = false)
    private Long uid;

    /** Server Id */
    @Column(nullable = false)
    private Long sid;

    @Column(nullable = false, length = 50, columnDefinition = "default ''")
    private String nick;

    /** Channel */
    @Column(length = 25)
    private String channel;

    /** Sub channel */
    @Column(length = 25)
    private String subChannel;

    /** Platform: ios/android */
    @Column(length = 25)
    private String platform;

    private Date blockedUntil;

    @Column(nullable = false, columnDefinition = "default 1")
    private Boolean active = false;

    @Column(nullable = false, columnDefinition = "default 0")
    private Integer state;

    @Column(nullable = false)
    private Date createdAt;

    private Date updatedAt;
}
