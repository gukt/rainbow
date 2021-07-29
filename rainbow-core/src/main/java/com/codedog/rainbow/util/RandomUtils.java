/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import java.util.Random;

/**
 * RandomUtils class
 *
 * @author https://github.com/gukt
 */
public class RandomUtils {

    /** Prevents to construct an instance. */
    private RandomUtils() {
        throw new AssertionError("No RandomUtils instances for you!");
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
}
