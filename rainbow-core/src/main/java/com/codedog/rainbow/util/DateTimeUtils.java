/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;


import com.codedog.rainbow.NotImplementedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * DateTimes class
 *
 * @author https://github.com/gukt
 */
public class DateTimeUtils {

    /**
     * 返回给定的两个时间是否是同一天，以零点为分界
     *
     * @param d1 时间 1
     * @param d2 时间 2
     * @return 如果是同一天返回 true，反之 false
     */
    public static boolean dayEquals(Date d1, Date d2) {
        throw new NotImplementedException();
    }

    /**
     * 返回给定的两个时间是否是同一天，以基于 0 点加指定的偏移时间为每天的分隔
     *
     * @param d1     时间 1
     * @param d2     时间 2
     * @param offset 时间偏移
     * @param unit   时间偏移单位
     * @return 如果是同一天返回 true，反之 false
     */
    public static boolean dayEquals(Date d1, Date d2, long offset, TimeUnit unit) {
        throw new NotImplementedException();
    }

    /**
     * 返回给定的两个时间是否是同一天，以基于 0 点加指定的偏移时间为每天的分隔
     *
     * @param d1       时间 1
     * @param d2       时间 2
     * @param duration 时间偏移
     * @return 如果是同一天返回 true，反之 false
     */
    public static boolean dayEquals(Date d1, Date d2, Duration duration) {
        throw new NotImplementedException();
    }

    /**
     * 返回给定的两个时间间隔的天数，以零点为分界
     *
     * @param d1 时间 1
     * @param d2 时间 2
     * @return
     */
    public static int daysBetween(Date d1, Date d2) {
        throw new NotImplementedException();
    }

    public static int daysBetween(Date d1, Date d2, long offset, TimeUnit unit) {
        throw new NotImplementedException();
    }

    public static int daysBetween(Date d1, Date d2, Duration duration) {
        throw new NotImplementedException();
    }

    /**
     * 返回当前时间距离指定时间的间隔天数
     * @param d
     * @return
     */
    public static long daysUntil(Date d) {
        throw new NotImplementedException();
    }

    /**
     * 返回给定的两个时间是否不是同一天，以基于 0 点加指定的偏移时间为每天的分隔
     *
     * @param d1     时间 1
     * @param d2     时间 2
     * @param offset 时间偏移
     * @param unit   时间偏移单位
     * @return 如果不是同一天返回 true，反之 false
     */
    public static boolean isDifferentDay(Date d1, Date d2, long offset, TimeUnit unit) {
        return !dayEquals(d1, d2, offset, unit);
    }

    /**
     * 距离
     *
     * @param d1
     * @param d2
     * @param duration
     * @return
     */
    public static boolean isDifferentDay(Date d1, Date d2, Duration duration) {
        return !dayEquals(d1, d2, duration);
    }


    /**
     * 返回距离指定时间还有多少毫秒
     *
     * 请参考：{@link LocalDateTime#until(Temporal, TemporalUnit)}
     *
     * @return 计算距离某个时间点还有多少毫秒
     */
    public static long until(Temporal temporal, TemporalUnit unit) {
        throw new NotImplementedException();
    }
}
