/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Range;

import java.io.IOException;
import java.util.Collection;

/**
 * Probable class
 *
 * @author https://github.com/gukt
 */
// @JsonDeserialize(using = Probable.ProbableDeserializer.class)
public final class Probable<E> {

  private int odds = 0;
  private E element;
  private Range<Integer> range;

  /** Prevents to construct an instance. */
  private Probable() {}

  public static <E> Probable<E> of(E element, int odds) {
    Probable<E> instance = new Probable<>();
    instance.odds = odds;
    instance.element = element;
    return instance;
  }

   public static <E> void resolveRange(
           Collection<Probable<E>> elements, boolean byWeight, int precision) {
     if (byWeight) {
       int totalWeights = elements.stream().mapToInt(element -> element.odds).sum();
       int lower = 0;
       for (Probable<?> element : elements) {
         int delta = element.odds * (int) Math.pow(10, 2 + precision) / totalWeights;
         int upper = lower + delta;
         element.range = Range.openClosed(lower, upper);
         lower = upper;
       }
     } else {
       int lower = 0;
       for (Probable<?> element : elements) {
         int delta = element.odds * (int) Math.pow(10, precision);
         int upper = lower + delta;
         element.range = Range.openClosed(lower, upper);
         lower = upper;
       }
     }
   }

  public int getOdds() {
    return odds;
  }

  E getElement() {
    return element;
  }

  Range<Integer> getRange() {
    return range;
  }

  public void setRange(Range<Integer> range) {
    this.range = range;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("odds", odds)
        .add("range", range)
        .add("element", element)
        .toString();
  }

 public static class ProbableDeserializer<T> extends JsonDeserializer<Probable<T>> {

    @Override
    public Probable<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      TreeNode node = p.getCodec().readTree(p);
      String s;
      if (node instanceof TextNode) {
        s = node.toString();
      } else {
        s = node.get(0).toString();
      }
      if (s == null || s.equalsIgnoreCase("null")) return null;
//      return Probable.of(s.substring(1, s.length() - 1));
      return null;
    }
  }
}
