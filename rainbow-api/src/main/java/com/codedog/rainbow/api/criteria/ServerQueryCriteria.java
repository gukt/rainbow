/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

import lombok.Data;

import java.util.Set;

/**
 * ServerQueryCriteria class
 *
 * @author https://github.com/gukt
 */
@Data
public class ServerQueryCriteria {

    private Long uid;
    private String q;
    private Set<Integer> states;
}
