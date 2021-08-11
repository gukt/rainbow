/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world.template;

import com.codedog.rainbow.world.excel.ExcelMapping;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * TODO 指定sheet
 *
 * @author https://github.com/gukt
 */
@ExcelMapping(file = "test.xlsx", sheet = "list")
@Getter
@ToString
public class TestTemplate {

    // private char char1;
    // private Character char2;
    // private char[] charArray1;
    // private Character[] charArray2;
    // //
    // private byte byte1;
    // private Byte byte2;
    // private byte[] byteArray1;
    // private Byte[] byteArray2;
    //
    // private short byte1;
    // private Short byte2;
    // private char[] char1;
    // private Character[] char2;
    //
    // private int byte1;
    // private Integer byte2;
    // private char[] char1;
    // private Character[] char2;
    //
    // private long byte1;
    // private Long byte2;
    // private char[] char1;
    // private Character[] char2;
    //
    // private double byte1;
    // private Double byte2;
    // private char[] char1;
    // private Character[] char2;
    //
    // private float byte1;
    // private Float byte2;
    // private char[] char1;
    // private Character[] char2;

    // private BigDecimal bigDecimal1;
    // private BigInteger bigInteger;
    // private Currency currency;
    //
    // private String string1;
    // private Date date1;
    // private LocalDate localDate1;
    // private LocalDateTime localDateTime1;
    //
    // private List<Integer> intList1;
    // private LinkedList<Integer> linkedList1;
    // private Set<Integer> intSet1;
    // private LinkedHashSet<Integer> linkedHashSet1;

    // List 嵌套
    // 默认情况下，required = true， ignore=false
    // 如果 required=true，ignore=true 同时指定，ignore 优先于 requried
    // @FieldMapping(required=false, ignore=true, alias="xxx")
    // private List<List<Integer>> nestedList1;
    //

    private Map<Long, Map<Long, Integer>> map1 = new HashMap<>();
    // private SortedMap<Integer, Integer> sortedMap1;
    // private LinkedHashMap<Integer, Integer> linkedMap1;


    // 表示范围
    // private Range<Integer> intRange1;
    // private Range<Long> longRange1;
    // private Weight weight1;
    // private Possible<Integer> possible1;
}
