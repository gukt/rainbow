/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.service;

import com.codedog.rainbow.domain.Mail;
import com.codedog.rainbow.repository.MailRepository;
import com.codedog.rainbow.world.generated.RoleServiceGrpc.RoleServiceImplBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author https://github.com/gukt
 */
@Service
@Slf4j
public class MailService extends RoleServiceImplBase {

    private final MailRepository mailRepository;

    public MailService(MailRepository mailRepository) {this.mailRepository = mailRepository;}

    public List<Mail> findByRoleId(long roleId) {
        // List<Mail> mails = mailRepository.findAllByRoleId(roleId);
        return null;
    }

}
