/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.Map;
import java.util.TreeMap;

/**
 * Encryption utilities
 *
 * @author https://github.com/gukt
 */
public class EncryptionUtils {

    public static Object encrypt(Object message) {
        // TODO 压缩
        return message;
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

    @SuppressWarnings("UnstableApiUsage")
    public static String sha256(String s) {
        HashFunction fn = Hashing.sha256();
        HashCode hashCode = fn.newHasher().putString(s, Charsets.UTF_8).hash();
        return hashCode.toString();
    }
}
