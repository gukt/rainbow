/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.domain;

import com.codedog.rainbow.support.AbstractAttributeSupport;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Entity
@Table(name = "t_roles")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Role extends AbstractAttributeSupport {

    @Id
    @NonNull
    private Long id;
    private String openId;
    private String nick;
    private String avatar;
    private Long gold;
    private Date banTime;
    private Date createdTime;
}
