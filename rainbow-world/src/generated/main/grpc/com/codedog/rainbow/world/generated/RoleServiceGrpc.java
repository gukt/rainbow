package com.codedog.rainbow.world.generated;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: role.proto")
public final class RoleServiceGrpc {

  private RoleServiceGrpc() {}

  public static final String SERVICE_NAME = "service.RoleService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.codedog.rainbow.world.generated.RoleListRequest,
      com.codedog.rainbow.world.generated.RoleListResponse> METHOD_FIND_ALL =
      io.grpc.MethodDescriptor.<com.codedog.rainbow.world.generated.RoleListRequest, com.codedog.rainbow.world.generated.RoleListResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "service.RoleService", "findAll"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.codedog.rainbow.world.generated.RoleListRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.codedog.rainbow.world.generated.RoleListResponse.getDefaultInstance()))
          .setSchemaDescriptor(new RoleServiceMethodDescriptorSupplier("findAll"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.codedog.rainbow.world.generated.BanRequest,
      com.codedog.rainbow.world.generated.Result> METHOD_BAN =
      io.grpc.MethodDescriptor.<com.codedog.rainbow.world.generated.BanRequest, com.codedog.rainbow.world.generated.Result>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "service.RoleService", "ban"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.codedog.rainbow.world.generated.BanRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.codedog.rainbow.world.generated.Result.getDefaultInstance()))
          .setSchemaDescriptor(new RoleServiceMethodDescriptorSupplier("ban"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.codedog.rainbow.world.generated.Number,
      com.codedog.rainbow.world.generated.RoleInfo> METHOD_FIND_ONE =
      io.grpc.MethodDescriptor.<com.codedog.rainbow.world.generated.Number, com.codedog.rainbow.world.generated.RoleInfo>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "service.RoleService", "findOne"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.codedog.rainbow.world.generated.Number.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.codedog.rainbow.world.generated.RoleInfo.getDefaultInstance()))
          .setSchemaDescriptor(new RoleServiceMethodDescriptorSupplier("findOne"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RoleServiceStub newStub(io.grpc.Channel channel) {
    return new RoleServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RoleServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RoleServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RoleServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RoleServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class RoleServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *  请求玩家列表
     * </pre>
     */
    public void findAll(com.codedog.rainbow.world.generated.RoleListRequest request,
        io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.RoleListResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FIND_ALL, responseObserver);
    }

    /**
     * <pre>
     * 封禁/解禁
     * </pre>
     */
    public void ban(com.codedog.rainbow.world.generated.BanRequest request,
        io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.Result> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_BAN, responseObserver);
    }

    /**
     * <pre>
     * 获得角色信息
     * </pre>
     */
    public void findOne(com.codedog.rainbow.world.generated.Number request,
        io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.RoleInfo> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FIND_ONE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_FIND_ALL,
            asyncUnaryCall(
              new MethodHandlers<
                com.codedog.rainbow.world.generated.RoleListRequest,
                com.codedog.rainbow.world.generated.RoleListResponse>(
                  this, METHODID_FIND_ALL)))
          .addMethod(
            METHOD_BAN,
            asyncUnaryCall(
              new MethodHandlers<
                com.codedog.rainbow.world.generated.BanRequest,
                com.codedog.rainbow.world.generated.Result>(
                  this, METHODID_BAN)))
          .addMethod(
            METHOD_FIND_ONE,
            asyncUnaryCall(
              new MethodHandlers<
                com.codedog.rainbow.world.generated.Number,
                com.codedog.rainbow.world.generated.RoleInfo>(
                  this, METHODID_FIND_ONE)))
          .build();
    }
  }

  /**
   */
  public static final class RoleServiceStub extends io.grpc.stub.AbstractStub<RoleServiceStub> {
    private RoleServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RoleServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RoleServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RoleServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     *  请求玩家列表
     * </pre>
     */
    public void findAll(com.codedog.rainbow.world.generated.RoleListRequest request,
        io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.RoleListResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FIND_ALL, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 封禁/解禁
     * </pre>
     */
    public void ban(com.codedog.rainbow.world.generated.BanRequest request,
        io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.Result> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_BAN, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 获得角色信息
     * </pre>
     */
    public void findOne(com.codedog.rainbow.world.generated.Number request,
        io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.RoleInfo> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FIND_ONE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RoleServiceBlockingStub extends io.grpc.stub.AbstractStub<RoleServiceBlockingStub> {
    private RoleServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RoleServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RoleServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RoleServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *  请求玩家列表
     * </pre>
     */
    public com.codedog.rainbow.world.generated.RoleListResponse findAll(com.codedog.rainbow.world.generated.RoleListRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FIND_ALL, getCallOptions(), request);
    }

    /**
     * <pre>
     * 封禁/解禁
     * </pre>
     */
    public com.codedog.rainbow.world.generated.Result ban(com.codedog.rainbow.world.generated.BanRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_BAN, getCallOptions(), request);
    }

    /**
     * <pre>
     * 获得角色信息
     * </pre>
     */
    public com.codedog.rainbow.world.generated.RoleInfo findOne(com.codedog.rainbow.world.generated.Number request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FIND_ONE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RoleServiceFutureStub extends io.grpc.stub.AbstractStub<RoleServiceFutureStub> {
    private RoleServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RoleServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RoleServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RoleServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *  请求玩家列表
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codedog.rainbow.world.generated.RoleListResponse> findAll(
        com.codedog.rainbow.world.generated.RoleListRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FIND_ALL, getCallOptions()), request);
    }

    /**
     * <pre>
     * 封禁/解禁
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codedog.rainbow.world.generated.Result> ban(
        com.codedog.rainbow.world.generated.BanRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_BAN, getCallOptions()), request);
    }

    /**
     * <pre>
     * 获得角色信息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codedog.rainbow.world.generated.RoleInfo> findOne(
        com.codedog.rainbow.world.generated.Number request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FIND_ONE, getCallOptions()), request);
    }
  }

  private static final int METHODID_FIND_ALL = 0;
  private static final int METHODID_BAN = 1;
  private static final int METHODID_FIND_ONE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RoleServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RoleServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FIND_ALL:
          serviceImpl.findAll((com.codedog.rainbow.world.generated.RoleListRequest) request,
              (io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.RoleListResponse>) responseObserver);
          break;
        case METHODID_BAN:
          serviceImpl.ban((com.codedog.rainbow.world.generated.BanRequest) request,
              (io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.Result>) responseObserver);
          break;
        case METHODID_FIND_ONE:
          serviceImpl.findOne((com.codedog.rainbow.world.generated.Number) request,
              (io.grpc.stub.StreamObserver<com.codedog.rainbow.world.generated.RoleInfo>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RoleServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RoleServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codedog.rainbow.world.generated.RoleProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RoleService");
    }
  }

  private static final class RoleServiceFileDescriptorSupplier
      extends RoleServiceBaseDescriptorSupplier {
    RoleServiceFileDescriptorSupplier() {}
  }

  private static final class RoleServiceMethodDescriptorSupplier
      extends RoleServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RoleServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RoleServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RoleServiceFileDescriptorSupplier())
              .addMethod(METHOD_FIND_ALL)
              .addMethod(METHOD_BAN)
              .addMethod(METHOD_FIND_ONE)
              .build();
        }
      }
    }
    return result;
  }
}
