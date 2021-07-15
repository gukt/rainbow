/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.template;

import com.codedog.rainbow.world.excel.ExcelMapping;
import lombok.Getter;
import lombok.ToString;

/**
 * TODO 指定sheet
 *
 * @author https://github.com/gukt
 */
@ExcelMapping(file = "sample.xlsx", sheet = "TODO")
@Getter
@ToString
public class SampleTemplate {

    private boolean bool1;
    private byte byte1;
    // // private Byte byte2;
    // // private short short1;
    // // private Short short2;
    // private int int1;
    // // private Integer int2;
    // private long long1;
    // // private Long long2;
    // private float float1;
    // // private Float float2;
    // private double double1;
    // // private Double double2;
    // private char char1;
    // // private Character char2;
    // private BigDecimal bigDecimal1;
    private String str1;
    // private Date date1;
    //
    // private List<Integer> intList1;
    // private LinkedList<Integer> intList2;
    // private List<Long> longList1;
    // // @ExcelColumn(ignore = true)
    // private List<String> strList1;
    // private List<List<Integer>> llInt1;
    // // @ExcelProperty(transformer = MyTransformer.class, ignore = true)
    // private List<List<Long>> llLong1;
    // private Set<Integer> set1;
    // @ExcelColumn(alias = "set1")
    // private LinkedHashSet<Integer> set2;
    //
    // Map<Long, Map<Long, Integer>> map1 = new HashMap<>();
    // private Range<Integer> intRange1;
    // private Range<Long> longRange1;
    // private SortedMap<Integer, Integer> sortedMap1;
    // private LinkedHashMap<Integer, Integer> linkedMap1;
    // private Weight weight1;
}
