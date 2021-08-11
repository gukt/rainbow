/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.google.common.collect.Range;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Probables class
 *
 * @author https://github.com/gukt
 */
public class ProbableUtils {

    // TODO 考虑按权重的情况
    @Nullable
    public static <K> K randomSelect(Map<K, Integer> itemsByOdds, int precision) {
        int scale = (int) Math.pow(10, precision);
        int rndNum = RandomUtils.nextInt(0, 100 * scale);
        int lower, upper = 0;
        for (Map.Entry<K, Integer> entry : itemsByOdds.entrySet()) {
            lower = upper;
            upper += entry.getValue() * scale;
            if (rndNum >= lower && rndNum < upper) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Nullable
    public static <K> K randomSelect(Map<K, Integer> itemsByOdds) {
        return randomSelect(itemsByOdds, 0);
    }

    public <K> List<Probable<K>> fromMap(Map<K, Integer> itemsMap, boolean byWeight) {
        List<Probable<K>> elements =
                itemsMap.entrySet().stream()
                        .map(entry -> Probable.of(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());
        resolveOddsRange(elements, byWeight, 4);
        return elements;
    }

    private static <E> void resolveOddsRange(
            Collection<Probable<E>> elements, boolean byWeight, int precision) {
        if (byWeight) {
            int totalWeights = elements.stream().mapToInt(Probable::getOdds).sum();
            int lower = 0;
            for (Probable<?> element : elements) {
                int delta = element.getOdds() * (int) Math.pow(10, 2 + precision) / totalWeights;
                int upper = lower + delta;
                element.setRange(Range.openClosed(lower, upper));
                lower = upper;
            }
        } else {
            int lower = 0;
            for (Probable<?> element : elements) {
                int delta = element.getOdds() * (int) Math.pow(10, precision);
                int upper = lower + delta;
                element.setRange(Range.openClosed(lower, upper));
                lower = upper;
            }
        }
    }

    private ProbableUtils() {}

    public static void main(String[] args) {}
}
