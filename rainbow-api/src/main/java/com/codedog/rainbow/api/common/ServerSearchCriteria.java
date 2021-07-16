/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.common;

import lombok.Data;

import java.util.Set;

/**
 * ServerSearchCriterial class
 *
 * @author https://github.com/gukt
 */
@Data
public class ServerSearchCriteria {

    private Long uid;
    private String q;
    private Set<Integer> states;
}
