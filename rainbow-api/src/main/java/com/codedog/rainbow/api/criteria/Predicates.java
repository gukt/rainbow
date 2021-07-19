/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.criteria;

import com.codedog.rainbow.util.MoreObjects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Specifications class
 *
 * @author https://github.com/gukt
 */
public class Predicates {

    public static List<Predicate> from(EntityQueryCriteria queryCriteria, Root<?> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (queryCriteria instanceof FieldIdAwareQueryCriteria) {
            // id = ?
            Serializable id = ((FieldIdAwareQueryCriteria<?>) queryCriteria).getId();
            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            // id in (?)
            Set<?> ids = ((FieldIdAwareQueryCriteria<?>) queryCriteria).getIds();
            if (!MoreObjects.isNullOrEmpty(ids)) {
                predicates.add(root.get("id").in(ids));
            }
        }
        if (queryCriteria instanceof FieldStateAwareQueryCriteria) {
            Serializable state = ((FieldStateAwareQueryCriteria) queryCriteria).getState();
            // state = ?
            if (state != null) {
                predicates.add(cb.equal(root.get("state"), state));
            }
            // state in (?)
            Set<Integer> states = ((FieldStateAwareQueryCriteria) queryCriteria).getStates();
            if (!MoreObjects.isNullOrEmpty(states)) {
                predicates.add(root.get("state").in(states));
            }
        }
        return predicates;
    }
}
