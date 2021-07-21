/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 这是 {@link PageImpl} 对象的装饰者。
 * <p></p>
 * 指定序列化时，忽略掉 {@link PageImpl} 对象的一些属性，比如：
 * <ul>
 *     <li>pageable - 多了一层，访问麻烦，常用的通过方法提取到外层，比如: {@link #getPage()}</li>
 *     <li>sort - 一般情况下用不到</li>
 *     <li>number - 为保持和传参名称统一，去掉 {@link PageImpl#getNumber()}，使用 {@link #getPage()} 表示当前页码</li>
 *     <li>pageable - 将常用的写个方法提取到外层，因为多了一层会给 API 调用者造成麻烦</li>
 *     <li>empty - 和 {@link #hasContent()} 语义上相同，但 hasContent 语义理解更直观，所以去掉 empty</li>
 * </ul>
 *
 * @author https://github.com/gukt
 */
@JsonView(ApiResultView.class)
@JsonIgnoreProperties({"pageable", "sort", "number", "empty"})
public class ApiResultViewAwarePage<T> extends PageImpl<T> {

    private ApiResultViewAwarePage(final List<T> content, final Pageable page, final long total) {
        super(content, page, total);
    }

    public static ApiResultViewAwarePage<?> of(final Page<?> page) {
        return new ApiResultViewAwarePage<>(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    public static <T> ApiResultViewAwarePage<?> of(final List<T> content, final Pageable page, final long total) {
        return new ApiResultViewAwarePage<>(content, page, total);
    }

    /**
     * 当前页码，由于 {@link PageImpl} 对象将分页相关的信息放在 {@link PageImpl#getPageable()} 里返回，
     * 多了一个层级，这里将其读取出来放到外层。
     *
     * @return 当前页码
     */
    public int getPage() {
        return super.getPageable().getPageNumber();
    }

    /**
     * 是否还有下一页。提供该字段是为了方便 API 调用者。
     * 尽管通过 {@link #getTotalPages() totalPages} - {@link #getPage() page} > 0 也可以判断。
     */
    @JsonProperty
    public boolean hasNext() {
        return super.hasNext();
    }

    /**
     * 本页是否有内容。提供该字段是为了方便 API 调用者。
     * 尽管通过 {@link #getNumberOfElements()} () numberOfElements} > 0 也可以判断。
     */
    @JsonProperty
    public boolean hasContent() {
        return super.hasContent();
    }
}
