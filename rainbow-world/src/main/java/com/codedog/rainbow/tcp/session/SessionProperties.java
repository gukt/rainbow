/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.session;

import lombok.Getter;
import lombok.Setter;

/**
 * SessionProperties class
 *
 * @author https://github.com/gukt
 */
@Getter
@Setter
public class SessionProperties {

    int maxPendingRequestSize;

    int maxCacheResponseSize;
}
