/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.cache;

import org.springframework.cache.annotation.Cacheable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * EntityCache class
 *
 * @author https://github.com/gukt
 */
public class CacheStore {

    Map<String, Object> cachesByName = new HashMap<>();

    static class Cache {
        Map<Serializable, Object> objectsById = new HashMap<>();
    }

    @Cacheable(key = "#id", value = "Person")
    static class Person {
        private Long id;
    }

    @Cacheable(key = "#roleId", value = "Person")
    static class Hero {
        private Long id;
        private Long roleId;
    }

    // @Cacheable(value="", key="")
    // Page<Hero> heroRepository.findAllByRoleId(100001);
}
