/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core;

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

