package com.codedog.rainbow.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JSON 序列化和反序列化工具类
 *
 * @author https://github.com/gukt
 */
public final class JsonUtils {

    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .registerModule(new SimpleModule())
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule())
                    .registerModule(new GuavaModule())
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL) // null 不参与转换
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private static final TypeFactory typeFactory;

    static {
        typeFactory = objectMapper.getTypeFactory();
    }

    /** Prevents to construct an instance. */
    private JsonUtils() {
        throw new AssertionError("No JsonUtils instances for you.");
    }

    @Nullable
    public static <E> E toBean(@Nullable String s, Class<E> type) {
        Objects.requireNonNull(type);

        if (s != null && !s.isEmpty()) {
            try {
                return objectMapper.readValue(s, type);
            } catch (JsonProcessingException e) {
                deserializeFailed(s, type, e);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E> E toBeanOrDefault(@Nullable String s, E defaultValue) {
        Objects.requireNonNull(defaultValue);

        E value = toBean(s, (Class<E>) defaultValue.getClass());
        return value != null ? value : defaultValue;
    }

    @Nullable
    public static <E> E toBean(@Nullable String s, TypeReference<E> typeRef) {
        Objects.requireNonNull(typeRef);

        if (s != null && !s.isEmpty()) {
            try {
                return objectMapper.readValue(s, typeRef);
            } catch (JsonProcessingException e) {
                deserializeFailed(s, typeRef.getType(), e);
            }
        }
        return null;
    }

    public static <E> E toBeanOrDefault(@Nullable String s, TypeReference<E> typeRef, E defaultValue) {
        Objects.requireNonNull(typeRef);
        Objects.requireNonNull(defaultValue);

        E value = toBean(s, typeRef);
        return value != null ? value : defaultValue;
    }

    @Nullable
    public static String toJson(@Nullable Object object) {
        if (object != null) {
            try {
                return objectMapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                serializeFailed(object, e);
            }
        }
        return null;
    }

    public static String toJsonOrDefault(@Nullable Object object, String defaultValue) {
        Objects.requireNonNull(defaultValue);

        String value = toJson(object);
        return value != null ? value : defaultValue;
    }

    // Collection

    @Nullable
    public static <E> Collection<E> toCollection(
            @Nullable String s, Class<? extends Collection> collectionClass, Class<E> elementType) {
        Objects.requireNonNull(collectionClass);
        Objects.requireNonNull(elementType);

        if (s != null && !s.isEmpty()) {
            JavaType javaType = typeFactory.constructCollectionType(collectionClass, elementType);
            try {
                return objectMapper.readValue(s, javaType);
            } catch (JsonProcessingException e) {
                deserializeFailed(s, javaType, e);
            }
        }
        return null;
    }

    public static <E> Collection<E> toCollectionOrDefault(
            @Nullable String s, Class<E> elementType, Collection<E> defaultValue) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(defaultValue);

        Collection<E> value = toCollection(s, defaultValue.getClass(), elementType);
        return value != null ? value : defaultValue;
    }

    @Nullable
    public static <E> List<E> toList(@Nullable String s, Class<E> elementType) {
        Objects.requireNonNull(elementType);
        return (List<E>) toCollection(s, ArrayList.class, elementType);
    }

    public static <E> List<E> toListOrDefault(
            @Nullable String s, Class<E> elementType, List<E> defaultValue) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(defaultValue);

        List<E> value = toList(s, elementType);
        return value != null ? value : defaultValue;
    }

    public static <E> List<E> toListOrEmpty(@Nullable String s, Class<E> elementType) {
        Objects.requireNonNull(elementType);
        return toListOrDefault(s, elementType, new ArrayList<>());
    }

    // toSet

    @Nullable
    public static <E> Set<E> toSet(@Nullable String s, Class<E> elementType) {
        Objects.requireNonNull(elementType);
        return (Set<E>) toCollection(s, HashSet.class, elementType);
    }

    public static <E> Set<E> toSetOrDefault(
            @Nullable String s, Class<E> elementType, Set<E> defaultValue) {
        Objects.requireNonNull(elementType);
        Objects.requireNonNull(defaultValue);

        Set<E> value = toSet(s, elementType);
        return value != null ? value : defaultValue;
    }

    public static <E> Set<E> toSetOrEmpty(@Nullable String s, Class<E> elementType) {
        Objects.requireNonNull(elementType);
        return toSetOrDefault(s, elementType, new HashSet<>());
    }

    // toMap

    @Nullable
    public static <K, V> Map<K, V> toMap(
            @Nullable String s, Class<? extends Map> mapClass, Class<K> keyType, Class<V> valueType) {
        Objects.requireNonNull(mapClass);
        Objects.requireNonNull(keyType);
        Objects.requireNonNull(valueType);

        if (s != null && !s.isEmpty()) {
            JavaType javaType = typeFactory.constructMapType(mapClass, keyType, valueType);
            try {
                return objectMapper.readValue(s, javaType);
            } catch (JsonProcessingException e) {
                deserializeFailed(s, javaType, e);
            }
        }
        return null;
    }

    @Nullable
    public static <K, V> Map<K, V> toMap(@Nullable String s, Class<K> keyType, Class<V> valueType) {
        return toMap(s, HashMap.class, keyType, valueType);
    }

    public static <K, V> Map<K, V> toMapOrDefault(
            @Nullable String s, Class<K> keyType, Class<V> valueType, Map<K, V> defaultValue) {
        Objects.requireNonNull(defaultValue);
        Objects.requireNonNull(keyType);
        Objects.requireNonNull(valueType);

        Map<K, V> value = toMap(s, keyType, valueType);
        return value != null ? value : defaultValue;
    }

    public static <K, V> Map<K, V> toMapOrEmpty(
            @Nullable String s, Class<K> keyType, Class<V> valueType) {
        Objects.requireNonNull(keyType);
        Objects.requireNonNull(valueType);

        return toMapOrDefault(s, keyType, valueType, new HashMap<>());
    }

    // Private methods

    private static void serializeFailed(Object object, Throwable e) {
        throw new RuntimeException(String.format("序列化失败, source: %s", object), e);
    }

    private static void deserializeFailed(String s, Type type, Throwable e) {
        throw new RuntimeException(
                String.format("反序列化为 %s 类型对象时失败, source: '%s'", type.getTypeName(), s), e);
    }
}
