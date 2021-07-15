/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

import com.codedog.rainbow.world.excel.ExcelParser.DataConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 泛型类型数据转换器
 *
 * @author https://github.com/gukt
 */
@Slf4j
public final class ParametricConverter<O> implements DataConverter<O> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Class<?> valueType;
    private final List<Class<?>> parameterTypes;
    private final JavaType targetType;

    public ParametricConverter(Class<?> valueType, List<Class<?>> parameterTypes) {
        this.valueType = valueType;
        this.parameterTypes = Lists.newArrayList(parameterTypes);
        this.targetType = OBJECT_MAPPER.getTypeFactory().constructParametricType(valueType,
                parameterTypes.toArray(new Class<?>[parameterTypes.size()]));
    }

    public boolean isSupported(Class<?> valueType, List<Class<?>> parameterTypes) {
        boolean matched = this.valueType.equals(valueType);
        matched = matched && parameterTypes.size() == this.parameterTypes.size();
        for (int i = 0; matched && i < parameterTypes.size(); i++) {
            if (!this.parameterTypes.get(0).equals(parameterTypes.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public O convert(String input) {
        if (input == null || input.trim().isEmpty() || input.equals("0.0")) {
            return null;
        }

        try {
            // 特殊用法：解析如：[[101,1],[102,2]] ==> Map<Integer,Integer>
            if (valueType.equals(Map.class) && input.startsWith("[")) {
//                if (input.startsWith("[")) {
                List<List<Integer>> list = OBJECT_MAPPER.readValue(input, new TypeReference<List<List<Integer>>>() {
                });
                Map<Integer, Integer> map = new HashMap<>();
                for (List<Integer> element : list) {
                    map.put(element.get(0), element.get(1));
                }
                return (O) map;
//                }
            }

            // 尝试解析如:2100010*1,2*100这样的格式为Map
            if ((valueType.equals(Map.class) || valueType.equals(LinkedHashMap.class)) && input.indexOf("*") > 0) {
                Map<Integer, Integer> map;

                if (valueType.equals(Map.class)) {
                    map = new HashMap<>();
                } else {
                    map = new LinkedHashMap<>();
                }

                try {
                    StringTokenizer tokenizer = new StringTokenizer(input, ",");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token != null) {
                            String[] parts = token.split("\\*");
                            map.put(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
                        }
                    }
                    return (O) map;
                } catch (Exception e) {
                    log.error("尝试解析{}为Map时发生异常{}", input, e);
                    return null;
                }
            }

            // 数据校正
            if (valueType.equals(List.class) && !input.startsWith("[")) {
                input = "[" + input + "]";
            }

            // 排除前面的特殊格式，其他数据都按规则格式解析Map
            return OBJECT_MAPPER.readValue(input, targetType);
        } catch (Exception e) {
            throw new RuntimeException("Parsing Exception: " + e.getClass().getName() + ":" + e.getMessage(), e);
        }
    }
}
