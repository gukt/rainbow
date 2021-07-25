/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

/**
 * RequestBodyData class
 * TODO 如果继承自 BodyData, 则这里定义的 JsonProperty 没有起作用，看看怎么回事
 * @author https://github.com/gukt
 */
@Data
public class BatchRequestBody<T, ID> {

    @JsonProperty("delete")
    private Set<ID> deleteIds;
    @JsonProperty("update")
    private List<T> updatingEntities;
    @JsonProperty("add")
    private List<T> addingEntities;
}
