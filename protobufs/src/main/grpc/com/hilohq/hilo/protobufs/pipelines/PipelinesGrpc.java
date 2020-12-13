package com.hilohq.hilo.protobufs.pipelines;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.31.1)",
    comments = "Source: com/hilohq/hilo/protobufs/pipelines/api.proto")
public final class PipelinesGrpc {

  private PipelinesGrpc() {}

  public static final String SERVICE_NAME = "hilo.protobufs.pipelines.Pipelines";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse> getExecuteBatchPipelineMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteBatchPipeline",
      requestType = com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest.class,
      responseType = com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse> getExecuteBatchPipelineMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest, com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse> getExecuteBatchPipelineMethod;
    if ((getExecuteBatchPipelineMethod = PipelinesGrpc.getExecuteBatchPipelineMethod) == null) {
      synchronized (PipelinesGrpc.class) {
        if ((getExecuteBatchPipelineMethod = PipelinesGrpc.getExecuteBatchPipelineMethod) == null) {
          PipelinesGrpc.getExecuteBatchPipelineMethod = getExecuteBatchPipelineMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest, com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteBatchPipeline"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PipelinesMethodDescriptorSupplier("ExecuteBatchPipeline"))
              .build();
        }
      }
    }
    return getExecuteBatchPipelineMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse> getExecuteStreamPipelineMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteStreamPipeline",
      requestType = com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest.class,
      responseType = com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse> getExecuteStreamPipelineMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest, com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse> getExecuteStreamPipelineMethod;
    if ((getExecuteStreamPipelineMethod = PipelinesGrpc.getExecuteStreamPipelineMethod) == null) {
      synchronized (PipelinesGrpc.class) {
        if ((getExecuteStreamPipelineMethod = PipelinesGrpc.getExecuteStreamPipelineMethod) == null) {
          PipelinesGrpc.getExecuteStreamPipelineMethod = getExecuteStreamPipelineMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest, com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteStreamPipeline"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PipelinesMethodDescriptorSupplier("ExecuteStreamPipeline"))
              .build();
        }
      }
    }
    return getExecuteStreamPipelineMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse> getGetPipelineStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetPipelineStatus",
      requestType = com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest.class,
      responseType = com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse> getGetPipelineStatusMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest, com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse> getGetPipelineStatusMethod;
    if ((getGetPipelineStatusMethod = PipelinesGrpc.getGetPipelineStatusMethod) == null) {
      synchronized (PipelinesGrpc.class) {
        if ((getGetPipelineStatusMethod = PipelinesGrpc.getGetPipelineStatusMethod) == null) {
          PipelinesGrpc.getGetPipelineStatusMethod = getGetPipelineStatusMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest, com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetPipelineStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PipelinesMethodDescriptorSupplier("GetPipelineStatus"))
              .build();
        }
      }
    }
    return getGetPipelineStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse> getCancelPipelineMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelPipeline",
      requestType = com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest.class,
      responseType = com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest,
      com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse> getCancelPipelineMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest, com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse> getCancelPipelineMethod;
    if ((getCancelPipelineMethod = PipelinesGrpc.getCancelPipelineMethod) == null) {
      synchronized (PipelinesGrpc.class) {
        if ((getCancelPipelineMethod = PipelinesGrpc.getCancelPipelineMethod) == null) {
          PipelinesGrpc.getCancelPipelineMethod = getCancelPipelineMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest, com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelPipeline"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PipelinesMethodDescriptorSupplier("CancelPipeline"))
              .build();
        }
      }
    }
    return getCancelPipelineMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PipelinesStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PipelinesStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PipelinesStub>() {
        @java.lang.Override
        public PipelinesStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PipelinesStub(channel, callOptions);
        }
      };
    return PipelinesStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PipelinesBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PipelinesBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PipelinesBlockingStub>() {
        @java.lang.Override
        public PipelinesBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PipelinesBlockingStub(channel, callOptions);
        }
      };
    return PipelinesBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PipelinesFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PipelinesFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PipelinesFutureStub>() {
        @java.lang.Override
        public PipelinesFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PipelinesFutureStub(channel, callOptions);
        }
      };
    return PipelinesFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class PipelinesImplBase implements io.grpc.BindableService {

    /**
     */
    public void executeBatchPipeline(com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getExecuteBatchPipelineMethod(), responseObserver);
    }

    /**
     */
    public void executeStreamPipeline(com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getExecuteStreamPipelineMethod(), responseObserver);
    }

    /**
     */
    public void getPipelineStatus(com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetPipelineStatusMethod(), responseObserver);
    }

    /**
     */
    public void cancelPipeline(com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCancelPipelineMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getExecuteBatchPipelineMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest,
                com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse>(
                  this, METHODID_EXECUTE_BATCH_PIPELINE)))
          .addMethod(
            getExecuteStreamPipelineMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest,
                com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse>(
                  this, METHODID_EXECUTE_STREAM_PIPELINE)))
          .addMethod(
            getGetPipelineStatusMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest,
                com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse>(
                  this, METHODID_GET_PIPELINE_STATUS)))
          .addMethod(
            getCancelPipelineMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest,
                com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse>(
                  this, METHODID_CANCEL_PIPELINE)))
          .build();
    }
  }

  /**
   */
  public static final class PipelinesStub extends io.grpc.stub.AbstractAsyncStub<PipelinesStub> {
    private PipelinesStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PipelinesStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PipelinesStub(channel, callOptions);
    }

    /**
     */
    public void executeBatchPipeline(com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExecuteBatchPipelineMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void executeStreamPipeline(com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExecuteStreamPipelineMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getPipelineStatus(com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetPipelineStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void cancelPipeline(com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCancelPipelineMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PipelinesBlockingStub extends io.grpc.stub.AbstractBlockingStub<PipelinesBlockingStub> {
    private PipelinesBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PipelinesBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PipelinesBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse executeBatchPipeline(com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest request) {
      return blockingUnaryCall(
          getChannel(), getExecuteBatchPipelineMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse executeStreamPipeline(com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest request) {
      return blockingUnaryCall(
          getChannel(), getExecuteStreamPipelineMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse getPipelineStatus(com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetPipelineStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse cancelPipeline(com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest request) {
      return blockingUnaryCall(
          getChannel(), getCancelPipelineMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PipelinesFutureStub extends io.grpc.stub.AbstractFutureStub<PipelinesFutureStub> {
    private PipelinesFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PipelinesFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PipelinesFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse> executeBatchPipeline(
        com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getExecuteBatchPipelineMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse> executeStreamPipeline(
        com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getExecuteStreamPipelineMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse> getPipelineStatus(
        com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetPipelineStatusMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse> cancelPipeline(
        com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCancelPipelineMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE_BATCH_PIPELINE = 0;
  private static final int METHODID_EXECUTE_STREAM_PIPELINE = 1;
  private static final int METHODID_GET_PIPELINE_STATUS = 2;
  private static final int METHODID_CANCEL_PIPELINE = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PipelinesImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PipelinesImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXECUTE_BATCH_PIPELINE:
          serviceImpl.executeBatchPipeline((com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteBatchPipelineResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_STREAM_PIPELINE:
          serviceImpl.executeStreamPipeline((com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.ExecuteStreamPipelineResponse>) responseObserver);
          break;
        case METHODID_GET_PIPELINE_STATUS:
          serviceImpl.getPipelineStatus((com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.GetPipelineStatusResponse>) responseObserver);
          break;
        case METHODID_CANCEL_PIPELINE:
          serviceImpl.cancelPipeline((com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.pipelines.Api.CancelPipelineResponse>) responseObserver);
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

  private static abstract class PipelinesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PipelinesBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.hilohq.hilo.protobufs.pipelines.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Pipelines");
    }
  }

  private static final class PipelinesFileDescriptorSupplier
      extends PipelinesBaseDescriptorSupplier {
    PipelinesFileDescriptorSupplier() {}
  }

  private static final class PipelinesMethodDescriptorSupplier
      extends PipelinesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PipelinesMethodDescriptorSupplier(String methodName) {
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
      synchronized (PipelinesGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PipelinesFileDescriptorSupplier())
              .addMethod(getExecuteBatchPipelineMethod())
              .addMethod(getExecuteStreamPipelineMethod())
              .addMethod(getGetPipelineStatusMethod())
              .addMethod(getCancelPipelineMethod())
              .build();
        }
      }
    }
    return result;
  }
}
