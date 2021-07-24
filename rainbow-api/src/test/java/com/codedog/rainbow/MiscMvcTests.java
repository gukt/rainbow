/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MiscEndpointTests class
 *
 * @author https://github.com/gukt
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MiscMvcTests {

    @Test
    void testApiHome(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(content().string("It works."));
    }

    @Test
    void testHome(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
