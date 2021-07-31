/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.codedog.rainbow.world.excel.ExcelMapping;
import com.codedog.rainbow.world.excel.ExcelParser;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author https://github.com/gukt
 */
@Component
@SuppressWarnings("unused")
@Slf4j
public class TemplateManager {

    private final LoadingCache<Class<?>, List<?>> cache;

    public TemplateManager(ExcelParser parser) {
        this.cache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .build(new CacheLoader<Class<?>, List<?>>() {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public List<?> load(Class<?> key) {
                        return parser.parse(key);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <V> List<V> find(Class<V> clazz) {
        return (List<V>) cache.getUnchecked(clazz);
    }

    public <V> List<V> find(Class<V> clazz, Predicate<V> predicate) {
        return find(clazz).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public <V> Optional<V> findOne(Class<V> clazz, Predicate<V> predicate) {
        return find(clazz).stream().filter(predicate).findFirst();
    }

    private void invalidate(Class<?> clazz) {
        cache.invalidate(clazz);
    }

    private void touch(Class<?> clazz) {
        cache.getUnchecked(clazz);
    }

    public void reload(Class<?> targetType) {
        String targetTypeName = targetType.getSimpleName();
        try {
            log.info("Template - Reloading {}", targetTypeName);
            invalidate(targetType);
            touch(targetType);
            log.info("Template - Reloaded {}", targetTypeName);
        } catch (Exception e) {
            log.error("Template - Failed to reload {}", targetTypeName, e);
        }
    }

    public void reload(Iterable<Class<?>> classes) {
        classes.forEach(this::reload);
    }

    public Optional<Class<?>> getTypeByFilename(String filename) {
        return allTypes().stream()
                .filter(t -> Objects.equals(filename, t.getAnnotation(ExcelMapping.class).file()))
                .findFirst();
    }

    public Set<Class<?>> allTypes() {
        return new HashSet<>();
    }
}
