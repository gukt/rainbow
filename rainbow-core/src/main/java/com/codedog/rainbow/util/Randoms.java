/*
 * Copyright 2018-2019 gukt, The Niuniu Project
 */

package com.codedog.rainbow.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 随机数相关工具类
 *
 * @author gukt
 * @version 1.0
 */
public class Randoms {

    /**
     * 产生指定范围区间内的随机值，注意：随机数会***等于区间边界***
     *
     * @param min 区间下限(inclusive)
     * @param max 区间上限(inclusive)
     * @return 返回指定区间内的随机值，返回值包含[min,max]
     */
    public static int random(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    /**
     * 产生指定区间内的随机值
     *
     * @return 返回指定区间内的随机值，返回值可能会包含(min,max)
     */
    public static int random(String range) {
        return random(range, "-");
    }

    public static int random(String range, String delimiter) {
        String[] arr = range.split(delimiter);
        int min = Integer.parseInt(arr[0]);
        int max = Integer.parseInt(arr[1]);
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    /**
     * 百分比概率
     *
     * @param percent 概率
     * @return 是否成功
     */
    public static boolean hited(int percent) {
        int i = random(1, 100);
        return i <= percent;
    }

    /**
     * 百分比概率
     *
     * @return 是否成功
     */
    public static boolean hited(float percent) {
        return random(1, 100) <= percent;
    }

    /**
     * 按权重值计算随机出一个
     */
    public static <T> T randomByWeights(Map<T, Integer> weightMap) {
        int sum = 0;
        double ceil = 0;
        for (Integer weight : weightMap.values()) {
            sum += weight;
        }

        Random random = new Random(System.nanoTime());
        int randNum = random.nextInt(10000);
        for (Map.Entry<T, Integer> entry : weightMap.entrySet()) {
            Integer weight = entry.getValue();
            ceil += ((double) weight / sum) * 10000;

            if (randNum <= ceil) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static int randomByWeights(String weights) {
        String[] parts = weights.split(",");
        int[] arr = new int[3];
        int n = 0;
        for (int i = 0; i < parts.length; i++) {
            n += Integer.valueOf(parts[i]) * 10000; // 放大100倍
            arr[i] = n;
        }

        int rnd = new Random(System.nanoTime()).nextInt(n + 1);
        for (int i = 0; i < arr.length; i++) {
            if (rnd <= arr[i]) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int value;
        for (int i = 0; i < 1000000; i++) {
            value = Randoms.random(1, 100);
            if (value == 100) {
                System.out.println("here");
            }
            System.out.println(value);
        }

        // 构造按权重随机的项目
        Map<String, Integer> weightMap = new HashMap<String, Integer>();
        weightMap.put("A", 9);
        weightMap.put("B", 1);
        weightMap.put("C", 1);

        for (int i = 0; i < 100; i++) {
            String result = Randoms.randomByWeights(weightMap); // 随机出一个
            System.out.println(result);
        }
    }
}
