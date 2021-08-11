package com.codedog.rainbow.world.excel;

import com.codedog.rainbow.util.RandomUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import lombok.Getter;

/**
 * WorldItem class TODO 精度问题
 *
 * @author https://github.com/gukt
 */
@JsonSerialize(using = WorldItemSerializer.class)
@JsonDeserialize(using = WorldItemDeserializer.class)
public class WorldItem {

    @Getter private int id;
    @Getter private String rawNum;
    @Getter private int minNum;
    @Getter private int maxNum;

    private WorldItem() {}

    /**
     * Construct an instance.
     *
     * @param id  item id
     * @param num 数量，可以是固定值，也可以使用[m,n]这种区间格式，以下统一格式为使用区间格式
     * @return an instance
     */
    public static WorldItem of(int id, String num) {
        WorldItem instance = new WorldItem();
        instance.id = id;
        instance.rawNum = num;
        if (!num.contains("-")) {
            instance.minNum = Integer.parseInt(num);
            instance.maxNum = instance.minNum;
        } else {
            String[] parts = num.split("-");
            instance.minNum = Integer.parseInt(parts[0]);
            instance.maxNum = Integer.parseInt(parts[1]);
            if (instance.minNum > instance.maxNum)
                throw new IllegalArgumentException("Invalid num range: " + num + ", 区间 m-n 必须满足 m <= n");
        }
        return instance;
    }

    /**
     * Construct an instance by string
     *
     * <p>格式: {id}*{num/range},
     *
     * <ul>
     *   例如:
     *   <li>1*2-4 表示id 为 1，数量为 2（包含）- 4（包含） 个
     *   <li>1*2 表示 id 为 1 数量为固定的 2
     * </ul>
     *
     * @param s 需要解析的字符串
     * @return a WorldItem object, or null if s is null or empty
     */
    public static WorldItem of(String s) {
        if (s == null || s.isEmpty()) throw new IllegalArgumentException("null");
        if (!s.contains("*")) throw new IllegalArgumentException("缺少分隔符'*': " + s);
        String[] parts = s.split("\\*");
        return WorldItem.of(Integer.parseInt(parts[0]), parts[1]);
    }

    public int getNum() {
        // 由于 nextInt 方法生成的随机数是不包含 upper 边界值的，所以这里需要 + 1
        return RandomUtils.nextInt(minNum, maxNum + 1);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("num", rawNum).toString();
    }
}
