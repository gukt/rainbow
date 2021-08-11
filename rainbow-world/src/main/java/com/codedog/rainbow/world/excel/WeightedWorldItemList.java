package com.codedog.rainbow.world.excel;

/**
 * WorldItem class
 *
 * @author https://github.com/gukt
 */
public class WeightedWorldItemList extends WorldItemList {

  public WeightedWorldItemList() {
    super(2, true);
  }

  public static WeightedWorldItemList valueOf(String s) {
    return (WeightedWorldItemList) parse(s, true);
  }
}
