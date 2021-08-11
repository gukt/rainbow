package com.codedog.rainbow.world.excel;

import com.codedog.rainbow.util.MathUtils;
import com.codedog.rainbow.util.Probable;
import com.codedog.rainbow.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * WorldItem class // TODO 改为 UnmodifiedList
 *
 * @author https://github.com/gukt
 */
public class WorldItemList extends ArrayList<Probable<WorldItem>> {

    private boolean byWeight;
    private int precision;
    private boolean resolved = false;
    private String rawData;

    public WorldItemList() {
        this(0, false);
    }

    public WorldItemList(int precision) {
        this(precision, false);
    }

    private WorldItemList(boolean byWeight) {
        this(0, byWeight);
    }

    public WorldItemList(int precision, boolean byWeight) {
        super();
        this.byWeight = byWeight;
        this.precision = precision;
    }

    // 1*2-3:4.567,2*2-3:4.01
    static WorldItemList parse(String s, boolean byWeight) {
        WorldItemList instance = new WorldItemList(byWeight);
        instance.rawData = s;
        if (s == null || s.isEmpty()) return instance;
        // 根据数值计算精度
        int precision = getMaxPrecision(s);
        // 如果是按权重计算概率，则最低精度设置为 2
        if (byWeight) precision = Math.max(2, precision);
        String[] parts;
        // 原始定义的 odds
        Double originOdds;
        int odds;
        // odds 的放大比例，根据精度计算
        int scale = (int) Math.pow(10, precision);
        // 每个 segment 表示一个 element 的定义，以逗号分开
        String[] segments = s.split(",");
        for (String segment : segments) {
            parts = segment.split(":");
            originOdds = Double.valueOf(parts[1]);
            if (byWeight) {
                // 如果是按权重计算概率，则所有权重的设定都修正为整数
                odds = originOdds.intValue() * scale;
            } else {
                odds = (int) (originOdds * scale);
            }
            // 生成 Probable 对象，并添加到 instance 中
            instance.add(Probable.of(WorldItem.of(parts[0]), odds));
        }
        return instance;
    }

    private static int getMaxPrecision(String s) {
        String[] segments = s.split(",");
        Objects.requireNonNull(segments);
        int maxPrecision = 0;
        for (String segment : segments) {
            String[] parts = segment.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("缺少':'分隔符: " + segment + ", source: " + s);
            }
            int precision = MathUtils.getPrecision(parts[1]);
            if (precision > maxPrecision) {
                maxPrecision = precision;
            }
        }
        return maxPrecision;
    }

    public static WorldItemList of(String s) {
        return parse(s, false);
    }

    public String getRawData() {
        return rawData;
    }

    public boolean isResolved() {
        return resolved;
    }

    public List<WorldItem> multiGet(int n) {
        List<WorldItem> retList = new ArrayList<>();
        IntStream.range(0, n).forEach(i -> get().ifPresent(retList::add));
        return retList;
    }

    public Optional<WorldItem> get() {
        ensureResolved();
        return RandomUtils.nextElement(this, precision);
    }

    private void ensureResolved() {
        if (!resolved) {
            Probable.resolveRange(this, byWeight, 2);
            resolved = true;
        }
    }
}
