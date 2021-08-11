/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.world.excel.ExcelParser;
import com.codedog.rainbow.world.template.TestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static com.codedog.rainbow.util.JsonUtils.toBean;
import static com.codedog.rainbow.util.JsonUtils.toList;
import static com.codedog.rainbow.world.excel.ExcelUtils.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * TemplateTests class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class TemplateTests {

    @Test
    void test1() {
        // 获得当前工作目录
        // /Users/ktgu/workspace/projects/201-rainbow/rainbow-world
        System.out.println(System.getProperty("user.dir"));

        // 输出类文件所在目录（相对目录/包路径名）
        // file:/Users/ktgu/workspace/projects/201-rainbow/rainbow-world/out/test/classes/com/codedog/rainbow
        System.out.println(getClass().getResource(""));
        System.out.println(getClass().getResource("."));
        System.out.println(getClass().getResource("./"));

        // file:/Users/ktgu/workspace/projects/201-rainbow/rainbow-world/out/test/classes/
        System.out.println(getClass().getClassLoader().getResource(""));
        System.out.println(getClass().getClassLoader().getResource("."));
        System.out.println(getClass().getClassLoader().getResource("./"));
        System.out.println(ClassLoader.getSystemResource(""));
        System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));

        // null
        System.out.println(Thread.currentThread().getClass().getResource(""));

        // 输出类文件路径的相对目录
        // file:/Users/ktgu/workspace/projects/201-rainbow/rainbow-world/out/test/classes/
        System.out.println(getClass().getResource("/"));

        // 输出类文件路径的父目录
        // file:/Users/ktgu/workspace/projects/201-rainbow/rainbow-world/out/test/classes/com/codedog/
        System.out.println(getClass().getResource(".."));
        System.out.println(getClass().getResource("../"));

        // 输出类文件路径的以相对目录开始的指定目录
        // file:/Users/ktgu/workspace/projects/201-rainbow/rainbow-world/out/test/classes/com/codedog
        System.out.println(getClass().getResource("/com/codedog"));
        // 指定的目录不存在时，返回 null
        System.out.println(getClass().getResource("/com/not-found"));
    }

    @Test
    void test2() {
        ExcelParser parser = ExcelParser.getDefaultInstance();
        Object list = parser.parse(TestTemplate.class);
        System.out.println(list);
    }

    @Test
    void testRegex() {
        String s = "{k1: {k11:v11},k2:{k22:v22}}";

        // TODO 检测左右花括号是否匹配
        // TODO 检测每个花括号内是否都是 k:v,k:v 格式
    }

    @Test
    void test3() {
        assertArrayEquals(new byte[]{1, 2}, toBean("[1,2]", byte[].class));
        assertArrayEquals(new short[]{1, 2}, toBean("[1,2]", short[].class));
        assertArrayEquals(new int[]{1, 2}, toBean("[1,2]", int[].class));
        assertArrayEquals(new long[]{1, 2}, toBean("[1,2]", long[].class));
        assertArrayEquals(new double[]{1, 2}, toBean("[1,2]", double[].class));
        assertArrayEquals(new float[]{1, 2}, toBean("[1,2]", float[].class));
    }

    /**
     * 1*1          -> [1,1]
     * 1*2-3:4      -> [1,[2,3],4]
     * 1*2-3        -> 1,[2,3]]
     * 1*1,2*2      -> [1,1],[2,2]
     * 1            -> 1 (保留原样)
     * 1,2          -> 1,2 (保留原样)
     *
     * @param s
     * @return
     */


    // @Nullable
    // private String normalize(@Nullable String s) {
    //     if (s == null) return null;
    //     StringJoiner resultJoiner = new StringJoiner(", ");
    //     String[] elements = s.split(",");
    //     // 1,1          => 1,1
    //     // 1*1          => 1,1
    //     // 1*1, 2*2     => [1,1], [2,2]
    //     for (String element : elements) {
    //         String[] parts = element.split("[*:]");
    //         String prefix = parts.length > 1 ? "[" : "";
    //         String suffix = parts.length > 1 ? "]" : "";
    //         StringJoiner joiner = new StringJoiner(", ", prefix, suffix);
    //         for (String part : parts) {
    //             // 将 "-" 分隔的字符串解析成数组: [...]
    //             if (part.contains("-")) {
    //                 String[] arr = part.split("-");
    //                 if (arr.length < 2) {
    //                     throw new RuntimeException("格式错误: " + part + " in " + s);
    //                 } else {
    //                     joiner.add("[" + arr[0] + ", " + arr[1] + "]");
    //                     if (arr.length > 2) {
    //                         log.warn("连接符-只能连接两个元素，多余的已被忽略解析: {} in {}", part, s);
    //                     }
    //                 }
    //             } else {
    //                 joiner.add(part);
    //             }
    //         }
    //         resultJoiner.add(joiner.toString());
    //     }
    //     System.out.println("Normalized: " + s + " => " + resultJoiner);
    //     return resultJoiner.toString();
    // }
    @Test
    void testJson1() {
        String s = "1*1,2*2";
        String s1 = normalizeMap(s);
        Object result = toBean(s1, Map.class);
        System.out.println(result);
    }

    @Test
    void testNormalizeExcelConfiguredInformalValue() {


        Assertions.assertEquals("1, 2", normalize("  1  ,  2  "));
        Assertions.assertEquals("[1, 2]", normalizeArray("  1, 2   "));
        Assertions.assertEquals("1", normalize("1"));
        Assertions.assertEquals("[1]", normalizeArray(" 1"));
        //
        Assertions.assertEquals("1, 1", normalize(" 1  *  1 "));
        Assertions.assertEquals("[1, 1]", normalizeArray(" 1 *1"));
        //
        Assertions.assertEquals("[1, 1], [2, 2]", normalize("1*1,2*2"));
        Assertions.assertEquals("[[1, 1], [2, 2]]", normalizeArray("1*1,2*2"));
        //
        Assertions.assertEquals("1, [2, 3]", normalize("1*2-3"));
        Assertions.assertEquals("[1, [2, 3]]", normalizeArray("1*2-3"));

        Assertions.assertEquals("1, [2, 3], 4", normalize("1*2-3*4"));
        Assertions.assertEquals("[1, [2, 3], 4]", normalizeArray("1*2-3*4"));

        Assertions.assertEquals("\"a\", \"b\"", normalize("  a  ,  b  ", true));

        // [1, 2]
        System.out.println(toList(normalizeArray("  1  ,  2  "), Byte.class));
        Assertions.assertIterableEquals(Arrays.asList(1,2), toList(normalizeArray("  1  ,  2  "), Byte.class));
        System.out.println(toList(normalizeArray("  1  ,  2  "), byte.class));
        // [1, 2]
        System.out.println(toList(normalizeArray("  1  ,  2  "), Short.class));
        System.out.println(toList(normalizeArray("  1  ,  2  "), short.class));
        // [1, 2]
        System.out.println(toList(normalizeArray("  1  ,  2  "), Integer.class));
        System.out.println(toList(normalizeArray("  1  ,  2  "), int.class));
        // [1, 2]
        System.out.println(toList(normalizeArray("  1  ,  2  "), Long.class));
        System.out.println(toList(normalizeArray("  1  ,  2  "), long.class));
        // [1.0, 2.0]
        System.out.println(toList(normalizeArray("  1  ,  2  "), Double.class));
        System.out.println(toList(normalizeArray("  1  ,  2  "), double.class));
        // [1.0, 2.0]
        System.out.println(toList(normalizeArray("  1  ,  2  "), Float.class));
        System.out.println(toList(normalizeArray("  1  ,  2  "), float.class));

        assertArrayEquals(new char[]{'a', 'b'}, toBean(normalizeArray("  a  ,  b  ", true), char[].class));
        assertArrayEquals(new Character[]{'a', 'b'}, toBean(normalizeArray("  a  ,  b  ", true), Character[].class));
        assertArrayEquals(new String[]{"a", "b"}, toBean(normalizeArray("  a  ,  b  ", true), String[].class));
        assertArrayEquals(new byte[]{1, 2}, toBean(normalizeArray("  1  ,  2  "), byte[].class));
        assertArrayEquals(new Byte[]{1, 2}, toBean(normalizeArray("  1  ,  2  "), Byte[].class));
        assertArrayEquals(new short[]{1, 2}, toBean(normalizeArray("  1  ,  2  "), short[].class));
        assertArrayEquals(new int[]{1, 2}, toBean(normalizeArray("  1  ,  2  "), int[].class));
        assertArrayEquals(new long[]{1, 2}, toBean(normalizeArray("  1  ,  2  "), long[].class));
        assertArrayEquals(new double[]{1.0d, 2.0d}, toBean(normalizeArray("  1  ,  2  "), double[].class));
        assertArrayEquals(new float[]{1.0f, 2.0f}, toBean(normalizeArray("  1  ,  2  "), float[].class));

        System.out.println("here");

        // 这种不匹配
        // Assertions.assertEquals("1, [2, 3]", normalize("1*[2-3]"));

        // Normalize: Map style

    }

    @Test
    void testNormalizeMap() {
        // {"1": {"11": "12", }, "2": "{"22": "22"}"}
        // System.out.println(normalizeMap("k1:{k11:v11, k12:v12},k2:{k21:v21}"));

        String s = "k1:{k11:v11},k2:{k21:v21}";
        String s2 = "1:{11:12, 13:14},2:{21:22}";

        System.out.println(normalizeMap("  {k1 *  {  k11  * v11 }   , k2 : { k22  : v22  } } "));
        Assertions.assertEquals("{\"k1\": \"{\"k11\": \"v11\"}\", \"k2\": \"{\"k22\": \"v22\"}\"}",
                normalizeMap(""));
    }
}
