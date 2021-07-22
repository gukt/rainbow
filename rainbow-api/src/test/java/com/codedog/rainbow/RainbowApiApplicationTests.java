package com.codedog.rainbow;

import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class RainbowApiApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test1() {
        String json = "{ \"id\": 1, \"name\": \"xxx\" }";
        User u = JsonUtils.toBean(json, User.class);
        System.out.println(u);
    }

}
