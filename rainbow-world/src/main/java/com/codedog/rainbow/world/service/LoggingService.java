/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.service;

import com.codedog.rainbow.domain.Role;
import com.codedog.rainbow.world.generated.GameEnterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LoggingService class
 *
 * @author https://github.com/gukt
 */
@Service
@Slf4j
public class LoggingService {

    // Inject some services here.

    public void recordRoleCreating(Role role, GameEnterRequest request) {
        // Code goes here.
    }

    public void writeRoleEnteringLog(Role role, GameEnterRequest request) {
        // Code goes here.
    }

    public void writeRoleInfoLog(Role role) {
        // Code goes here.
    }
}
