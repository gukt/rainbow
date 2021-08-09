/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.world.generated.TempProto.Foo;
import org.junit.jupiter.api.Test;

/**
 * ProtoTests class
 *
 * @author https://github.com/gukt
 */
public class ProtoTests {

    @Test
    public void test1() {
        Foo foo = Foo.getDefaultInstance();
    }
}
