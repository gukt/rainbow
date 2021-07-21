/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

/**
 * FieldInactiveAwareQueryCriteria class
 *
 * @author https://github.com/gukt
 */
public interface FieldInactiveAwareQueryCriteria {

    Boolean isInactive();

    void setInactive(Boolean inactive);
}
