package com.codedog.rainbow.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

// TODO 需要移除或整合这里所有的方法
public final class CommonUtils {

    private static final Integer MAX_ACCESS_PER_DAY = 10;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, Integer> countsByIp = new HashMap<>();
    /** Prevents to construct an instance. */
    private CommonUtils() {
        throw new AssertionError("No CommonUtils instances for you!");
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String httpGet(String s) throws IOException {
        // TODO 使用IP代理池
        URL url = new URL(s);
        return Resources.asCharSource(url, Charsets.UTF_8).read();
    }

    public static String toJson(@Nullable Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("将对象序列化成JSON字符串时发生解析异常", e);
        }
    }

    public static Map<String, Object> fromKeyValuePairs(@Nonnull Object... pairs) {
        Objects.requireNonNull(pairs);
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("参数 pairs 的个数必须是偶数, got:" + pairs.length);
        }
        Object key, value;
        Map<String, Object> retMap = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            key = pairs[i];
            value = pairs[i + 1];
            String k = key instanceof String ? (String) key : key.toString();
            retMap.put(k, value);
        }
        return retMap;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File ensureParentPath(File file) {
        Objects.requireNonNull(file, "文件不能为null");
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        return file;
    }

    public static String getFileSuffix(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
            return s;
        }
    }

    public static String generateRandomChars(int n) {
        Random rnd = new Random(System.nanoTime());
        char[] chars = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
                'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z', '!', '@', '#', '$', '%', '&', '*', '_'
        };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(chars[rnd.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    public static String makeSign(Map<String, String[]> params, String appSecret) {
        if (!(params instanceof TreeMap)) {
            params = new TreeMap<>(params);
        }
        StringBuilder sb = new StringBuilder();
        params.forEach(
                (key, values) -> {
                    if (!key.equals("sign")) {
                        String s = Joiner.on(",").join(values);
                        // 忽略参数值为空的情况
                        if (!",".equalsIgnoreCase(s)) {
                            sb.append(s).append("|");
                        }
                    }
                });
        sb.append(appSecret);
//        log.debug("Make sign: params: {}, values serial: {}", params, sb.toString());
        return sb.toString();
    }

    public static boolean ensurePath(File path) {
        if (!path.exists()) {
            return path.mkdirs();
        }
        return true;
    }

    public static boolean isAccessExceed(String ip) {
        Integer count = countsByIp.getOrDefault(ip, 0);
        return count >= MAX_ACCESS_PER_DAY;
    }

    public static void incrAccessCount(String ip) {
        Integer count = countsByIp.getOrDefault(ip, 0);
        countsByIp.put(ip, count + 1);
    }

    public static String composeId(long urlId, int openType) {
        return Long.toUnsignedString(urlId, 35) + "_" + Integer.toUnsignedString(openType, 33);
    }

    // TODO 移到 ObjectUtils 中，重命名为 getRequired(map, key)
//    @SuppressWarnings("unchecked")
//    public static <V> V safeGetValue(Map<String, Object> map, String key) {
//        Object value = map.get(key);
//        checkNotNull(value, ERR_BAD_PARAMETER.error("The field '" + key + "' is not present"));
//        return (V) value;
//    }

    @SuppressWarnings("UnstableApiUsage")
    public static String sha256(String s) {
        HashFunction fn = Hashing.sha256();
        HashCode hashCode = fn.newHasher().putString(s, Charsets.UTF_8).hash();
        return hashCode.toString();
    }

    public static <K, V> Map<K, V> include(Map<K, V> source, String... fields) {
        Set<String> fieldSet = Sets.newHashSet(fields);
        Map<K, V> retMap = new HashMap<>();
        source.forEach(
                ((k, v) -> {
                    if (fieldSet.contains(k.toString())) {
                        retMap.put(k, v);
                    }
                }));
        return retMap;
    }

    public static String ensureUrlPrefix(String url, boolean sslEnabled) {
        if (!url.startsWith("http")) {
            return (sslEnabled ? "https://" : "http://") + url;
        }
        return url;
    }

    public static boolean isValidDomain(String domain) {
        return false;
    }

    public static String xor(String s, String salt) {
        byte[] bytes = s.getBytes(Charsets.UTF_8);
        byte[] saltBytes = salt.getBytes(Charsets.UTF_8);
        byte[] resultBytes = new byte[bytes.length];
        byte b, b1;
        int saltLen = saltBytes.length;
        for (int i = 0; i < bytes.length; i++) {
            b = bytes[i];
            b1 = saltBytes[i / saltLen];
            resultBytes[i] = (byte) (b ^ b1);
        }
        return new String(resultBytes);
    }
}
