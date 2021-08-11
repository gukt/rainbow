package com.codedog.rainbow.world.excel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

/**
 * WorldItem class TODO 精度问题
 *
 * @author https://github.com/gukt
 */
public class WorldItemDeserializer extends JsonDeserializer<WorldItem> {

  @Override
  public WorldItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    TreeNode node = p.getCodec().readTree(p);
    String s;
    if (node instanceof TextNode) {
      s = node.toString();
    } else {
      s = node.get(0).toString();
    }
    if (s == null || s.equalsIgnoreCase("null")) return null;
    return WorldItem.of(s.substring(1, s.length() - 1));
  }
}
