/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.support;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author https://github.com/gukt
 */
public abstract class AbstractAttributeSupport implements AttributeSupport {

    @Getter
    private final Map<String, Object> attrsMap = new HashMap<>();
}

