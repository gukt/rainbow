/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role {

    /**
     * 主键，由程序提供，需保证全局唯一
     */
    @Id
    private Long id;

    /** User id */
    @Column(nullable = false)
    private Long uid;

    /** Server id, max value: 3 bytes */
    @Column(nullable = false, columnDefinition = "mediumint default 0")
    private Integer sid;

    @Column(nullable = false, columnDefinition = "varchar(50) default ''")
    private String nick;

    /** Channel */
    @Column(nullable = false, columnDefinition = "varchar(25) default ''")
    private String channel;

    /** Sub channel */
    @Column(nullable = false, columnDefinition = "varchar(25) default ''")
    private String subChannel;

    /** Platform: ios/android */
    @Column(nullable = false, columnDefinition = "varchar(25) default ''")
    private String platform;

    @Temporal(TemporalType.TIMESTAMP)
    private Date blockedUntil;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private Boolean inactive = false;

    @Column(nullable = false, columnDefinition = "tinyint default 0")
    private Integer state;

    @Column(nullable = false)
    private Date createdAt;

    private Date updatedAt;
}
