package com.codedog.rainbow;

import com.codedog.rainbow.api.service.UserService;
import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.util.IdGenerator;
import com.codedog.rainbow.util.JsonUtils;
import com.codedog.rainbow.util.MapUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.codedog.rainbow.util.TestUtils.apiResultIsOk;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 默认情况下，@SpringBootTest 是不启动 web 服务器的，r
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserEndpointTests {

    @Autowired private UserService userService;

    @Test
    void testRegister(@Autowired MockMvc mvc) throws Exception {
        // 产生一个全局唯一 ID
        long id = IdGenerator.nextId();
        String body = "{\"id\":" + id + ", \"name\":\"xxx\", \"password\":\"aaaaaa\"}";
        mvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    @Test
    @Transactional
    void testLogin0(@Autowired MockMvc mvc, @Autowired UserService userService) throws Exception {
        User u = userService.getById(1L);
        Map<String, Object> bodyMap = MapUtils.newHashMap();
        bodyMap.put("name", u.getName());
        bodyMap.put("password", u.getPassword());
        mvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
//                .param("name","name1").param("password", "pwd1")
                .content(JsonUtils.toJson(bodyMap)))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    @Test
    void testLogout(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"delete\":[1,2,3]}";
        mvc.perform(post("/api/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    @Test
    void testSearchUsers(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/api/users")
                .param("ids", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    @Test
    void testGetUserById(@Autowired MockMvc mvc) throws Exception {
        long uid = 1;
        mvc.perform(get("/api/users/" + uid))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    @Test
    void testExistsByName(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"delete\":[1,2,3]}";
        mvc.perform(post("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    /**
     * 测试重置密码
     */
    @Test
    void testResetPassword(@Autowired MockMvc mvc) throws Exception {
        long uid = 1;
        mvc.perform(post("/api/users/" + uid + "/reset-password"))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    /**
     * 测试批量删除
     */
    @Test
    void testBatchDeleting(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"delete\":[1,2,3]}";
        mvc.perform(post("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    /**
     * 测试批量更新
     */
    @Test
    void testBatchUpdating(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"update\": [{ \"id\": 1, \"name\": \"xxx\" },{ \"id\":2, \"name\": \"yyy\" }]}";
        mvc.perform(post("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

    /**
     * 测试批量添加
     */
    @Test
    void testBatchAdding(@Autowired MockMvc mvc) throws Exception {
        String body = "{\"add\":[{ \"id\": 3, \"name\": \"zzz\" }]}";
        mvc.perform(post("/api/users/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(apiResultIsOk());
    }

}
