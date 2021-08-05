/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.service;

import com.codedog.rainbow.lang.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author https://github.com/gukt
 */
@Service
@Slf4j
public class RedisService {

     void updateRole(Serializable roleId, String key, Object value) {
          throw new NotImplementedException();
     }
}
