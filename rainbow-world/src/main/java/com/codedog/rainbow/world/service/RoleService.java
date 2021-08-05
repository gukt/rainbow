/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.service;

import com.codedog.rainbow.domain.Role;
import com.codedog.rainbow.repository.RoleRepository;
import com.codedog.rainbow.tcp.message.MessageHandlerException;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.util.IdGenerator;
import com.codedog.rainbow.world.generated.*;
import com.codedog.rainbow.world.generated.Number;
import com.codedog.rainbow.world.generated.CommonProto.Error.ErrorCode;
import com.codedog.rainbow.world.generated.RoleInfo.Builder;
import com.codedog.rainbow.world.generated.RoleServiceGrpc.RoleServiceImplBase;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author https://github.com/gukt
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService extends RoleServiceImplBase {

    @NonNull
    private final RoleRepository roleRepository;

    /**
     * Converter function: Role -> RoleInfo
     */
    private final Function<Role, RoleInfo> role2RoleInfoConverter = r -> RoleInfo.newBuilder()
            .setId(r.getId())
            .setCreateTime(r.getCreatedAt().getTime())
            .build();

    public Role save(Role entity) {
        if (entity.getId() == null) {
            entity.setId(IdGenerator.nextId());
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(new Date());
        }
        roleRepository.save(entity);
        return entity;
    }

    @Nullable
    public Role findByOpenId(String openId) {
        Assert.notNull(openId, "openId");
        return roleRepository.findByOpenId(openId);
    }

    public Role findByOpenIdOrCreate(String openId, GameEnterRequest request) {
        Assert.notNull(request, "request");
        Role role = findByOpenId(openId);
        if (role == null) {
            try {
                role = create(request);
                log.info("成功创建角色: {}", role);
            } catch (Exception e) {
                log.error("创建角色失败", e);
                throw new MessageHandlerException(ErrorCode.ROLE_CREATION_FAILED_VALUE, "创建角色失败，请稍后重试");
            }
        }
        return role;
    }

    public Role findByOpenIdOrCreate2(String openId, Map<String, Object> payload) {
        Assert.notNull(payload, "payload");
        Role role = findByOpenId(openId);
        if (role == null) {
            role = create2(payload);
            log.info("成功创建角色: {}", role);
        }
        return role;
    }

    /**
     * 使用指定的 {@link GameEnterRequest GameEnterRequest} 请求对象，创建一个角色对象。
     *
     * @param request GameEnterRequest object, must not be null.
     * @return 新创建的角色对象
     */
    public Role create(GameEnterRequest request) {
        Date now = new Date();
        Role role = new Role();
        role.setId(IdGenerator.nextId());
        role.setUid(request.getUid());
        role.setOpenId(request.getOpenId());
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        role.setLoginTime(now);
        return roleRepository.save(role);
    }

    public Role create2(Map<String, Object> payload) {
        Date now = new Date();
        Role role = new Role();
        role.setId(IdGenerator.nextId());
        role.setUid((Long) payload.get("uid"));
        role.setOpenId((String) payload.get("openId"));
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        role.setLoginTime(now);
        return roleRepository.save(role);
    }

    public Role getOne(Long id) {
        return roleRepository.getOne(id);
    }

    /**
     * =============== + RPC implements ===============
     */

    @Override
    public void findAll(RoleListRequest request, StreamObserver<RoleListResponse> responseObserver) {
        List<Role> roles = new ArrayList<>();
        int totalPage = 1;
        int page = request.getPage();
        int pageSize = request.getPageSize();
        pageSize = pageSize <= 0 ? 15 : pageSize;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Direction.ASC, "id");

        if (request.getRoleId() != 0) {
            // 表示根据id查找role
            Optional<Role> role = roleRepository.findById(request.getRoleId());
            role.ifPresent(roles::add);
        } else if (!Strings.isNullOrEmpty(request.getKeyword())) {
            // 根据昵称或id模糊查询
            Page<Role> paged = roleRepository.findAll((Specification<Role>) (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                String kw = request.getKeyword();
                if (!Strings.isNullOrEmpty(kw)) {
                    predicates.add(cb.or(
                            cb.like(root.get("id").as(String.class), "%" + kw + "%"),
                            cb.like(root.get("nick"), "%" + kw + "%")
                    ));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }, pageRequest);
            roles = Lists.newArrayList(paged);
            totalPage = paged.getTotalPages();
        } else {
            // 查询所有
            Page<Role> paged = roleRepository.findAll(pageRequest);
            roles = Lists.newArrayList(paged);
            totalPage = paged.getTotalPages();
        }

        RoleListResponse response = RoleListResponse.newBuilder()
                .addAllRole(roles.stream().map(role2RoleInfoConverter).collect(Collectors.toList()))
                .setTotalPage(totalPage)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findOne(Number request, StreamObserver<RoleInfo> responseObserver) {
        // if (true) {
        //     throw new RuntimeException("my exception");
        // }
        long roleId = request.getLongValue();
        Optional<Role> role = roleRepository.findById(roleId);
        Builder response = RoleInfo.newBuilder();
        if (role.isPresent()) {
            Role r = role.get();
            response.setId(r.getId());
            response.setCreateTime(r.getCreatedAt().getTime());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ban(BanRequest request, StreamObserver<Result> responseObserver) {
        Role r = roleRepository.getById(request.getRoleId());
        if (request.getBanTime() == -1 && r.getBlockedUntil() != null) {
            // 取消封禁
            r.setBlockedUntil(null);
        } else {
            // 封禁
            r.setBlockedUntil(new Date(request.getBanTime()));
        }
        roleRepository.save(r);

        responseObserver.onNext(Result.getDefaultInstance());
        responseObserver.onCompleted();
    }

//    RPC implements
}
