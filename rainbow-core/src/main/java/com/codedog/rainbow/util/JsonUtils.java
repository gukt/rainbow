/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TOOD: Resolve the following case:
 * boolean : null
 * List: 1,2,,3
 * List: 1,2,3   很多空格
 * Set: 1,1,2,3 很多重复
 * List<String>: a,b,c  want: "a","b","c"
 * [[1,2,3],[4,5,6]后面少一个空格的问题
 * [1,2,3],[4,5,6] 少了前后两个【】的格式问题
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * TypeReference: List<Integer>
     */
    public static final TypeReference<List<Integer>> TYPE_REF_LIST_INT = new TypeReference<List<Integer>>() {};
    /**
     * TypeReference: List<Long>
     */
    public static final TypeReference<List<Long>> TYPE_REF_LIST_LONG = new TypeReference<List<Long>>() {};
    /**
     * TypeReference: List<String>
     */
    public static final TypeReference<List<String>> TYPE_REF_LIST_STR = new TypeReference<List<String>>() {};
    /**
     * TypeReference: Set<Integer>
     */
    public static final TypeReference<Set<Integer>> TYPE_REF_SET_INT = new TypeReference<Set<Integer>>() {};
    /**
     * TypeReference: Set<Long>
     */
    public static final TypeReference<Set<Long>> TYPE_REF_SET_LONG = new TypeReference<Set<Long>>() {};
    /**
     * TypeReference: Set<String>
     */
    public static final TypeReference<Set<String>> TYPE_REF_SET_STR = new TypeReference<Set<String>>() {};
    /**
     * TypeReference: Map<String, Object>
     */
    public static final TypeReference<Map<String, Object>> TYPE_REF_MAP_STR_OBJECT =
            new TypeReference<Map<String, Object>>() {};
    /**
     * TypeReference: Map<String, String>
     */
    public static final TypeReference<Map<String, String>> TYPE_REF_MAP_STR_STR =
            new TypeReference<Map<String, String>>() {};
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new SimpleModule());
        OBJECT_MAPPER.registerModule(new GuavaModule());
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static String toJson(@Nullable Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException(e);
        }
    }

    /**
     * 将指定的JSON格式的字符串解析成指定类型的对象
     *
     * @param <V>  返回的对象类型
     * @param json 要解析成对象的JSON格式字符串
     * @param type 指定要解析成对象的类型
     * @return 返回解析对象
     * @throws RuntimeException 非检查型异常,表示解析过程中产生的错误
     */
    @Nullable
    public static <V> V toBean(@Nullable String json, Class<V> type) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new JsonConversionException(String.format("json: %s, type: %s", json, type), e);
        }
    }

    public static <V> V toBeanOrDefault(@Nullable String json, Class<V> type, V defaultValue) {
        V bean = toBean(json, type);
        return bean != null ? bean : defaultValue;
    }

    /**
     * 将指定的JSON格式的字符串解析成指定类型的对象
     *
     * @param <T>     返回的对象类型
     * @param json    要解析成对象的JSON格式字符串
     * @param typeRef 指定要解析成对象的类型
     * @return 返回解析对象
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T toBean(@Nullable String json, TypeReference<T> typeRef) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            throw new JsonConversionException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V toBeanOrDefault(@Nullable String json, TypeReference<V> typeRef, V defaultValue) {
        V bean = toBean(json, typeRef);
        return bean == null ? defaultValue : bean;
    }

    /**
     * 将指定的JSON格式的字符串解析成指定类型的对象
     *
     * @param <V>  返回的对象类型
     * @param json 要解析成对象的JSON格式字符串
     * @param type 指定要解析成对象的类型
     * @return 返回解析对象
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <V> V toBean(@Nullable String json, JavaType type) {
        try {
            if (!Strings.isNullOrEmpty(json)) {
                return OBJECT_MAPPER.readValue(json, type);
            }
            return null;
        } catch (IOException e) {
            throw new JsonConversionException(e);
        }
    }

    public static <V> V toBeanOrDefault(@Nullable String json, JavaType type, V defaultValue) {
        V value = toBean(json, type);
        return value != null ? value : defaultValue;
    }

    static class JsonConversionException extends RuntimeException {

        JsonConversionException(Throwable e) {
            super("JSON: Conversion exception", e);
        }

        JsonConversionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
