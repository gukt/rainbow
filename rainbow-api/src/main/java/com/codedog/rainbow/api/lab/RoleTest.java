/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.lab;

/**
 * RoleTest class
 *
 * @author https://github.com/gukt
 */
public class RoleTest {

    //    @Role("do")
//    @Role("something")
    @Roles({
            @Role("tom"),
            @Role("jerry")
    })
    public String doSomething() {
        return "";
    }
}
