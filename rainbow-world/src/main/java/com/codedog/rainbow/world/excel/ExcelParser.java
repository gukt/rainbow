/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.excel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.codedog.rainbow.util.Assert;
import com.codedog.rainbow.util.ObjectUtils;
import com.codedog.rainbow.util.StringUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Primitives;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO 如果是 required ，则检测 null 要报错。或者前置检查。
 * // TODO 默认路径需要相对于 classpath:
 * // TODO Utils: add 获取 classpath 路径，不依赖于 spring 的 classpath
 * TODO 经常见到的配置错误要前置检查出来：
 * 1. 逗号用中文全角的
 * 2. 前后括号，花括号不匹配的
 * 3. 将数值填写成字符串的
 * 4. 填了很多个空格的
 * 表格中间空行的，日志要打印出本次解析概要，从rows: 1-30, columns: 0 - 25
 * sheet 后面加其他内容的。
 * ObjectMapper 要可以注入进来
 * 日期类型要可以注册新的
 * 要支持更多类型的格式，比如 LocalDate，LocalDateTime
 * 添加对 Range, Weighted, Possible 的支持
 * add method: toCsv
 * 对应集合和 map 类型，都使用 readonly 数据类型
 *
 * @author https://github.com/gukt
 */
@Builder
@Slf4j
public class ExcelParser {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        // TODO Fix it ASAP
        // OBJECT_MAPPER.registerModule(new GuavaModule());
    }

    public static ExcelParser getDefaultInstance() {
        return ExcelParserHolder.INSTANCE;
    }

    private final Map<Class<?>, Map<String, Integer>> propertyMappingByType = new HashMap<>();
    /**
     * 用于对象序列化与反序列化
     */
    private final Kryo kryo = new Kryo();
    @Builder.Default
    private final String basePath = "/excel/";
    private String actualBasePath;
    /**
     * 命名行索引（以 0 为基数），必须大于或等于 0。
     * 这里默认为 1，因为填表时，第 0 行往往填的是列名称，起注释的作用（而不是字段名称）。
     */
    @Builder.Default
    private final int namingRowIndex = 1;
    /**
     * 数据化起始索引（以 0 为基数），表示从哪一行开始是数据行。必须大于 {@link #namingRowIndex}。
     */
    @Builder.Default
    private final int dataRowStartIndex = 3;
    /**
     * 支持的日期格式
     */
    @Builder.Default
    private final SimpleDateFormat[] supportedDateFormats = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    };
    /**
     * 是否启动对被解析对象字段的是否可变检查
     */
    @Builder.Default
    private final boolean mutableCheck = true;
    /**
     * 是否启用对字段值为null时的检查，如果启用，若发现表格中原始数据为null或为空时会立即抛出错误，反之根据字段类型使用默认值
     */
    @Builder.Default
    private final boolean nullable = true;
    /**
     * 是否需要将解析结果持久化保存到文件中。默认为 {@code true }，这样会更高效。
     */
    @Builder.Default
    private final boolean persistEnabled = false;
    /**
     * 持久化文件名后缀
     */
    @Builder.Default
    private final String persistSuffix = ".dat";

    private final boolean failFast = false;

    @Nullable
    public <V> List<V> parse(Class<V> targetType) {
        preCheck(targetType);
        List<V> list = null;
        // 如果持久化开关是开启的，则尝试先从持久化文件中解析（这样会很高效）
        if (persistEnabled) {
            try {
                list = parseFromSerializedFile(targetType);
            } catch (ExcelParseException e) {
                log.debug("Excel - Failed to parse serialized from the file", e);
            }
        }
        return list == null ? parseFromExcel(targetType) : list;
    }

    /**
     * 从 Excel 文件中，逐行解析数据为指定类型的对象，返回对象集合。
     *
     * @param targetType 集合元素类型
     * @param <V>        集合元素类型
     * @return 对象集合
     */
    private <V> List<V> parseFromExcel(Class<V> targetType) {
        List<V> result = new ArrayList<>();
        String filename = getMappingFilename(targetType);
        log.info("Excel - Parsing form the target file: {}{}", actualBasePath, filename);
        List<List<String>> rows = readRaw(filename);
        // 解析命名行，将列名和列索引映射关系保存起来
        resolveFieldPosition(targetType, rows.get(namingRowIndex));
        // 逐行解析数据
        for (int row = dataRowStartIndex; row < rows.size(); row++) {
            V v = resolveBean(rows.get(row), targetType);
            result.add(v);
        }
        if (persistEnabled) {
            serializeAndSave(targetType, result);
        }
        return Collections.unmodifiableList(result);
    }

    private <V> V resolveBean(List<String> row, Class<V> beanType) {
        V instance = null;
        try {
            instance = beanType.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            fail("Cannot initialize the " + beanType.getName());
        }
        for (Field field : beanType.getDeclaredFields()) {
            // 获得该字段对应的配置值在配置文件的哪一列
            int columnIndex = getColumnIndex(beanType, field.getName());
            if (columnIndex != -1) {
                String v = row.get(columnIndex);
                // 给字段赋值
                setFieldValue(instance, field, v);
            } else {
                // 只要发现有字段赋值不成功，就抛出异常并退出
                fail("Cannot found mapping column for [" + beanType.getSimpleName() + "#" + field.getName() + "]");
            }
        }
        return instance;
    }

    private int getColumnIndex(Class<?> beanType, String columnName) {
        Map<String, Integer> nameIndexMap = propertyMappingByType.get(beanType);
        return nameIndexMap.getOrDefault(columnName, -1);
    }

    /**
     * 根据指定的原始的字符串值进行相应的转换后，给指定对象的指定字段赋值
     *
     * @param bean     被赋值字段所属的对象
     * @param field    被赋值字段
     * @param rawValue 原始的字符串值
     * @throws ExcelParseException if file to parse
     */
    private void setFieldValue(Object bean, Field field, String rawValue) {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        // 根据字段类型对原始数据格式进行转换，以便可以顺利的解析
        String normalized = normalizeByType(rawValue, fieldType);
        try {
            field.set(bean, convert(field, normalized));
        } catch (IllegalAccessException e) {
            fail(String.format("字段赋值异常: [class=%s, field=%s, value=%s]",
                    bean.getClass().getSimpleName(), field.getName(), normalized), e);
        }
        field.setAccessible(false);
    }

    /**
     * 预检查
     *
     * @param targetType 被检测的对象类型，不能为 null
     */
    private void preCheck(Class<?> targetType) {
        log.info("Excel - Checking...");
        Assert.notNull(targetType, "targetType");
        Assert.isAnnotationPresent(targetType, ExcelMapping.class);

        // TODO 将以下这些移到 build 之后进行检查，因为它们是全局性的
        Assert.isTrue(namingRowIndex >= 0, "namingRowIndex >= 0");
        Assert.isTrue(dataRowStartIndex >= 0, "dataRowStartIndex >= 0");
        Assert.isTrue(dataRowStartIndex > namingRowIndex,
                "Excel - The dataRowStartIndex must be greater than the namingRowIndex: [dataRowStartIndex=%s, namingRowIndex=%s]",
                dataRowStartIndex, namingRowIndex);
        // 检查指定的路径是否存在
        URL url = getClass().getResource(basePath);
        Assert.notNull(url, "Excel - Cannot found the '%s' directory in the classpath", basePath);
        this.actualBasePath = url.getPath();

        // 检查是否定义了 Wrapper 类型的字段，如果有，提示使用相应的 Primitive 类型代替
        Map<String, String> wrapperTypeFields = new HashMap<>(8);
        // 遍历所有的 fields
        for (Field field : targetType.getDeclaredFields()) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            // 需要忽略该字段的解析吗？
            if (excelProperty != null && excelProperty.ignored()) {
                continue;
            }
            // 字段类型是包装类型吗？
            if (Primitives.isWrapperType(field.getType())) {
                wrapperTypeFields.put(field.getName(), field.getType().getSimpleName());
            }
        }
        if (!wrapperTypeFields.isEmpty()) {
            log.info("Excel - Found {} wrapper type of fields in {}: {}",
                    wrapperTypeFields.size(), targetType.getSimpleName(), wrapperTypeFields);
        }
    }

    private void serializeAndSave(Class<?> clazz, Object object) {
        // TODO 移到 FileUtils 里
        File destFile = new File(actualBasePath + "/bin/");
        boolean exists = destFile.exists();
        if (!exists && !destFile.mkdir()) {
            throw new RuntimeException("创建目录失败: " + destFile);
        }
        String binFilename = destFile.getPath() + "/" + clazz.getSimpleName() + persistSuffix;
        File binaryFile = new File(binFilename);
        try (Output output = new Output(new FileOutputStream(binaryFile))) {
            kryo.writeClassAndObject(output, object);
        } catch (FileNotFoundException ignore) {
            // ignore, 因为必定会存在
        }
        log.debug("Excel - Successfully serialized and saved to {}", binFilename);
        long lastModified = getExcelLastModified(clazz);
        if (!binaryFile.setLastModified(lastModified)) {
            throw new RuntimeException("Excel - Failed to set the last modified attribute for " + binFilename);
        }
    }

    private long getExcelLastModified(Class<?> clazz) {
        String excelFilename = getMappingFilename(clazz);
        File excelFile = new File(getClass().getResource(basePath) + excelFilename);
        return excelFile.lastModified();
    }

    private File getSerializedFilename(Class<?> targetType) {
        String filename = actualBasePath + "bin/" + targetType.getSimpleName() + persistSuffix;
        return new File(filename);
    }

    private boolean isSerializedFileStaled(File file, Class<?> targetType) {
        return file.lastModified() < getExcelLastModified(targetType);
    }

    /**
     * 从上次已保存的序列化文件中解析（反序列化出对象）。
     * // 存在两种情况会导致回退到从文件解析：
     * // 1. 序列化文件不存在（首次调用或序列化文件被手动删除）
     * // 2. 自上次保存序列化文件后，excel内容被修改过，此时序列化文件内容就是陈旧的需要丢弃。
     *
     * @param targetType 被映射的目标对象类型，不能为 null
     * @param <V>        目标对象类型
     * @return 从文件中反序列化成功的对象集合，可能为 null
     * @throws ExcelParseException 如果发生解析错误
     * @see #parseFromExcel(Class)
     */
    @Nullable
    private <V> List<V> parseFromSerializedFile(Class<V> targetType) {
        Assert.notNull(targetType, "targetType");
        File file = getSerializedFilename(targetType);
        String filename = file.getName();
        log.info("Excel - Trying to deserialize from the binary file: {}", filename);
        if (!file.exists()) {
            log.debug("Excel - File not found: {}", filename);
            return null;
        }
        if (isSerializedFileStaled(file, targetType)) {
            log.debug("Excel - The serialized file is staled: {}", filename);
            return null;
        }
        // 读取文件并反序列
        try (Input input = new Input(new FileInputStream(filename))) {
            long t1 = System.currentTimeMillis();
            @SuppressWarnings("unchecked")
            List<V> list = (List<V>) kryo.readClassAndObject(input);
            long elapsedMillis = System.currentTimeMillis() - t1;
            log.info("Excel - Successfully deserialized {} {} from {}, It took {} millis.",
                    ObjectUtils.safeGetSize(list),
                    targetType.getSimpleName(),
                    filename,
                    elapsedMillis);
            return Collections.unmodifiableList(list);
        } catch (Exception e) {
            fail("Failed to deserialized from " + filename, e);
            return null;
        }
    }

    /**
     * 序列化字符串值为指定类型的对象。
     *
     * @param field 对象的字段，不能为 null
     * @param s     原始填写的字符串值，可以为 null，为 null 时，对象值用 null 表示; primitive 类型值用它们的默认值。
     * @return 序列化成功的对象
     * @throws ExcelParseException 如果发生解析错误
     */
    @Nullable
    private Object convert(Field field, @Nullable String s) {
        Assert.notNull(field, "field");
        Class<?> fieldType = field.getType();
        // 对于常见类型，直接通过相应的解析方法进行解析
        if (String.class.equals(fieldType)) {
            return ObjectUtils.nullToEmpty(s);
        }
        // Character
        else if (Character.class.equals(fieldType) || char.class.equals(fieldType)) {
            if (s == null || s.length() != 1) {
                fail("Invalid value of char/Character type: " + s);
            }
            return s.charAt(0);
        }
        // Date
        else if (Date.class.equals(fieldType)) {
            return parseDate(s);
        }
        // Boolean
        else if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {
            if (s == null) {
                return false;
            }
            String v1 = s.trim();
            String[] arrTrue = new String[]{"是", "1", "yes", "true", "ok"};
            return Arrays.stream(arrTrue).anyMatch(s1 -> s1.equalsIgnoreCase(v1));
        }
        // Byte
        else if (Byte.class.equals(fieldType) || byte.class.equals(fieldType)) {
            return s == null ? 0 : Byte.parseByte(s);
        }
        // Short
        else if (Short.class.equals(fieldType) || short.class.equals(fieldType)) {
            return s == null ? 0 : Short.parseShort(s);
        }
        // Integer
        else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
            return s == null ? 0 : Integer.parseInt(s);
        }
        // Long
        else if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
            return s == null ? 0L : Long.parseLong(s);
        }
        // Float
        else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
            return s == null ? 0.0 : Float.parseFloat(s);
        }
        // Double
        else if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
            return s == null ? 0.0 : Double.parseDouble(s);
        }
        // BigDecimal
        else if (BigDecimal.class.equals(fieldType)) {
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
     * @param s 被解析的日期字符串
     * @return 日期对象，如果参数 s 为 null，则返回 null
     * @throws ExcelParseException 如果解析失败
     */
    @Nullable
    private Date parseDate(@Nullable String s) {
        if (s == null) return null;
        for (SimpleDateFormat dateFormat : supportedDateFormats) {
            try {
                return dateFormat.parse(s);
            } catch (ParseException ignore) {
                // Ignored, continue...
            }
        }
        throw new ExcelParseException("Unable to convert to Date：" + s + ", Supported formats: "
                + Arrays.toString(supportedDateFormats));
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
        if (field.getGenericType() instanceof ParameterizedType) {
            return typeFactory.constructFromCanonical(field.getGenericType().toString());
        } else {
            Class<?> fieldType = field.getType();
            // 如果是集合类型
            // TODO 添加其他 guava 类型的判断
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

    private String normalizeByType(@Nullable String s, Class<?> targetType) {
        if (Collection.class.isAssignableFrom(targetType)) {
            s = normalizeArray(s);
        } else if (Map.class.isAssignableFrom(targetType)) {
            s = normalizeMap(s);
        } else {
            s = normalize(s);
        }
        return s;
    }

    /**
     * 1*1          -> [1,1]
     * 1*2-3:4      -> [1,[2,3],4]
     * 1*2-3        -> [1,[2,3]]
     * 1*1,2*2      -> [1,1],[2,2]
     * 1            -> 1 (保留原样)
     * 1,2          -> 1,2 (保留原样)
     */
    private String normalize(String s) {
        String[] elements = s.split(",");
        StringBuilder sb = new StringBuilder();
        for (String element : elements) {
            String[] parts = element.split("[*:]");
            if (parts.length == 1) {
                sb.append(element).append(",");
            } else {
                StringJoiner joiner = new StringJoiner(",", "[", "]");
                for (String part : parts) {
                    if (part.contains("-")) {
                        String[] arr = part.split("-");
                        joiner.add(Arrays.toString(arr));
                    } else {
                        joiner.add(part);
                    }
                }
                sb.append(joiner).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        System.out.println("normalize: " + s + " => " + sb);
        return sb.toString();
    }

    private String normalizeArray(String s) {
        return "[" + normalize(s) + "]";
    }

    // 有可能带有 {"1":{"11":12}} 左右花括号，如果有去掉它们后再检查

    // 1*1
    // "1"*1
    private String normalizeMap(String s) {
        if (s == null) return s;
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length() - 1);
        }
        String[] elements = s.split(",");
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        for (String element : elements) {
            String[] parts = element.split("[:*]");
            if (parts.length < 2) {
                // TODO refine
                throw new IncorrectFormatException("" + element);
            }
            joiner.add(StringUtils.doubleQuoteIfAbsent(parts[0]) + ":" + normalize(parts[1]));
        }
        return joiner.toString();
    }

    // @Nullable
    // private String transform(Class<?> fieldType, @Nullable String s) {
    //     if (Collection.class.isAssignableFrom(fieldType)) {
    //         return Transformer.COLLECTION_LIKE.apply(s);
    //     }
    //     if (Map.class.isAssignableFrom(fieldType)) {
    //         return Transformer.MAP_LIKE.apply(s);
    //     }
    //     ExcelProperty anno = fieldType.getAnnotation(ExcelProperty.class);
    //     if (anno != null) {
    //         // TODO fix it ASAP
    //         // return anno.transformer().apply(s);
    //     }
    //     return s;
    // }

    /**
     * 从列命名行读取所有列，组织成"列名->列索引位置"的映射。
     *
     * @param type        目标对象类型，不能为 null
     * @param columnNames Excel 文件中的命名行配置的列名集合，不能为 null
     * @throws ExcelParseException 如果 excel 文件中缺失列名
     */
    private void resolveFieldPosition(Class<?> type, List<String> columnNames) {
        Assert.notNull(type, "type");
        Assert.notNull(columnNames, "columnNames");

        Map<String, Integer> positionByFieldName = new HashMap<>(columnNames.size());
        Set<String> missingFields = new HashSet<>();
        for (Field field : type.getDeclaredFields()) {
            String targetColumnName = getMappingColumnName(field);
            if (targetColumnName != null) {
                int index = columnNames.indexOf(targetColumnName);
                if (index != -1) {
                    positionByFieldName.put(targetColumnName, index);
                } else {
                    missingFields.add(targetColumnName);
                }
            }
        }
        // 及时失败，并打印出 excel 文件中缺失的列
        if (!missingFields.isEmpty()) {
            fail(String.format("Excel - Cannot found the following columns in %s for %s: %s",
                    getMappingFilename(type), type.getSimpleName(), missingFields));
        }
        propertyMappingByType.put(type, positionByFieldName);
    }

    @Nullable
    private String getMappingColumnName(Field field) {
        Assert.notNull(field, "field");
        ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
        if (excelProperty != null && excelProperty.ignored()) {
            return null;
        }
        String fieldName = field.getName();
        if (excelProperty != null && !excelProperty.value().isEmpty()) {
            fieldName = excelProperty.value();
        }
        return fieldName;
    }

    private String getMappingFilename(Class<?> type) {
        Assert.notNull(type, "type");
        return ObjectUtils.requireAnnotation(type, ExcelMapping.class).file();
    }

    private String getMappingSheetName(Class<?> type) {
        Assert.notNull(type, "type");
        return ObjectUtils.requireAnnotation(type, ExcelMapping.class).sheet();
    }

    /**
     * 解析指定的 Excel 文件
     *
     * @param filename Excel 文件名。不能为 null
     * @return return parsed rows
     * @throws ExcelParseException if failed to close input stream
     */
    private List<List<String>> readRaw(String filename) {
        Assert.notNull(filename, "filename");
        List<List<String>> rows = new ArrayList<>();
        LinkedHashMap<?, ?> map = new LinkedHashMap<>();
        try {
            log.info("Excel - Read raw data from the specified sheet.");
            try (InputStream stream = getClass().getResourceAsStream(basePath + filename)) {
                // ExcelReader reader =     EasyExcelFactory.read(stream, new DefaultAnalysisEventListener() {
                //     @Override
                //     public void invoke(List<String> row, AnalysisContext context) {
                //         // rows.add(row);
                //         log.info("djflajsfjalsjflsjlfjsljfls: {}", row);
                //     }
                //
                //
                // })
                //         // .autoCloseStream(true)
                //         // .ignoreEmptyRow(true)
                //         .excelType(ExcelTypeEnum.XLSX)
                //         .build();
                // reader.readAll();
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

    private static class ExcelParserHolder {

        private static final ExcelParser INSTANCE = ExcelParser.builder().build();
    }

    // Internal help methods

    private void fail(String message, Throwable cause) {
        if (failFast) {
            throw new ExcelParseException(message, cause);
        } else {
            log.warn("{}: cause:", message, cause);
        }
    }

    private void fail(String message) {
        fail(message, null);
    }

    // public enum Transformer implements Function<String, String> {
    //     /**
    //      * 原封不动的返回
    //      */
    //     NOOP {
    //         @Override
    //         public String apply(String s) {
    //             return s;
    //         }
    //     },
    //
    //     /**
    //      * Map-like content transformer
    //      */
    //     MAP_LIKE {
    //         @Override
    //         public String apply(String s) {
    //             if (s == null) {
    //                 return "{}";
    //             }
    //             String validLeading = "[[";
    //             String validTailing = "]]";
    //             // 将格式:[[101,1],[102,2]] 转换为标准的Json对象格式
    //             if (s.startsWith(validLeading) && s.endsWith(validTailing)) {
    //                 // 去除首尾[]
    //                 s = trim(s, "[", 1);
    //                 s = trim(s, "]", 1);
    //                 StringTokenizer tokenizer = new StringTokenizer(s, ",");
    //                 StringBuilder sb = new StringBuilder();
    //                 while (tokenizer.hasMoreTokens()) {
    //                     String[] elements = asList(tokenizer.nextToken());
    //                     sb.append(elements[0]).append(":").append(elements[1]).append(",");
    //                 }
    //                 if (sb.length() > 0) {
    //                     // trim the tailing ','
    //                     sb.setLength(sb.length() - 1);
    //                 }
    //                 return sb.toString();
    //             }
    //             // TODO 用正则表达式匹配1*1,2*2,3*3这种格式
    //             s = s.replace("*", ":");
    //             StringBuilder sb = new StringBuilder();
    //             if (!s.startsWith(jsonObjectLeading)) {
    //                 sb.append("{");
    //             }
    //             sb.append(s);
    //             if (!s.endsWith(jsonObjectTailing)) {
    //                 sb.append("}");
    //             }
    //             return sb.toString();
    //         }
    //     },
    //
    //     /**
    //      * Collection-like content transformer
    //      */
    //     COLLECTION_LIKE {
    //         @Override
    //         public String apply(String s) {
    //             if (s == null || s.isEmpty()) {
    //                 return "[]";
    //             }
    //             StringBuilder sb = new StringBuilder();
    //             if (!s.startsWith(jsonArrayLeading)) {
    //                 sb.append("[");
    //             }
    //             sb.append(s);
    //             if (!s.endsWith(jsonArrayTailing)) {
    //                 sb.append("]");
    //             }
    //             return sb.toString();
    //         }
    //     };
    //
    //     String jsonArrayLeading = "[";
    //     String jsonArrayTailing = "]";
    //     String jsonObjectLeading = "{";
    //     String jsonObjectTailing = "}";
    //
    //     String[] asList(String s) {
    //         if (!s.startsWith(jsonArrayLeading) && !s.endsWith(jsonArrayTailing)) {
    //             throw new IncorrectFormatException("字符串格式不正确，want:[...], got:" + s);
    //         }
    //         s = trim(s, "[", 1);
    //         s = trim(s, "]", 1);
    //         String[] parts = s.split(",");
    //         int expectedLen = 2;
    //         if (parts.length != expectedLen) {
    //             throw new IncorrectFormatException("元素内容个数不正确, want:2, got:" + parts.length);
    //         }
    //         return parts;
    //     }
    //
    //     /**
    //      * 返回被裁剪过的字符串，返回的字符串首尾不包含what参数指定的字符串，
    //      * 如果what为null或为空，则等同于s.trim()
    //      *
    //      * @param s    被裁剪的字符串
    //      * @param what 要裁剪什么
    //      * @return 返回被裁剪过的字符串，返回的字符串首尾不包含what参数指定的字符串
    //      */
    //     String trim(String s, String what, int n) {
    //         Objects.requireNonNull(s, "Source string should not be null.");
    //         if (what == null || what.trim().isEmpty()) {
    //             return s.trim();
    //         }
    //         int count = 0;
    //         while (s.startsWith(what)) {
    //             s = s.substring(what.length());
    //             if (count++ >= n && n > 0) {
    //                 break;
    //             }
    //         }
    //         count = 0;
    //         while (s.endsWith(what)) {
    //             s = s.substring(0, s.length() - what.length());
    //             if (count++ >= n && n > 0) {
    //                 break;
    //             }
    //         }
    //         return s;
    //     }
    // }

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
