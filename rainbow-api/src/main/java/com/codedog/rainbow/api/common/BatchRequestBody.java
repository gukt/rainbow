/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.api.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * RequestBodyData class
 *
 * @author https://github.com/gukt
 */
@Data
public class BatchRequestBody<T, ID> extends BodyData {

    @JsonProperty("delete")
    private Set<ID> deleteIds;
    @JsonProperty("update")
    private List<T> updatingEntities;
    @JsonProperty("add")
    private List<T> addingEntities;
}
