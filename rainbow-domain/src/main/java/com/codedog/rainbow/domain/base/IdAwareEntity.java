/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.domain.base;

import com.codedog.rainbow.core.CacheableObject;
import com.codedog.rainbow.core.rest.ApiResultView.IdOnly;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * BaseEntity class
 *
 * @author https://github.com/gukt
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdAwareEntity extends CacheableObject {

    /**
     * 主键，由程序提供，需保证全局唯一
     */
    @Id
    @JsonView(IdOnly.class)
    @JsonSerialize(using= ToStringSerializer.class)
    protected Long id;
}
