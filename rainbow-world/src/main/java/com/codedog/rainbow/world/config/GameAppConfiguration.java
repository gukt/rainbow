/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.config;

import com.codedog.rainbow.world.EventPublisher;
import com.codedog.rainbow.world.excel.ExcelParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author https://github.com/gukt
 */
@Configuration
@Slf4j
public class GameAppConfiguration {

    private final ApplicationContext context;
    private final AppProperties options;

    public GameAppConfiguration(ApplicationContext context, AppProperties options) {
        this.context = context;
        this.options = options;
    }

    @Bean
    public EventPublisher eventPublisher() {
        EventPublisher publisher = new EventPublisher();
        scanEventSubscribers().forEach(publisher::registerSubscribe);
        return publisher;
    }

    private Collection<Object> scanEventSubscribers() {
        Map<String, Object> beansMap = context.getBeansWithAnnotation(Service.class);
        beansMap.putAll(context.getBeansWithAnnotation(Controller.class));
        return new ArrayList<>(beansMap.values());
    }

    @Bean
    public ExcelParser excelParser(AppProperties opts) {
        return ExcelParser.builder().baseDir(opts.getExcelPath())
                .mutableCheck(true)
                .nullable(true)
                .persistEnabled(true)
                .persistSuffix(".dat")
                .namingRowIndex(1)
                .build();
    }
}
