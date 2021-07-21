/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

import java.util.Date;

/**
 * StateAwareQueryCriteria class
 * TODO 移为公共
 *
 * @author https://github.com/gukt
 */
public interface FieldCreatedAtAwareQueryCriteria {

    Date getCreatedStart();

    void setCreatedStart(Date createdStart);

    Date getCreatedEnd();

    void setCreatedEnd(Date createdEnd);
}
