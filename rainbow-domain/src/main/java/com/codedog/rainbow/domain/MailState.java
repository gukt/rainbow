/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.codedog.rainbow.domain.base.RoleAwareEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_mail_state")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MailState extends RoleAwareEntity {

    /**
     * 邮件 ID
     */
    @Column(nullable = false)
    private Long mailId;

    /**
     * 附件状态
     */
    @Column(nullable = false, columnDefinition = "tinyint default 0")
    private Integer attachmentState;
}