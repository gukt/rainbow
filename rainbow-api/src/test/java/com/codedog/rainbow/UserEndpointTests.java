package com.codedog.rainbow;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 默认情况下，@SpringBootTest 是不启动 web 服务器的，r
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserEndpointTests {

    private final ResultMatcher apiResultIsOk = jsonPath("$.code").value(0);

    @Test
    void contextLoads() {
    }

    @Test
    void testBatchDeleting(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"delete\":[1,2,3]}";
        mvc.perform(post("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk);
    }

    @Test
    void testBatchUpdating(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"update\": [{ \"id\": 1, \"name\": \"xxx\" },{ \"id\":2, \"name\": \"yyy\" }]}";
        mvc.perform(post("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk);
    }

    @Test
    void testBatchAdding(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"add\":[{ \"id\": 3, \"name\": \"zzz\" }]}";
        mvc.perform(post("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk);
    }

}
