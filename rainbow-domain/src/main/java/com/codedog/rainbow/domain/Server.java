/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.codedog.rainbow.domain.base.TimeAwareEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_servers")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Server extends TimeAwareEntity {

    /**
     * 类型，0: pre-publish, 1: backup, 2: published
     */
    private Integer type;
    /**
     * 名称
     */
    private String name;
    /**
     * 上线服务时间
     */
    private Date servedAt;
    /**
     * 状态，-1: deleted, 0: offline, 1: online, 2: trouble
     */
    private Integer state;
}
