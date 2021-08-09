/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain;

import com.codedog.rainbow.domain.base.RoleAwareEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Map;

/**
 * 邮件
 * @author https://github.com/gukt
 */

@Entity
@Table(name = "t_mails")
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
})
public class Mail extends RoleAwareEntity {

    /**
     * 标题，不多于 25 个字符。
     */
    @Column(nullable = false, columnDefinition = "varchar(25) default ''")
    private String title;

    /**
     * 正文，不多于 1000 个字符。
     */
    @Column(nullable = false, columnDefinition = "varchar(1000) default ''")
    private String body;

    /**
     * 发件人 ID
     */
    @Column(nullable = false, columnDefinition = "int(11) default 0")
    private Long fromId = 0L;

    /**
     * 收件人 ID
     */
    @Column(nullable = false, columnDefinition = "int(11) default 0")
    private Long toId = 0L;

    @Column(nullable = false, columnDefinition = "int(11) default 0")
    private Integer type;

    /**
     * 附件
     */
    @Type(type = "json")
    @Column(nullable = false, columnDefinition = "json default '{}'")
    private Map<String,Object> attachment;
}
