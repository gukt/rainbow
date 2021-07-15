/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RainbowApiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RainbowApiApplication.class, args);
    }
}
