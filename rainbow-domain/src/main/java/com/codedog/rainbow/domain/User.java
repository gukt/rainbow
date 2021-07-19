/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

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

    /**
     * 主键，由程序提供，需保证全局唯一
     */
    @Id
    private Long id;
    /**
     * 用户名，仅用于内部账号登陆
     */
    private String name;
    /**
     * 密码，仅用于内部账号登陆
     */
    private String password;
    private Integer state;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;

}
