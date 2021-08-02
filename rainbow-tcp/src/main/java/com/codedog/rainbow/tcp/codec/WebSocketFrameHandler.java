/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.tcp.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * WebSocketFrameHandler class
 *
 * @author https://github.com/gukt
 */
public class WebSocketFrameHandler extends MessageToMessageDecoder<WebSocketFrame> {

  @Override
  protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) {
    // ping and pong and close frames already handled

    if (frame instanceof TextWebSocketFrame) {
      out.add(frame.retain().content());
    } else {
      String message = "Unsupported frame type: " + frame.getClass().getName();
      throw new UnsupportedOperationException(message);
    }
  }
}
