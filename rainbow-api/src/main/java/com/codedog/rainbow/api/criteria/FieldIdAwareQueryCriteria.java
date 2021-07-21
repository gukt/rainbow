/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * IdAwareQueryCriteria class
 * TODO 移为公共
 *
 * @author https://github.com/gukt
 */
public interface FieldIdAwareQueryCriteria<T extends Serializable> {

    Set<T> getIds();

    void setIds(Set<T> ids);
}
