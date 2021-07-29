/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.temp;

import java.util.ArrayList;
import java.util.List;

/**
 * NestedAnimal class
 *
 * @author https://github.com/gukt
 */
public class WildAnimal<ID, T extends WildAnimal<ID, T>> {

    List<T> children() {
        return new ArrayList<T>();
    }
}
