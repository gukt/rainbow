/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.codedog.rainbow.JsonViews.UserBasicView;
import com.codedog.rainbow.core.rest.ApiResultView.IdOnly;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @JsonView(UserBasicView.class)
    private String name;
    /** 密码，仅用于内部账号系统 */
    private String password;
    /** 用户类型 */
    @JsonView(UserBasicView.class)
    private Integer type;
    private Boolean active;
    private Integer state;
    @JsonView(UserBasicView.class)
    private Date createdAt;
    private Date updatedAt;

}
