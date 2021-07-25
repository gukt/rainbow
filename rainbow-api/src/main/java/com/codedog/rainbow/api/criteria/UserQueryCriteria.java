/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

import com.codedog.rainbow.util.ObjectUtils;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * ServerSearchCriterial class
 *
 * @author https://github.com/gukt
 */
@Data
public class UserQueryCriteria implements
        EntityQueryCriteria<Long>, FieldIdAwareQueryCriteria<Long>,
        FieldStateAwareQueryCriteria, FieldCreatedAtAwareQueryCriteria {

    private Set<Long> ids;
    private String q;
    private Integer inactive;
    private Set<Integer> states;
    private Integer type;
    private Date createdStart;
    private Date createdEnd;
    private Date loginStart;
    private Date loginEnd;
    private Date blockStart;
    private Date blockEnd;

    public static UserQueryCriteria of(Set<Long> ids) {
        ObjectUtils.requireNonEmpty(ids, "ids");
        UserQueryCriteria instance = new UserQueryCriteria();
        instance.ids = ids;
        return instance;
    }
}
