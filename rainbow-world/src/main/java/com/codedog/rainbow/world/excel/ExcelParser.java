/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world.excel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Primitives;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import javax.annotation.Nullable;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-23 14:10
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Builder
@SuppressWarnings("restriction") // TODO 移除这里
@Slf4j
public class ExcelParser {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new GuavaModule());
    }

    private final Map<Class<?>, Map<String, Integer>> propertyMappingByType = new HashMap<>();
    private final Kryo kryo = new Kryo();
    private final String baseDir;
    private int namingRowIndex;
    private int dataRowIndex;
    /**
     * 是否启动对被解析对象字段的primitive类型检查
     */
    private final boolean primitiveCheck;
    /**
     * 是否启动对被解析对象字段的是否可变检查
     */
    private final boolean mutableCheck;
    /**
     * 是否启用对字段值为null时的检查，如果启用，若发现表格中原始数据为null或为空时会立即抛出错误，反之根据字段类型使用默认值
     */
    private final boolean nullable;
    /**
     * 是否将解析结果持久化到磁盘，以便下次在被解析对象类型没有改变的情况下从持久化的二进制文件中反序列化对象
     */
    private final boolean persist;
    private static final String PATH_SEPERATOR = "/";
    private final String persistFileSuffix = ".dat";
    private final SimpleDateFormat[] DEFAULT_DATE_FORMATS = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    };

    @Nullable
    public <V> List<V> parse(Class<V> clazz) {
        if (primitiveCheck) {
            // 将所有包装类型找出来并打印
            checkPrimitiveAndPrint(clazz);
        }
        // 如果持久化开关是开启的，则尝试先从持久化文件中解析（这样会很高效）
        // 但存在两种情况会导致回退到从文件解析：
        // 1. 序列化文件不存在（首次调用或序列化文件被手动删除）
        // 2. 自上次保存序列化文件后，excel内容被修改过，此时序列化文件作失效处理，需要从文件重新解析
        List<V> retList = null;
        if (persist) {
            boolean exist = true;
            try {
                retList = readAndDeserialize(clazz);
            } catch (FileNotFoundException e) {
                exist = false;
                log.debug("序列化文件未找到: {}", e.getMessage());
            } finally {
                // fall back
                if (retList == null) {
                    log.debug("从序列化文件中解析失败,即将回退到从文件解析，原因: {}", exist ? "序列化文件已失效" : "文件未找到");
                    // parse from file
                    retList = parseFromExcel(clazz);
                    // persist
                    serializeAndSave(clazz, retList);
                }
            }
        } else {
            retList = parseFromExcel(clazz);
        }
        return retList;
    }

    private <V> List<V> parseFromExcel(Class<V> clazz) {
        List<V> retList = new ArrayList<>();
        int row = -1;
        List<List<String>> rows = parseRaw(getMappedFilename(clazz));
        for (List<String> rowData : rows) {
            row++;
            // 解析命名行，将列名和列索引映射关系保存起来
            if (row == namingRowIndex) {
                propertyMappingByType.put(clazz, buildColumnMapping(clazz, rowData));
                continue;
            }
            // 解析数据行
            if (row >= dataRowIndex) {
                try {
                    retList.add(resolveBean(rowData, clazz));
                } catch (IllegalAccessException e) {
                    fail("给字段赋值时发生异常", e);
                } catch (InstantiationException e) {
                    fail("初始化对象" + clazz.getSimpleName() + "时发生异常", e);
                }
            }
        }
        return retList;
    }

    private <V> V resolveBean(List<String> row, Class<V> beanType)
            throws InstantiationException, IllegalAccessException {
        V bean = beanType.newInstance();
        for (Field field : beanType.getDeclaredFields()) {
            Integer columnIndex = getColumnIndex(beanType, field.getName());
            if (columnIndex != null) {
                String v = row.get(columnIndex);
                doAssignField(bean, field, v);
            } else {
                fail("Column not found: " + field.getName());
            }
        }
        return bean;
    }

    @Nullable
    private Integer getColumnIndex(Class<?> beanType, String columnName) {
        Map<String, Integer> nameIndexMap = propertyMappingByType.get(beanType);
        return nameIndexMap.get(columnName);
    }

    /**
     * 根据指定的原始的字符串值进行相应的转换后，给指定对象的指定字段赋值
     *
     * @param bean 被赋值字段所属的对象
     * @param field 被赋值字段
     * @param v 原始的字符串值
     * @throws ExcelParseException if file to parse
     */
    private void doAssignField(Object bean, Field field, String v) {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        // 根据字段类型对原始数据格式进行转换，以便可以顺利的解析
        v = transform(fieldType, v);
        try {
            // 转换并给字段赋值
            field.set(bean, convert(field, v));
        } catch (IllegalAccessException e) {
            fail(String.format("给类型%s的字段%s赋值时发生异常: value: %s", bean.getClass().getSimpleName(), field.getName(), v), e);
        }
        field.setAccessible(false);
    }

    /**
     * 检查指定类型中所有WrapperType类型的字段，收集并打印
     *
     * @param clazz object type
     */
    private void checkPrimitiveAndPrint(Class<?> clazz) {
        Map<String, String> wrapperTypeFields = new HashMap<>(8);
        ExcelColumn anno;
        for (Field field : clazz.getDeclaredFields()) {
            anno = field.getAnnotation(ExcelColumn.class);
            if (anno != null && anno.ignore()) {
                continue;
            }
            if (Primitives.isWrapperType(field.getType())) {
                wrapperTypeFields.put(field.getName(), field.getType().getSimpleName());
            }
        }
        if (!wrapperTypeFields.isEmpty()) {
            log.warn("检测到类型{}存在以下包装类型字段，建议使用primitive类型代替: {}",
                    clazz.getSimpleName(), wrapperTypeFields);
        }
    }

    private void checkMutable(Class<?> clazz) {
        Map<String, Class<?>> mutableFields = new HashMap<>(8);
        for (Field field : clazz.getDeclaredFields()) {
            // TODO fix it ASAP
        }
        if (!mutableFields.isEmpty()) {
            log.warn("检测到类型{}存在以下非Immutable集合类型字段，建议使用Immutable集合类型代替Mutable集合类型: {}",
                    clazz.getSimpleName(), mutableFields);
        }
    }

    private void serializeAndSave(Class<?> clazz, Object object) {
        File binDir = new File(baseDir + "bin/");
        boolean exists = binDir.exists();
        if (!exists && !binDir.mkdir()) {
            throw new RuntimeException("创建目录失败: " + binDir);
        }
        String binFilename = binDir.getPath() + "/" + clazz.getSimpleName() + persistFileSuffix;
        File binaryFile = new File(binFilename);
        try (Output output = new Output(new FileOutputStream(binaryFile))) {
            kryo.writeClassAndObject(output, object);
        } catch (FileNotFoundException ignore) {
            // ignore, 因为必定会存在
        }

        log.debug("保存序列化文件成功: {}", binFilename);

        long lastModified = getExcelLastModified(clazz);
        if (!binaryFile.setLastModified(lastModified)) {
            throw new RuntimeException("设置lastModified失败: file:" + binFilename);
        }
    }

    private void fail(String message, Throwable cause) {
        throw new ExcelParseException(message, cause);
    }

    private void fail(String message) {
        fail(message, null);
    }

    private long getExcelLastModified(Class<?> clazz) {
        String excelFilename = getMappedFilename(clazz);
        File excelFile = new File(baseDir + excelFilename);
        return excelFile.lastModified();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <V> List<V> readAndDeserialize(Class<?> clazz) throws FileNotFoundException {
        List<V> retList = null;
        String binFilename = baseDir + "bin/" + clazz.getSimpleName() + persistFileSuffix;
        File file = new File(binFilename);
        if (!file.exists()) {
            throw new FileNotFoundException(binFilename);
        }
        // 如果excel文件在此序列化文件上次保存之后被更改过，则直接返回null
        if (file.lastModified() < getExcelLastModified(clazz)) {
            return null;
        }
        // 读取文件并反序列
        try (Input input = new Input(new FileInputStream(binFilename))) {
            long t1 = System.currentTimeMillis();
            retList = (List<V>) kryo.readClassAndObject(input);
            long elapsed = System.currentTimeMillis() - t1;
            log.debug("从序列化文件中解析成功: {}: rows:{}, elapsed: {} millis, from: {}", clazz.getSimpleName(),
                    (retList == null ? 0 : retList.size()), elapsed, binFilename);
        } catch (Exception e) {
            log.warn("从序列化文件中解析失败: file:{}, cause:{}", binFilename, e.getMessage());
        }
        return retList;
    }

    /**
     * 将字符串转换为Field定义的类型值
     *
     * @param field field of object
     * @param s raw string value
     * @return return converted value according to field type
     * @throws ExcelParseException if fail in any case.
     */
    @Nullable
    private Object convert(Field field, @Nullable String s) {
        Class<?> fieldType = field.getType();
        // 对于常见类型，直接通过相应的解析方法进行解析
        if (String.class.equals(fieldType)) {
            return Strings.nullToEmpty(s);
        } else if (Character.class.equals(fieldType) || char.class.equals(fieldType)) {
            if (s == null || s.length() != 1) {
                fail("Invalid value of char/Character type: " + s);
            }
            return s.charAt(0);
        } else if (Date.class.equals(fieldType)) {
            if (s == null) {
                fail("Invalid value of Date type: " + s);
            }
            return parseDate(s);
        } else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
            if (s == null) {
                return false;
            }
            String v1 = s.trim();
            String[] arrTrue = new String[]{"是", "1", "yes", "true", "ok"};
            return Arrays.stream(arrTrue).anyMatch(s1 -> s1.equalsIgnoreCase(v1));
        } else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
            return s == null ? 0 : Byte.parseByte(s);
        } else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
            return s == null ? 0 : Short.parseShort(s);
        } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            return s == null ? 0 : Integer.parseInt(s);
        } else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            return s == null ? 0L : Long.parseLong(s);
        } else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
            return s == null ? 0.0 : Float.parseFloat(s);
        } else if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
            return s == null ? 0.0 : Double.parseDouble(s);
        } else if (BigDecimal.class.equals(fieldType)) {
            return new BigDecimal(s == null ? "0" : s);
        }
        // 对于其他对象类型或泛型类型，使用Jackson类库进行解析
        // 使用Jackson的好处是，可以在外部提供各种对象的自定义Serializer或Deserializer
        try {
            return OBJECT_MAPPER.readValue(s, getFieldJavaType(field));
        } catch (IOException e) {
            fail("转换错误", e);
            return null;
        }
    }

    /**
     * 解析指定的字符串为日期类型
     *
     * @param s string by default date format
     * @return return date
     * @throws IncorrectFormatException if format not supported
     */
    private Date parseDate(String s) {
        Date date = null;
        for (SimpleDateFormat dateFormat : DEFAULT_DATE_FORMATS) {
            try {
                date = dateFormat.parse(s);
                break;
            } catch (ParseException ignore) {
            }
        }
        if (date == null) {
            throw new IncorrectFormatException("解析字符串为日期类型时出错：" + s);
        }
        return date;
    }

    /**
     * 根据字段定义的类型，转换为JavaType，支持泛型嵌套
     *
     * @param field field of object
     * @return return JavaType for jackson
     */
    private JavaType getFieldJavaType(Field field) {
        TypeFactory typeFactory = OBJECT_MAPPER.getTypeFactory();
        // 如果字段类型是泛型类型
        // TODO ParameterizedTypeImpl是内部专用 API, 可能会在未来发行版中删除
        // TODO 暂时想让代码编译过去，临时改了让其通过，这里还可能有问题
        if (field.getGenericType() instanceof ParameterizedType) {
            return typeFactory.constructFromCanonical(field.getGenericType().toString());
        } else {
            Class<?> fieldType = field.getType();
            // 如果是集合类型
            // TODO 添加其他guava类型的判断
            if (Set.class.isAssignableFrom(fieldType) || List.class.isAssignableFrom(fieldType)) {
                return typeFactory.constructRawCollectionLikeType(fieldType);
            }
            // 如果是Map类型或guava的Multimap类型
            if (Map.class.isAssignableFrom(fieldType) || Multimap.class.isAssignableFrom(fieldType)) {
                return typeFactory.constructRawMapLikeType(field.getClass());
            }
            return typeFactory.constructSimpleType(field.getType(), new JavaType[0]);
        }
    }

    @Nullable
    private String transform(Class<?> fieldType, @Nullable String s) {
        if (Collection.class.isAssignableFrom(fieldType)) {
            return Transformer.COLLECTION_LIKE.apply(s);
        }
        if (Map.class.isAssignableFrom(fieldType)) {
            return Transformer.MAP_LIKE.apply(s);
        }
        ExcelColumn anno = fieldType.getAnnotation(ExcelColumn.class);
        if (anno != null) {
            // TODO fix it ASAP
            // return anno.transformer().apply(s);
        }
        return s;
    }

    /**
     * 查找指定列的索引位置
     *
     * @param targetName 目标列名
     * @param columnNames 所有列名
     * @return 返回指定列的索引位置
     */
    private int getColumnIndexByName(String targetName, List<String> columnNames) {
        int index = 0;
        for (String name : columnNames) {
            if (targetName.equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * 从列命名行读取所有列，组织成"列名->列索引位置"的映射
     *
     * @param clazz 类型
     * @param columnNames 命名行
     * @return 返回"列名->列索引位置"的映射
     * @throws ExcelParseException 如果excel文件中有缺失的列名
     */
    private Map<String, Integer> buildColumnMapping(Class<?> clazz, List<String> columnNames) {
        Map<String, Integer> columnMapping = new HashMap<>(columnNames.size());
        Set<String> missFields = new HashSet<>();
        ExcelColumn anno;
        for (Field field : clazz.getDeclaredFields()) {
            // 找出字段上的ExcelColumn注解
            anno = field.getAnnotation(ExcelColumn.class);
            if (anno != null && anno.ignore()) {
                continue;
            }
            String fileName = field.getName();
            if (anno != null && !anno.alias().isEmpty()) {
                fileName = anno.alias();
            }
            int index = getColumnIndexByName(fileName, columnNames);
            if (index == -1) {
                missFields.add(fileName);
            } else {
                columnMapping.put(fileName, index);
            }
        }
        if (!missFields.isEmpty()) {
            fail(String.format("The file %s is missing some columns for %s class: %s",
                    getMappedFilename(clazz), clazz.getSimpleName(), missFields));
        }
        return columnMapping;
    }

    /**
     * 获取指定类要映射到哪个excel文件
     *
     * @param clazz type of object
     * @return return excel file name
     * @throws ExcelParseException if annotation ExcelMapping absent
     */
    private String getMappedFilename(Class<?> clazz) {
        return getExcelMapping(clazz).file();
    }

    /**
     * 获取指定类要映射到的excel文件中的哪个sheet
     *
     * @param clazz type of object
     * @return return excel sheet name
     * @throws ExcelParseException if annotation ExcelMapping absent
     */
    @SuppressWarnings("unused")
    private String getMappedSheetName(Class<?> clazz) {
        return getExcelMapping(clazz).sheet();
    }

    /**
     * 在指定的类型上找到ExcelMapping注解并放回
     *
     * @return return ExcelMapping annotation if present
     * @throws ExcelParseException if annotation ExcelMapping absent
     */
    private ExcelMapping getExcelMapping(Class<?> clazz) {
        ExcelMapping anno = clazz.getAnnotation(ExcelMapping.class);
        if (anno == null) {
            fail("Cannot find @ExcelMapping on " + clazz.getSimpleName() + " class");
        }
        return anno;
    }

    /**
     * Parsing specified excel file.
     *
     * @param filename excel filename
     * @return return parsed rows
     * @throws ExcelParseException if failed to close input stream
     */
    private List<List<String>> parseRaw(String filename) {
        List<List<String>> rows = new ArrayList<>();
        try {
            // try (InputStream stream = getClass().getResourceAsStream(baseDir + filename)) {
            try (InputStream stream = new FileInputStream(baseDir + filename)) {
                // stream = new BufferedInputStream(stream);
                // if (stream == null) {
                //     fail("文件未找到: " + baseDir + filename);
                // }
                // TODO 这里的BufferedInputStream需要close
                new ExcelReader(new BufferedInputStream(stream), ExcelTypeEnum.XLSX,
                        new DefaultAnalysisEventListener() {
                            @Override
                            public void invoke(List<String> row, AnalysisContext context) {
                                rows.add(row);
                            }
                        }).read();
            }
        } catch (IOException e) {
            fail("关闭Excel文件流失败", e);
        }
        return rows;
    }

    public enum Transformer implements Function<String, String> {
        /**
         * 原封不动的返回
         */
        NOOP {
            @Override
            public String apply(String s) {
                return s;
            }
        },

        /**
         * Map-like content transformer
         */
        MAP_LIKE {
            @Override
            public String apply(String s) {
                if (s == null) {
                    return "{}";
                }
                String validLeading = "[[";
                String validTailing = "]]";
                // 将格式:[[101,1],[102,2]] 转换为标准的Json对象格式
                if (s.startsWith(validLeading) && s.endsWith(validTailing)) {
                    // 去除首尾[]
                    s = trim(s, "[", 1);
                    s = trim(s, "]", 1);
                    StringTokenizer tokenizer = new StringTokenizer(s, ",");
                    StringBuilder sb = new StringBuilder();
                    while (tokenizer.hasMoreTokens()) {
                        String[] elements = asList(tokenizer.nextToken());
                        sb.append(elements[0]).append(":").append(elements[1]).append(",");
                    }
                    if (sb.length() > 0) {
                        // trim the tailing ','
                        sb.setLength(sb.length() - 1);
                    }
                    return sb.toString();
                }
                // TODO 用正则表达式匹配1*1,2*2,3*3这种格式
                s = s.replace("*", ":");
                StringBuilder sb = new StringBuilder();
                if (!s.startsWith(jsonObjectLeading)) {
                    sb.append("{");
                }
                sb.append(s);
                if (!s.endsWith(jsonObjectTailing)) {
                    sb.append("}");
                }
                return sb.toString();
            }
        },

        /**
         * Collection-like content transformer
         */
        COLLECTION_LIKE {
            @Override
            public String apply(String s) {
                if (s == null || s.isEmpty()) {
                    return "[]";
                }
                StringBuilder sb = new StringBuilder();
                if (!s.startsWith(jsonArrayLeading)) {
                    sb.append("[");
                }
                sb.append(s);
                if (!s.endsWith(jsonArrayTailing)) {
                    sb.append("]");
                }
                return sb.toString();
            }
        };

        String jsonArrayLeading = "[";
        String jsonArrayTailing = "]";
        String jsonObjectLeading = "{";
        String jsonObjectTailing = "}";

        String[] asList(String s) {
            if (!s.startsWith(jsonArrayLeading) && !s.endsWith(jsonArrayTailing)) {
                throw new IncorrectFormatException("字符串格式不正确，want:[...], got:" + s);
            }
            s = trim(s, "[", 1);
            s = trim(s, "]", 1);
            String[] parts = s.split(",");
            int expectedLen = 2;
            if (parts.length != expectedLen) {
                throw new IncorrectFormatException("元素内容个数不正确, want:2, got:" + parts.length);
            }
            return parts;
        }

        /**
         * 返回被裁剪过的字符串，返回的字符串首尾不包含what参数指定的字符串，
         * 如果what为null或为空，则等同于s.trim()
         *
         * @param s 被裁剪的字符串
         * @param what 要裁剪什么
         * @return 返回被裁剪过的字符串，返回的字符串首尾不包含what参数指定的字符串
         */
        String trim(String s, String what, int n) {
            Objects.requireNonNull(s, "Source string should not be null.");
            if (what == null || what.trim().isEmpty()) {
                return s.trim();
            }
            int count = 0;
            while (s.startsWith(what)) {
                s = s.substring(what.length());
                if (count++ >= n && n > 0) {
                    break;
                }
            }
            count = 0;
            while (s.endsWith(what)) {
                s = s.substring(0, s.length() - what.length());
                if (count++ >= n && n > 0) {
                    break;
                }
            }
            return s;
        }
    }

    interface DataConverter<O> {

        O convert(String input);
    }

    static class DefaultAnalysisEventListener extends AnalysisEventListener<List<String>> {

        @Override
        public void invoke(List<String> object, AnalysisContext context) {
            // NOOP
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // NOOP
        }
    }
}
