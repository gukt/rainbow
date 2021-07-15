/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.support;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
public abstract class AbstractAttributeSupport implements AttributeSupport {

    @Getter
    private Map<String, Object> attrsMap = new HashMap<>();
}

