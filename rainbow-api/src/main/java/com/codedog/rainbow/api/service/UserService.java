/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.service;

import com.codedog.rainbow.api.criteria.Predicates;
import com.codedog.rainbow.api.criteria.UserQueryCriteria;
import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.repository.UserRepository;
import com.codedog.rainbow.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ServerService class
 *
 * @author https://github.com/gukt
 */
@Service
@CacheConfig(cacheNames = "cache:users")
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据名称获取 User 对象，结果会被缓存
     *
     * @param name 用户名，不可为空。
     * @return an user object.
     */
    @CachePut(key = "#name")
    @Nullable
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

//    public User existsByName(String name) {
////        return userRepository.exists(new Example<User>() {
////            @Override
////            public User getProbe() {
////                return null;
////            }
////
////            @Override
////            public ExampleMatcher getMatcher() {
////                return null;
////            }
////        })
//    }

    /**
     * 保存（创建或更新） User 对象，如果 isAdd 为 true 表示新增，反之表示更新
     *
     * @param entity 正在保存的 User 对象
     * @param isAdd  是否是新增
     * @return 保存后的 User 对象
     */
    @Cacheable(key = "#result.name")
    public User save(User entity, boolean isAdd) throws EntityNotFoundException {
        Objects.requireNonNull(entity);
        Date now = new Date();
        User saving = entity;
        if (isAdd) {
            saving.setInactive(false);
            saving.setLoginTime(now);
            if (saving.getLoginIp() == null) {
                saving.setLoginIp("");
            }
            saving.setType(0);
            saving.setCreatedAt(now);
        } else {
//            saving = userRepository.getById(entity.getId());
//            BeanUtils.copyProperties(entity, saving, true, "id");

            // 如果碰到
            Optional<User> result = userRepository.findById(entity.getId());
            if (result.isPresent()) {
                saving = result.get();
            } else {
//                log.warn("[Ignored updates] Unable to find an user with id {}", entity.getId());
                throw new EntityNotFoundException("Unable to find an user with id " + entity.getId());
            }
        }
        saving.setUpdatedAt(now);
        return userRepository.save(saving);
    }

    public Page<User> search(UserQueryCriteria criteria, Pageable page) {
        Specification<User> spec =
                (root, query, cb) -> {
                    List<Predicate> predicates = Predicates.from(criteria, root, cb);
                    // And name like '...'
                    if (!ObjectUtils.isNullOrEmpty(criteria.getQ())) {
                        String kw = "%" + criteria.getQ() + "%";
                        predicates.add(cb.or(
                                cb.like(root.get("id"), kw),
                                cb.like(root.get("name"), kw))
                        );
                    }
                    // And loginTime >= ...
                    if (criteria.getLoginStart() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("loginTime"), criteria.getLoginStart()));
                    }
                    // And loginTime <= ...
                    if (criteria.getLoginEnd() != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("loginTime"), criteria.getLoginEnd()));
                    }
                    // And blockUntil >= ...
                    if (criteria.getBlockStart() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("blockUntil"), criteria.getBlockStart()));
                    }
                    // And blockUntil <= ...
                    if (criteria.getBlockEnd() != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("blockUntil"), criteria.getBlockEnd()));
                    }
                    Predicate[] arr = new Predicate[predicates.size()];
                    return cb.and(predicates.toArray(arr));
                };
        return userRepository.findAll(spec, page);
    }

    /**
     * 根据 ID 查询指定的用户
     *
     * @param id 用户 ID
     * @return 和 Id 关联的用户
     */
    public User getById(Long id) {
        return userRepository.getById(id);
    }

    /**
     * 根据 IDs 查询指定的多个用户，其实内部是通过 {@link #search(UserQueryCriteria, Pageable)} 方法查询的。
     * TODO 好像可以省略,没必要提供这个快捷方法
     *
     * @param ids  用户 IDs
     * @param page 分页参数
     * @return 和 Ids 关联的用户
     */
    public Page<User> getByIds(Set<Long> ids, Pageable page) {
        return search(UserQueryCriteria.of(ids), page);
    }

    public int removeByIds(Set<Long> ids, boolean force) {
        ObjectUtils.requireNonEmpty(ids, "ids");
        if (force) {
            // 物理删除
            return userRepository.deleteAllByIdIn(ids);
        }
        AtomicInteger rows = new AtomicInteger();
        // 逻辑删除
        ids.forEach(id -> {
            Optional<User> user = userRepository.findById(id);
            user.ifPresent(entity -> {
                entity.setInactive(true);
                entity.setUpdatedAt(new Date());
                userRepository.save(entity);
                rows.getAndIncrement();
            });
        });
        return rows.get();
    }
}
