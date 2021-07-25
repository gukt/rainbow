/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.codedog.rainbow.util.ObjectUtils.isNotEmpty;

/**
 * Specifications class
 *
 * @author https://github.com/gukt
 */
public class Predicates {

    public static List<Predicate> from(EntityQueryCriteria queryCriteria, Root<?> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        // id in (?)
        if (queryCriteria instanceof FieldIdAwareQueryCriteria) {
            Set<?> ids = ((FieldIdAwareQueryCriteria<?>) queryCriteria).getIds();
            if (isNotEmpty(ids)) {
                predicates.add(root.get("id").in(ids));
            }
        }
        // state in (?)
        if (queryCriteria instanceof FieldStateAwareQueryCriteria) {
            Set<Integer> states = ((FieldStateAwareQueryCriteria) queryCriteria).getStates();
            if (isNotEmpty(states)) {
                predicates.add(root.get("state").in(states));
            }
        }
        // inactive = ?
        if (queryCriteria instanceof FieldInactiveAwareQueryCriteria) {
            Boolean inactive = ((FieldInactiveAwareQueryCriteria) queryCriteria).isInactive();
            if (inactive != null) {
                predicates.add(cb.equal(root.get("inactive"), inactive));
            }
        }
        // createdAt between (...)
        if (queryCriteria instanceof FieldCreatedAtAwareQueryCriteria) {
            FieldCreatedAtAwareQueryCriteria criteria = (FieldCreatedAtAwareQueryCriteria) queryCriteria;
            // And createdAt >= ...
            if (criteria.getCreatedStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedStart()));
            }
            // And createdAt <= ...
            if (criteria.getCreatedEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdEnd"), criteria.getCreatedEnd()));
            }
        }
        return predicates;
    }
}
