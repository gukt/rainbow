/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
@Data
public class User {

    /**
     * ID，主键，由程序提供，需保证是全局唯一 ID
     */
    @Id
    @NonNull
    private Long id;
    private String nick;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
}
