/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * StringUtils class
 *
 * @author https://github.com/gukt
 */
@SuppressWarnings("unused")
@Slf4j
public final class StringUtils {

    public static final String EMPTY = "";
    public static final String EMPTY_JSON_ARRAY = "[]";
    public static final String EMPTY_JSON_OBJECT = "{}";
    public static final String SPACE = " ";
    public static final String TAB = "\t";
    public static final String CR = "\r";
    public static final String LF = "\n";
    public static final String DOT = ".";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String UNDERLINE = "_";
    public static final String HYPHEN = "-";
    public static final String QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";
    public static final String BACK_SLASH = "\\";
    public static final String DOUBLE_SLASH = "/";
    public static final String LEFT_PARENTHESIS = "(";
    public static final String RIGHT_PARENTHESIS = ")";
    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";
    public static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACES = "}";
    public static final String CRLF = "\r\n";
    public static final String NULL = "null";
    public static final String DOUBLE_BACK_SLASH = "\\\\";
    /**
     * 换行符。依赖于具体的操作系统：windows: \r\n; Linux: \n
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /**
     * Prevents to construct an instance.
     */
    private StringUtils() {
        throw new AssertionError("No StringUtils instances for you!");
    }

    /**
     * 检查指定的字符是否为空（{@code null} 或 {@link #EMPTY 空字符串}）
     *
     * <pre>
     * StringUtils.isEmpty(null)    = true
     * StringUtils.isEmpty("")      = true
     * StringUtils.isEmpty(" ")     = false
     * StringUtils.isEmpty("\t")    = false
     * StringUtils.isEmpty("\r")    = false
     * StringUtils.isEmpty("\n")    = false
     * StringUtils.isEmpty("foo")   = false
     * </pre>
     *
     * @param s 被检测的字符串，可以为 null
     * @return 如果被检测的字符为 null 或 "" 返回 true；反之 false
     */
    public static boolean isEmpty(@Nullable CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 检查指定的字符串是不是空白字符串。
     * 空白字符串包括: {@code null}、{@link #EMPTY 空字符串} 或者 {@link String#trim() trim} 后的结果为 {@link #EMPTY 空字符串}。
     * 如果一个字符串是空白字符串，则 {@link #isEmpty(CharSequence)} 也必定返回 {@code true}。
     *
     * <pre>
     * StringUtils.isBlank(null)        = true
     * StringUtils.isBlank(" \t\r\n ")  = true
     * StringUtils.isBlank("")          = true
     * StringUtils.isBlank("foo")       = false
     * </pre>
     *
     * @param s 被检测的字符串，可以为 null
     * @return 如果被检测的字符串为空白字符串返回 {@code true}; 反之 {@code false}
     */
    public static boolean isBlank(@Nullable String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 检查指定的字符串是否不是空白字符串，和 {@link #isBlank(String)} 相反。
     *
     * @param s 被检测的字符串，可以为 null
     * @return 如果被检测的字符串不是 null 且有内容，则返回 {@code true}；反之 {@code false}。
     */
    public static boolean isNotBlank(@Nullable String s) {
        return !isBlank(s);
    }

    /**
     * 如果给定的字符串为 null 则返回指定的默认值，否则返回字符串本身。
     *
     * @param s            被测试的字符串，可能为 null
     * @param defaultValue 指定的默认值，不能为 null
     * @return 如果被测试的字符串不为 null 则返回自身，反之，返回指定的默认值
     */
    public static String nullToDefault(@Nullable String s, String defaultValue) {
        ObjectUtils.requireNonNull(defaultValue, "defaultValue");
        return s == null ? defaultValue : s;
    }

    /**
     * 如果给定的字符串为 {@code null} 返回 {@link #EMPTY 空字符串}；反之返回自身。
     *
     * @param s 被测试的字符串，可能为 null
     * @return 如果给定的字符串为 {@code null} 返回 {@link #EMPTY 空字符串}；反之返回自身
     */
    public static String nullToEmpty(@Nullable String s) {
        return s == null ? EMPTY : s;
    }

    /**
     * 如果给定的字符为 {@link #EMPTY 空字符串}，则返回 {@code null}；反之返回自身。
     *
     * @param s 被测试的字符串，可能为 null
     * @return 如果给定的字符为 {@link #EMPTY 空字符串}，则返回 {@code null}；反之返回自身。
     */
    @Nullable
    public static String emptyToNull(@Nullable String s) {
        return isEmpty(s) ? null : s;
    }

    /**
     * 如果给定的字符串为空白字符串，则返回自身；反之返回 {@code null}。
     *
     * @param s 被测试的字符串，可能为 null
     * @return 如果给定的字符串为空白字符串，则返回自身；反之返回 {@code null}
     */
    @Nullable
    public static String blankToNull(@Nullable String s) {
        return isBlank(s) ? null : s;
    }

    /**
     * Removes control characters (char &lt;= 32) from both ends of this String returning {@code null}
     * if the String is empty ("") after the trim or if it is {@code null}.
     *
     * <pre>
     * StringsUtils.trimToNull(null)     = null
     * StringsUtils.trimToNull("")       = null
     * StringsUtils.trimToNull(" ")      = null
     * StringsUtils.trimToNull("foo")    = "foo"
     * StringsUtils.trimToNull(" foo ")  = "foo"
     * </pre>
     *
     * <p>NOTE: The only difference between trimToNull and {@link #blankToNull(String)} is that
     * trimToNull will trim the leading and trailing whitespace of result.
     *
     * <pre>
     * StringsUtils.trimToNull(" foo ")    = "foo"
     * StringsUtils.blankToNull(" foo ")   = " foo "
     * </pre>
     *
     * It is equivalent to <code>trim(blankToNull(s))</code>
     *
     * @param s the string to be trimmed, may be null
     * @return the trimmed String, {@code null} if only chars &lt;= 32, empty or null String input
     */
    @Nullable
    public static String trimToNull(@Nullable String s) {
        return trim(blankToNull(s));
    }

    /**
     * 移除给定字符串*开头*的 {@link Character#isWhitespace(char) 空白符}。
     *
     * <pre>
     * StringsUtils.trimLeft(null)           = null
     * StringsUtils.trimLeft("\r\n\t foo ")  = "foo "
     * </pre>
     *
     * @param s 被修剪的字符串，可以为 null
     * @return 一个新的字符串，它的头部空白符被裁减掉了；如果给定的字符串为 null，则返回 null
     */
    @Nullable
    public static String trimLeft(@Nullable String s) {
        if (isEmpty(s)) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * 移除给定字符串*尾部*的 {@link Character#isWhitespace(char) 空白符}。
     *
     * <pre>
     * StringsUtils.trimRight(null)           = null
     * StringsUtils.trimRight(" foo\r\n\t ")  = " foo"
     * </pre>
     *
     * @param s 被修剪的字符串，可以为 null
     * @return 一个新的字符串，它的尾部空白符被裁减掉了；如果给定的字符串为 null，则返回 null
     */
    @Nullable
    public static String trimRight(@Nullable String s) {
        if (isEmpty(s)) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 移除给定字符串首尾的 {@link Character#isWhitespace(char) 空白符}。它等效于 {@link String#trim()}，但该方法使用更方便，它是带 null-check 的。
     *
     * <pre>
     * StringsUtils.trimAround(null)                 = null
     * StringsUtils.trimAround(" \r\n\t foo\r\n\t ")  = "foo"
     * </pre>
     *
     * @param s 被裁剪的对象，可以为 null
     * @return 新的首尾空白符都被裁剪了的字符串；如果给定字符串本身为 null，则返回 null
     */
    @Nullable
    public static String trim(@Nullable String s) {
        return isEmpty(s) ? s : s.trim();
    }

    /**
     * Removes the specified leading string for the given string if the leading string exists.
     *
     * <pre>
     * StringsUtils.trimLeft(null, "http:")                      = null
     * StringsUtils.trimLeft("http://www.baidu.com", "http://")  = "www.baidu.com"
     * </pre>
     *
     * @param s       the string, may be null
     * @param leading the leading string to test, must not be null
     * @return a string with the given prefix removed; or itself if the given string does not start
     * with the specified prefix
     */
    @Nullable
    public static String trimLeft(@Nullable String s, String leading) {
        ObjectUtils.requireNonNull(leading, "leading");
        if (isEmpty(s)) return s;
        return s.startsWith(leading) ? s.substring(leading.length()) : s;
    }

    /**
     * Removes the specified trailing string for the given string if the trailing string exists.
     *
     * <pre>
     * StringsUtils.trimRight(null, ".png")         = null
     * StringsUtils.trimRight("logo.png", ".png")   = "logo"
     * </pre>
     *
     * @param s        the string, may be null
     * @param trailing the trailing string, must not be null
     * @return a string with the given trailing string removed; or itself if the given string does not
     * end with the specified trailing string
     */
    @Nullable
    public static String trimRight(@Nullable String s, String trailing) {
        ObjectUtils.requireNonNull(trailing, "trailing");
        if (isEmpty(s)) return s;
        return s.endsWith(trailing) ? s.substring(0, s.length() - trailing.length()) : s;
    }

    public static String trim(@Nullable String s, String leading, String trailing) {
        if (isEmpty(s)) return s;
        return trimLeft(trimRight(s, trailing), leading);
    }

    // TODO 需要测试
    public static String strip(@Nullable String s, String leadingPattern, String trailingPattern) {
        if (isEmpty(s)) return s;
        return s.replaceAll("^(" + leadingPattern + ")+|(" + trailingPattern + ")+$", "");
    }

    /**
     * Returns a string which starts with the given padding characters and followed by the given
     * string, if the given string is null, it will be converted to empty.
     *
     * <p>The opposite of {@link #trimLeft(String, String)}
     *
     * <pre>
     * StringsUtils.padLeft(null, "http:")               = "http:"
     * StringsUtils.padLeft("www.baidu.com", "http://")  = "http://www.baidu.com"
     * </pre>
     *
     * @param s        the string which should appear at the end of the result, may be null
     * @param padChars the characters to insert at the beginning of the result, requires non-null
     * @return the padded string
     */
    public static String padLeft(@Nullable String s, String padChars) {
        ObjectUtils.requireNonNull(padChars, "padChars");
        return padChars + nullToEmpty(s);
    }

    /**
     * Returns a string which ends with the given padding characters and leading with the given
     * string, if the given string is null, it will be converted to empty.
     *
     * <pre>
     * StringsUtils.padRight(null, ".png")   = ".png"
     * StringsUtils.padRight("logo", ".png") = "log.png"
     * </pre>
     *
     * @param s        the string which should appear at the beginning of the result, may be null
     * @param padChars the characters to insert at the end of the result, requires non-null
     * @return the padded string
     */
    public static String padRight(@Nullable String s, String padChars) {
        ObjectUtils.requireNonNull(padChars, "padChars");
        return nullToEmpty(s) + padChars;
    }

    /**
     * Returns a string which surrounded with the given padding characters, if the given string is
     * null, it will be treated as empty.
     *
     * <pre>
     * StringsUtils.padAround("hello", "'")  = "'hello'"
     * StringsUtils.padAround("null", "'")   = "'null'"
     * StringsUtils.padAround(null, "'")     = "''"
     * </pre>
     *
     * @param s        the string which should appear at the beginning of the result, may be null
     * @param padChars the characters to insert at the end of the result, requires non-null
     * @return the padded string
     */
    public static String padAround(@Nullable String s, String padChars) {
        return padRight(padLeft(s, padChars), padChars);
    }

    /**
     * Returns a quoted string, for example:
     *
     * <pre>
     * StringsUtils.quote("hello") = 'hello'
     * StringsUtils.quote(null)    = 'null'
     * </pre>
     *
     * @param s the string to quoted, may be null
     * @return the quoted string
     */
    public static String quote(@Nullable String s) {
        return padAround(nullToDefault(s, NULL), QUOTE);
    }

    public static String quoteIfAbsent(@Nullable String s) {
        return s == null || (!s.startsWith(QUOTE) && !s.endsWith(QUOTE)) ? quote(s) : s;
    }

    /**
     * Returns a double quoted string.
     *
     * @param s a string, may be null
     * @return the quoted string
     */
    public static String doubleQuote(String s) {
        return padAround(nullToDefault(s, NULL), DOUBLE_QUOTE);
    }

    public static String doubleQuoteIfAbsent(@Nullable String s) {
        return s == null || (!s.startsWith(DOUBLE_QUOTE) && !s.endsWith(DOUBLE_QUOTE))
                ? doubleQuote(s)
                : s;
    }

    // TODO add doubleQuoteIfAbsent here

    /**
     * Returns the specified length of characters from beginning of the given string.
     * 从给定字符串的开头返回指定长度的字符。
     *
     * <p>NOTE: 它等效于 {@link #slice(String, int, int) slice(s, 0, len)}
     *
     * <pre>
     * StringsUtils.left(null, 10)       = null
     * StringsUtils.left("hello", 0)     = ""
     * StringsUtils.left("hello", 10)    = "hello"
     * StringsUtils.left("hello", 4)     = "hell"
     * StringsUtils.left("hello", -1)    = "hello"
     * StringsUtils.left("hello", -10)   = ""
     * </pre>
     *
     * @param s 字符串，可能为 null
     * @param n 返回字符串的最大长度，可以为负值，负值会被处理成正确的长度。
     * @return 给定长度的字符串；如果给定字符串为 null，则返回 null。
     */
    @Nullable
    public static String left(@Nullable String s, int n) {
        return slice(s, 0, n);
    }

    /**
     * Returns a string that is a slice of the given string, The slice string begins at start index
     * (included) and extends to the character at the end of the given string.
     *
     * @param s     the string, may be null
     * @param start start index (included), can be negative
     * @return the slice of the given string between start index and
     */
    @Nullable
    public static String slice(@Nullable String s, int start) {
        if (s == null) return null;
        return slice(s, start, s.length());
    }

    /**
     * Returns a string that is a slice of the given string, The slice string begins at start index
     * (included) and extends to end index (excluded).
     *
     * <p>NOTE: This method is the advanced edition of the {@link String#substring(int, int)}, it
     * supports negative start and end index, and avoids throwing the {@link
     * IndexOutOfBoundsException} when end index is larger than length of given string, or begin index
     * is larger than end index.
     *
     * <pre>
     * String s = "0123";
     * assertEquals("", StringsUtils.slice(s, 0, 0));
     * assertEquals("", StringsUtils.slice(s, 100, 100));
     * assertEquals("", StringsUtils.slice(s, -1, -1));
     * assertEquals("", StringsUtils.slice(s, -100, -100));
     * assertEquals("", StringsUtils.slice(s, -100, 0));
     * assertEquals("", StringsUtils.slice(s, -100, -4));
     * assertEquals("0", StringsUtils.slice(s, -100, -3));
     * assertEquals("01", StringsUtils.slice(s, -100, -2));
     * assertEquals("0", StringsUtils.slice(s, 0, 1));
     * assertEquals("0123", StringsUtils.slice(s, 0, 100));
     * assertEquals("0123", StringsUtils.slice(s, 0, s.length()));
     * assertEquals("2", StringsUtils.slice(s, -2, -1));
     * assertEquals("", StringsUtils.slice(s, -2, -2));
     * </pre>
     *
     * @param s     the string to slice, may be null
     * @param start start index (included), can be negative
     * @param end   end index (excluded), can be negative
     * @return the slice of given string between start index and end index
     */
    @Nullable
    public static String slice(@Nullable String s, int start, int end) {
        if (s == null) return null;
        int len = s.length();
        if (start < 0) start = Math.max(0, start + len);
        if (end < 0) end = Math.max(0, end + len);
        start = Math.min(start, len);
        end = Math.min(end, len);
        if (start >= end) return EMPTY;
        return s.substring(start, end);
    }

    @Nullable
    public static String mask(@Nullable String s, int start, int end) {
        return mask(s, Chars.ASTERISK, start, end);
    }

    /**
     * Hides some characters for the given string, The hided characters is located from start index
     * (included) to end index (excluded).
     *
     * @param s        the string, may be null
     * @param maskChar mash character
     * @param start    start index (included)
     * @param end      end index (excluded)
     * @return an new string with some character hided
     */
    @Nullable
    public static String mask(@Nullable String s, char maskChar, int start, int end) {
        if (isEmpty(s)) return s;
        // TODO 将 start - end 中间的字符用 maskChar 隐藏起来
        return s;
    }

    @Nullable
    public static String maskN(@Nullable String s, int start, int len) {
        return maskN(s, Chars.ASTERISK, start, len);
    }

    @Nullable
    public static String maskN(@Nullable String s, char maskChar, int start, int len) {
        if (isEmpty(s)) return s;
        // TODO 将 start 开始的len 个字符隐藏起来 中间的字符用 maskChar 隐藏起来
        return s;
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.warn("URLEncoder.encode", e);
            return s;
        }
    }

    public static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.warn("URLDecoder.decode", e);
            return s;
        }
    }

    // TODO 这里要考虑将数组转换为字符串，要不要将数组压平？
    //  public static String concat(Object... args) {
    //    StringJoiner joiner = new StringJoiner(",", "[", "]");
    //    for (Object arg : args) {
    //      if (arg == null) continue;
    //      if (arg.getClass().isArray()) {
    //        joiner.add(Arrays.toString((int[]) arg));
    //      } else {
    //        joiner.add(arg.toString());
    //      }
    //    }
    //    return joiner.toString();
    //  }

    public static String format(String template, Object... args) {
        // TODO 支持 {} 这种模式
        return template;
    }

    /**
     * TODO 添加新的方法 format(template, args) Console.log(o1, o2, o3...), 对象自动调用 toString, 数组自动调用
     * Arrays.toString Strings.print 代替 Console.log Console.printf Strings.println Strings.printerr
     * Phone: isMobile(), isTel(), hide("13611632335", "*", 3, 5) start,end hideN("13611632335", "*",
     * 3, 7) => start, number
     */
}
