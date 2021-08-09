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
 * 道具
 *
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_items")
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item extends RoleAwareEntity {

    /**
     * 名称，不多于 15 个字符。
     */
    @Column(nullable = false, columnDefinition = "varchar(15) default ''")
    private String name;

    /**
     * 数量
     */
    @Column(nullable = false, columnDefinition = "mediumint default 0")
    private Integer num;
}
