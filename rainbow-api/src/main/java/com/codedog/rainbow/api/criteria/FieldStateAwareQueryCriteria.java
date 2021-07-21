/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

import java.util.Set;

/**
 * StateAwareQueryCriteria class
 * TODO 移为公共
 *
 * @author https://github.com/gukt
 */
public interface FieldStateAwareQueryCriteria {

    Set<Integer> getStates();

    void setStates(Set<Integer> states);
}
