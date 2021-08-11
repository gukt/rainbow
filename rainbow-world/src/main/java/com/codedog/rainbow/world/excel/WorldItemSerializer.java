package com.codedog.rainbow.world.excel;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * WorldItem class TODO 精度问题
 *
 * @author https://github.com/gukt
 */
public class WorldItemSerializer extends JsonSerializer<WorldItem> {

  @Override
  public void serialize(WorldItem value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    //    gen.writeStartArray();
    //    gen.writeString(value.getId() + "*" + value.getRawNum());
    //    gen.writeEndArray();

    gen.writeString(value.getId() + "*" + value.getRawNum());
  }
}
