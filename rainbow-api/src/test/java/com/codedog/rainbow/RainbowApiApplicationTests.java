package com.codedog.rainbow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(args = "--app.test=one")
class RainbowApiApplicationTests {

    @Test
    void contextLoads() {
    }

    /**
     * 可以在 @SpringBootTest 中指定命令行参数，用于测试那些依赖于命令行参数的场景，以下只是个示例。
     *
     * @param args 命令行参数
     * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing.spring-boot-applications.using-application-arguments">Using Application Arguments</a>
     */
    @Test
    void testArguments(@Autowired ApplicationArguments args) {
        assertThat(args.getOptionNames()).containsOnly("app.test");
        assertThat(args.getOptionValues("app.test")).containsOnly(("one"));
    }
}
