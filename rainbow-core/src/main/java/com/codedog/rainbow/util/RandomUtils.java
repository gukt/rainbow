/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.google.common.collect.Range;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static com.google.common.collect.BoundType.CLOSED;

/**
 * 随机数、概率计算相关的工具类。
 *
 * @author https://github.com/gukt
 */
public class RandomUtils {

    private RandomUtils() {
        throw new AssertionError("No RandomUtils instances for you!");
    }

    private static final String BadLowerBound = "The lower bound must be positive";
    private static final String BadUpperBound = "The upper bound must be positive";
    private static final String BadBound = "The bound must be positive";

    /**
     * 在区间 [0, bound) 产生一个随机整数。
     *
     * @param bound 区间上限 (不包括)，必须为正数
     * @return 随机数
     * @throws IllegalArgumentException 如果指定的区间参数不是正数
     */
    public static int nextInt(int bound) {
        Assert.isTrue(bound > 0, () -> BadBound);
        return new Random(System.nanoTime()).nextInt(bound);
    }

    /**
     * 在区间 [lower, upper) 产生一个随机数。
     *
     * @param lower 区间下限 (包括), 必须为正数
     * @param upper 区间上限 (不包括)，必须为正数
     * @return 随机数
     * @throws IllegalArgumentException 如果 lower、upper 为负数，或 lower >= upper
     */
    public static int nextInt(int lower, int upper) {
        if (lower < 0) throw new IllegalArgumentException(BadLowerBound);
        if (upper < 0) throw new IllegalArgumentException(BadUpperBound);
        if (lower >= upper)
            throw new IllegalStateException(
                    "The upper must greater than lower: [lower=" + lower + ", upper=" + upper + "]");
        return lower + nextInt(upper - lower);
    }

    /**
     * 在指定的区间内产生一个随机数。
     * 区间的 lower (包含）和 upper（不包含）必须以 ',' 或 '-' 分隔，且 upper 必须要大于 lower。
     *
     * @param bounds 区间定义，必须以","或"-"隔开
     * @return 随机数
     * @throws IllegalArgumentException 如果参数不是以 ',' 或 '-' 分隔
     * @throws NumberFormatException    如果 lower 或 upper 解析失败
     */
    public static int nextInt(String bounds) {
        ObjectUtils.requireNonNull(bounds, "bounds");
        String[] parts;
        if (bounds.contains(",")) {
            parts = bounds.split(",");
        } else if (bounds.contains("-")) {
            parts = bounds.split("-");
        } else {
            throw new IllegalArgumentException("The bounds must be separated by the ',' or '-' character");
        }
        return nextInt(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    /**
     * 根据指定的{@link Range 区间} 产生一个随机数， range 不能为 null，且要有上下边界；左边界必须小于或等于右边界。
     *
     * @param range Range 对象
     * @return 随机数
     */
    public static int nextInt(Range<Integer> range) {
        Objects.requireNonNull(range);
        if (!range.hasLowerBound()) throw new IllegalArgumentException("range requires a lower bound");
        if (!range.hasUpperBound()) throw new IllegalArgumentException("range requires a upper bound");
        int lower = range.lowerEndpoint() + (range.lowerBoundType() == CLOSED ? 0 : 1);
        int upper = range.upperEndpoint() + (range.upperBoundType() == CLOSED ? 1 : 0);
        return nextInt(lower, upper);
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

     public static <E> Optional<E> nextElement(Collection<Probable<E>> elements, int precision) {
       int randomNum = nextInt((int) Math.pow(10, 2 + precision));
       for (Probable<E> element : elements) {
         if (randomNum >= element.getRange().lowerEndpoint()
             || randomNum < element.getRange().upperEndpoint()) {
           return Optional.of(element.getElement());
         }
       }
       return Optional.empty();
     }
}
