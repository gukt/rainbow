// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: role.proto

package com.codedog.rainbow.world.generated;

public interface ResultOrBuilder extends
    // @@protoc_insertion_point(interface_extends:service.Result)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 ret = 1;</code>
   */
  int getRet();

  /**
   * <code>string msg = 2;</code>
   */
  java.lang.String getMsg();
  /**
   * <code>string msg = 2;</code>
   */
  com.google.protobuf.ByteString
      getMsgBytes();

  /**
   * <code>map&lt;string, string&gt; payload = 3;</code>
   */
  int getPayloadCount();
  /**
   * <code>map&lt;string, string&gt; payload = 3;</code>
   */
  boolean containsPayload(
      java.lang.String key);
  /**
   * Use {@link #getPayloadMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String>
  getPayload();
  /**
   * <code>map&lt;string, string&gt; payload = 3;</code>
   */
  java.util.Map<java.lang.String, java.lang.String>
  getPayloadMap();
  /**
   * <code>map&lt;string, string&gt; payload = 3;</code>
   */

  java.lang.String getPayloadOrDefault(
      java.lang.String key,
      java.lang.String defaultValue);
  /**
   * <code>map&lt;string, string&gt; payload = 3;</code>
   */

  java.lang.String getPayloadOrThrow(
      java.lang.String key);
}
