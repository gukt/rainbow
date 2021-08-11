/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.excel;

import com.codedog.rainbow.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ExcelUtils class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class ExcelUtils {

    private ExcelUtils() {}

    @Nullable
    public static String normalize(@Nullable String s) {
        return normalize(s, false);
    }

    @Nullable
    public static String normalize(@Nullable String s, boolean quote) {
        if (s == null) return null;
        StringJoiner resultJoiner = new StringJoiner(", ");
        String[] elements = s.split(",");
        for (String element : elements) {
            String[] parts = element.split("[*:]");
            String prefix = elements.length > 1 && parts.length > 1 ? "[" : "";
            String suffix = elements.length > 1 && parts.length > 1 ? "]" : "";
            StringJoiner joiner = new StringJoiner(", ", prefix, suffix);
            for (String part : parts) {
                part = part.trim();
                // 将 "-" 分隔的字符串解析成数组: [...]
                if (part.contains("-")) {
                    String[] arr = part.split("-");
                    if (arr.length < 2) {
                        throw new RuntimeException("格式错误: " + part + " in " + s);
                    } else {
                        joiner.add("[" + quoteIfRequired(arr[0].trim(), quote) + ", " + quoteIfRequired(arr[1].trim(), quote) + "]");
                        if (arr.length > 2) {
                            log.warn("连接符-只能连接两个元素，多余的已被忽略解析: {} in {}", part, s);
                        }
                    }
                } else {
                    joiner.add(quoteIfRequired(part, quote));
                }
            }
            resultJoiner.add(joiner.toString());
        }
        System.out.println("Normalized: \"" + s + "\" => " + resultJoiner);
        return resultJoiner.toString();
    }

    private static String quoteIfRequired(String s, boolean quote) {
        return quote ? StringUtils.doubleQuote(s) : s;
    }

    public static String normalizeArray(String s) {
        return normalizeArray(s, false);
    }

    public static String normalizeArray(String s, boolean quote) {
        return "[" + normalize(s, quote) + "]";
    }

    public static String normalizeMap(String s) {
        if(s == null) return null;
        final String origin = s;
        s = s.trim();
        if(s.startsWith("{") && s.endsWith("}")) {
            s = StringUtils.trim(s, "{", "}");
        }
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        // 以逗号分隔划分 entries
        for (String element : s.split(",")) {
            Pattern pattern = Pattern.compile("([^{]*?)\\s*[*:]\\s*(.*)");
            Matcher matcher = pattern.matcher(element);
            if (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                if (value.matches("\\s*\\{.*\\}\\s*")) {
                    value = normalizeMap(value);
                } else {
                    value = normalize(value);
                }
                key = StringUtils.doubleQuoteIfAbsent(StringUtils.trim(key));
                value = StringUtils.doubleQuoteIfAbsent(StringUtils.trim(value));
                joiner.add(key + ": " + value);
            }
        }
        return joiner.toString();
    }

    // public static String normalizeMap(String s) {
    //     if(s == null) return null;
    //     final String origin = s;
    //     s = s.trim();
    //     if(s.startsWith("{") && s.endsWith("}")) {
    //         s = StringUtils.trim(s, "{", "}");
    //     }
    //     StringJoiner joiner = new StringJoiner(", ", "{", "}");
    //     // 以逗号分隔划分 entries
    //     for (String element : s.split(",")) {
    //         Pattern pattern = Pattern.compile("([^{]*?)\\s*[*:]\\s*(.*)");
    //         Matcher matcher = pattern.matcher(element);
    //         if (matcher.find()) {
    //             String key = matcher.group(1);
    //             String value = matcher.group(2);
    //             if (value.matches("\\s*\\{.*\\}\\s*")) {
    //                 value = normalizeMap(value);
    //             } else {
    //                 value = normalize(value);
    //             }
    //             key = StringUtils.doubleQuoteIfAbsent(StringUtils.trim(key));
    //             value = StringUtils.doubleQuoteIfAbsent(StringUtils.trim(value));
    //             joiner.add(key + ": " + value);
    //         }
    //     }
    //     return joiner.toString();
    // }
}
