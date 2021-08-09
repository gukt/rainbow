/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.codedog.rainbow.domain.base.RoleAwareEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_tasks")
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task extends RoleAwareEntity {

    /**
     * 名称，不能超过 25 个字符。
     */
    @Column(nullable = false, columnDefinition = "varchar(25) default ''")
    private String name;

}
