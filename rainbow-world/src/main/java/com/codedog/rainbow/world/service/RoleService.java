/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.service;

import com.codedog.rainbow.world.domain.RoleRepository;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;
import com.codedog.rainbow.world.domain.Role;
import com.codedog.rainbow.world.generated.BanRequest;
import com.codedog.rainbow.world.generated.Number;
import com.codedog.rainbow.world.generated.Result;
import com.codedog.rainbow.world.generated.RoleInfo;
import com.codedog.rainbow.world.generated.RoleInfo.Builder;
import com.codedog.rainbow.world.generated.RoleListRequest;
import com.codedog.rainbow.world.generated.RoleListResponse;
import com.codedog.rainbow.world.generated.RoleServiceGrpc.RoleServiceImplBase;
import com.codedog.rainbow.util.IdGenerator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
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
    private Function<Role, @Nullable RoleInfo> role2RoleInfoConverter = r -> RoleInfo.newBuilder()
            .setId(r.getId())
            .setNick(Strings.nullToEmpty(r.getNick()))
            .setGold(r.getGold())
            .setBanTime(r.getBanTime().getTime())
            .setCreateTime(r.getCreatedTime().getTime())
            .build();

    public Role save(Role entity) {
        if (entity.getId() == null) {
            entity.setId(IdGenerator.nextId());
        }
        if (entity.getCreatedTime() == null) {
            entity.setCreatedTime(new Date());
        }
        roleRepository.save(entity);
        return entity;
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
            response.setNick(r.getNick());
            response.setAvatar(r.getAvatar());
            response.setBanTime(r.getBanTime().getTime());
            response.setCreateTime(r.getCreatedTime().getTime());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ban(BanRequest request, StreamObserver<Result> responseObserver) {
        Role r = roleRepository.getOne(request.getRoleId());
        if (request.getBanTime() == -1 && r.getBanTime() != null) {
            // 取消封禁
            r.setBanTime(null);
        } else {
            // 封禁
            r.setBanTime(new Date(request.getBanTime()));
        }
        roleRepository.save(r);

        responseObserver.onNext(Result.getDefaultInstance());
        responseObserver.onCompleted();
    }

    /**
     * =============== - RPC implements ===============
     */
}
