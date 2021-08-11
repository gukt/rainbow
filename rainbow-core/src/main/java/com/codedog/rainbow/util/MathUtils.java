/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * MathUtils
 *
 * @author gukt
 */
public class MathUtils {

    private MathUtils() { }

    /**
     * 获得 “一个字符串表示的数字” 的精度，例如：1.234 的小数点后有 3 位，所以返回 3。
     *
     * @param number 字符串表示的数字
     * @return 精度
     */
    public static int getPrecision(String number) {
        Objects.requireNonNull(number);
        return number.length() - 1 - number.indexOf(".");
    }

    /**
     * 多个字符串表示的数字中，取最大精度
     *
     * @param numbers 多个字符串表示的数字
     * @return 最大精度
     */
    public static int getMaxPrecision(String... numbers) {
        int precision, maxPrecision = 0;
        for (String value : numbers) {
            precision = getPrecision(value);
            if (precision > maxPrecision) maxPrecision = precision;
        }
        return maxPrecision;
    }

    /**
     * 计算阶乘数，即 n! = n * (n-1) * ... * 2 * 1。
     *
     * @param n n
     * @return 阶乘结果
     */
    private static long factorial(int n) {
        return (n > 1) ? n * factorial(n - 1) : 1;
    }

    /**
     * 计算排列数，即A(n, m) = n!/(n-m)!
     *
     * @param n n
     * @param m m
     * @return 排列的个数
     */
    public static long arrangement(int n, int m) {
        return (n >= m) ? factorial(n) / factorial(n - m) : 0;
    }

    /**
     * 计算组合数，即C(n, m) = n!/((n-m)! * m!)
     *
     * @param n n
     * @param m m
     * @return 组合的个数
     */
    public static long combination(int n, int m) {
        return (n >= m) ? factorial(n) / factorial(n - m) / factorial(m) : 0;
    }

    /**
     * 排列选择（从列表中选择n个排列）
     *
     * @param dataList 待选列表
     * @param n        选择个数
     */
    public static void arrangementSelect(String[] dataList, int n) {
        System.out.printf("A(%d, %d) = %d%n", dataList.length, n,
                arrangement(dataList.length, n));
        arrangementSelect(dataList, new String[n], 0);
    }

    /**
     * 排列选择
     *
     * @param dataList    待选列表
     * @param resultList  前面（resultIndex-1）个的排列结果
     * @param resultIndex 选择索引，从0开始
     */
    private static void arrangementSelect(String[] dataList, String[] resultList, int resultIndex) {
        int resultLen = resultList.length;
        if (resultIndex >= resultLen) { // 全部选择完时，输出排列结果
            System.out.println(Arrays.asList(resultList));
            return;
        }
        // 递归选择下一个
        for (String data : dataList) {
            // 判断待选项是否存在于排列结果中
            boolean exists = false;
            for (int j = 0; j < resultIndex; j++) {
                if (data.equals(resultList[j])) {
                    exists = true;
                    break;
                }
            }
            // 排列结果不存在该项，才可选择
            if (!exists) {
                resultList[resultIndex] = data;
                arrangementSelect(dataList, resultList, resultIndex + 1);
            }
        }
    }

    /**
     * 组合选择（从列表中选择n个组合）。
     *
     * @param dataList 待选列表
     * @param n        选择个数
     * @return 返回所有组合的集合
     */
    public static <V> Collection<List<V>> combinationSelect(Object[] dataList, int n) {
        Set<List<V>> results = new HashSet<>();
        combinationSelect0(dataList, 0, new Object[n], 0, results);
        return results;
    }

    /**
     * 组合选择（从列表中选择n个组合）
     *
     * @param dataList 待选列表
     * @param n        选择个数
     * @return 返回所有组合的集合
     */
    public static <V> Collection<List<V>> combinationSelect(List<V> dataList, int n) {
        return combinationSelect(dataList.toArray(), n);
    }

    /**
     * 组合选择
     *
     * @param dataList    待选列表
     * @param dataIndex   待选开始索引
     * @param result      前面（resultIndex-1）个的组合结果
     * @param resultIndex 选择索引，从0开始
     */
    @SuppressWarnings("unchecked")
    private static <V> void combinationSelect0(Object[] dataList,
                                               int dataIndex,
                                               Object[] result,
                                               int resultIndex,
                                               Set<List<V>> results) {
        Object[] resultList = Arrays.copyOf(result, result.length);
        int resultLen = resultList.length;
        int resultCount = resultIndex + 1;
        if (resultCount > resultLen) {
            results.add(Lists.newArrayList((V[]) resultList));
            return;
        }
        // 递归选择下一个
        for (int i = dataIndex; i < dataList.length + resultCount - resultLen; i++) {
            resultList[resultIndex] = dataList[i];
            combinationSelect0(dataList, i + 1, resultList, resultIndex + 1, results);
        }
    }
}