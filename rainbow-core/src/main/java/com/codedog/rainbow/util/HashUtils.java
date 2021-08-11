/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * HashUtils class
 *
 * @author https://github.com/gukt
 */
public class HashUtils {

    @SuppressWarnings("UnstableApiUsage")
    public static String sha256(String s) {
        HashCode hashCode = Hashing.sha256().newHasher().putString(s, UTF_8).hash();
        return hashCode.toString();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String sha512(String s) {
        HashCode hashCode = Hashing.sha512().newHasher().putString(s, UTF_8).hash();
        return hashCode.toString();
    }
}
