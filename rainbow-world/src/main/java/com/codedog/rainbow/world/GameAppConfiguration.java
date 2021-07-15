/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world;

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
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Slf4j
@Configuration
public class GameAppConfiguration {

    private final ApplicationContext context;
    private final GameOptions options;

    public GameAppConfiguration(ApplicationContext context, GameOptions options) {
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
    public ExcelParser excelParser(GameOptions opts) {
        return ExcelParser.builder().baseDir(opts.getExcelPath())
                .mutableCheck(true)
                .nullable(true)
                .primitiveCheck(true)
                .persist(true)
                .namingRowIndex(1)
                .build();
    }
}
