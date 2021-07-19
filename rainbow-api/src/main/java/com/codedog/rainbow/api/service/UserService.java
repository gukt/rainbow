/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.service;

import com.codedog.rainbow.api.criteria.Predicates;
import com.codedog.rainbow.api.criteria.UserQueryCriteria;
import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.repository.UserRepository;
import com.codedog.rainbow.util.ObjectUtils;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public User getByName(String name) {
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
     * @param entity
     * @param isAdd
     * @return
     */
    @Cacheable(key = "#result.name")
    public User save(User entity, boolean isAdd) {
        Objects.requireNonNull(entity);
        Date now = new Date();
        if (isAdd) {
            entity.setCreatedAt(now);
        } else {
            entity.setUpdatedAt(now);
        }
        userRepository.save(entity);
        return entity;
    }

    public Page<User> search(UserQueryCriteria criteria, Pageable page) {
        Specification<User> spec =
                (root, query, cb) -> {
                    List<Predicate> predicates = Predicates.from(criteria, root, cb);
                    // And name like '...'
                    if (!ObjectUtils.isNullOrEmpty(criteria.getQ())) {
                        String kw = "%" + criteria.getQ() + "%";
                        predicates.add(cb.like(root.get("name"), kw));
                    }
                    // And createdAt >= ...
                    if (criteria.getCreatedStart() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedStart()));
                    }
                    // And createdAt <= ...
                    if (criteria.getCreatedEnd() != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedEnd()));
                    }
                    Predicate[] arr = new Predicate[predicates.size()];
                    return cb.and(predicates.toArray(arr));
                };
        return userRepository.findAll(spec, page);
    }

    public User getById(long id) {
        return userRepository.getById(id);
    }

    public Page<User> getByIds(Set<Long> ids, Pageable page) {
        ObjectUtils.requireNonEmpty(ids, "ids");
        UserQueryCriteria criteria = new UserQueryCriteria();
        criteria.setIds(ids);
        return (Page<User>) search(criteria, page);
    }

    public int removeById(long id, boolean force) {
        ObjectUtils.requirePositive(id, "id");
        return removeByIds(Sets.newHashSet(id), force);
    }

    public int removeByIds(Set<Long> ids, boolean force) {
        ObjectUtils.requireNonEmpty(ids, "ids");
        if (force) {
            // 物理删除
            return userRepository.deleteAllByIdIn(ids);
        }
        // 逻辑删除
        ids.forEach(id -> {
            User entity = getById(id);
            entity.setState(-1);
            entity.setUpdatedAt(new Date());
            userRepository.save(entity);
        });
        return ids.size();
    }
}
