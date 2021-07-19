/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.core.rest.ApiResultBodyAdvice;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * MyRunner class
 *
 * @author https://github.com/gukt
 */
@Component
public class MyRunner implements CommandLineRunner {

    private final ApplicationContext context;

    public MyRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        ApiResultBodyAdvice advice = context.getBean(ApiResultBodyAdvice.class);
        advice.addApiResultViewClasses(JsonViews.class.getClasses());
    }
}
