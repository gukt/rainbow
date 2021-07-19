/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author https://github.com/gukt
 */
@JsonIgnoreProperties({"pageable", "sort"})
public class JsonPage<T> extends PageImpl<T> {

    private JsonPage(final List<T> content, final Pageable page, final long total) {
        super(content, page, total);
    }

    public static JsonPage<?> of(final Page<?> page) {
        return new JsonPage<>(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    @JsonView(ApiResultView.class)
    public int getPage() {
        return super.getPageable().getPageNumber();
    }

    @Override
    @JsonView(ApiResultView.class)
    public int getTotalPages() {
        return super.getTotalPages();
    }

    @Override
    @JsonView(ApiResultView.class)
    public long getTotalElements() {
        return super.getTotalElements();
    }

    @Override
    @JsonView(ApiResultView.class)
    public boolean hasNext() {
        return super.hasNext();
    }

    @Override
    @JsonView(ApiResultView.class)
    public boolean hasContent() {
        return super.hasContent();
    }

    @Override
    @JsonView(ApiResultView.class)
    public List<T> getContent() {
        return super.getContent();
    }
}
