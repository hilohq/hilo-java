package com.hilohq.hilo.protobufs.structure;

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
    comments = "Source: com/hilohq/hilo/protobufs/structure/api.proto")
public final class SchemaGrpc {

  private SchemaGrpc() {}

  public static final String SERVICE_NAME = "hilo.protobufs.structure.Schema";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest,
      com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse> getGetSchemaIdsBySourceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSchemaIdsBySource",
      requestType = com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest.class,
      responseType = com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest,
      com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse> getGetSchemaIdsBySourceMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest, com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse> getGetSchemaIdsBySourceMethod;
    if ((getGetSchemaIdsBySourceMethod = SchemaGrpc.getGetSchemaIdsBySourceMethod) == null) {
      synchronized (SchemaGrpc.class) {
        if ((getGetSchemaIdsBySourceMethod = SchemaGrpc.getGetSchemaIdsBySourceMethod) == null) {
          SchemaGrpc.getGetSchemaIdsBySourceMethod = getGetSchemaIdsBySourceMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest, com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetSchemaIdsBySource"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SchemaMethodDescriptorSupplier("GetSchemaIdsBySource"))
              .build();
        }
      }
    }
    return getGetSchemaIdsBySourceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest,
      com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse> getGetSchemaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSchema",
      requestType = com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest.class,
      responseType = com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest,
      com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse> getGetSchemaMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest, com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse> getGetSchemaMethod;
    if ((getGetSchemaMethod = SchemaGrpc.getGetSchemaMethod) == null) {
      synchronized (SchemaGrpc.class) {
        if ((getGetSchemaMethod = SchemaGrpc.getGetSchemaMethod) == null) {
          SchemaGrpc.getGetSchemaMethod = getGetSchemaMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest, com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetSchema"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SchemaMethodDescriptorSupplier("GetSchema"))
              .build();
        }
      }
    }
    return getGetSchemaMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest,
      com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse> getGetSchemaLogsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSchemaLogs",
      requestType = com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest.class,
      responseType = com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest,
      com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse> getGetSchemaLogsMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest, com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse> getGetSchemaLogsMethod;
    if ((getGetSchemaLogsMethod = SchemaGrpc.getGetSchemaLogsMethod) == null) {
      synchronized (SchemaGrpc.class) {
        if ((getGetSchemaLogsMethod = SchemaGrpc.getGetSchemaLogsMethod) == null) {
          SchemaGrpc.getGetSchemaLogsMethod = getGetSchemaLogsMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest, com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetSchemaLogs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SchemaMethodDescriptorSupplier("GetSchemaLogs"))
              .build();
        }
      }
    }
    return getGetSchemaLogsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SchemaStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SchemaStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SchemaStub>() {
        @java.lang.Override
        public SchemaStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SchemaStub(channel, callOptions);
        }
      };
    return SchemaStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SchemaBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SchemaBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SchemaBlockingStub>() {
        @java.lang.Override
        public SchemaBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SchemaBlockingStub(channel, callOptions);
        }
      };
    return SchemaBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SchemaFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SchemaFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SchemaFutureStub>() {
        @java.lang.Override
        public SchemaFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SchemaFutureStub(channel, callOptions);
        }
      };
    return SchemaFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class SchemaImplBase implements io.grpc.BindableService {

    /**
     */
    public void getSchemaIdsBySource(com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetSchemaIdsBySourceMethod(), responseObserver);
    }

    /**
     */
    public void getSchema(com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetSchemaMethod(), responseObserver);
    }

    /**
     */
    public void getSchemaLogs(com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetSchemaLogsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetSchemaIdsBySourceMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest,
                com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse>(
                  this, METHODID_GET_SCHEMA_IDS_BY_SOURCE)))
          .addMethod(
            getGetSchemaMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest,
                com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse>(
                  this, METHODID_GET_SCHEMA)))
          .addMethod(
            getGetSchemaLogsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest,
                com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse>(
                  this, METHODID_GET_SCHEMA_LOGS)))
          .build();
    }
  }

  /**
   */
  public static final class SchemaStub extends io.grpc.stub.AbstractAsyncStub<SchemaStub> {
    private SchemaStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchemaStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SchemaStub(channel, callOptions);
    }

    /**
     */
    public void getSchemaIdsBySource(com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSchemaIdsBySourceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSchema(com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSchemaMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSchemaLogs(com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSchemaLogsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SchemaBlockingStub extends io.grpc.stub.AbstractBlockingStub<SchemaBlockingStub> {
    private SchemaBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchemaBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SchemaBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse getSchemaIdsBySource(com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetSchemaIdsBySourceMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse getSchema(com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetSchemaMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse getSchemaLogs(com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetSchemaLogsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SchemaFutureStub extends io.grpc.stub.AbstractFutureStub<SchemaFutureStub> {
    private SchemaFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchemaFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SchemaFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse> getSchemaIdsBySource(
        com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSchemaIdsBySourceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse> getSchema(
        com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSchemaMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse> getSchemaLogs(
        com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSchemaLogsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_SCHEMA_IDS_BY_SOURCE = 0;
  private static final int METHODID_GET_SCHEMA = 1;
  private static final int METHODID_GET_SCHEMA_LOGS = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SchemaImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SchemaImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_SCHEMA_IDS_BY_SOURCE:
          serviceImpl.getSchemaIdsBySource((com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaIdsBySourceResponse>) responseObserver);
          break;
        case METHODID_GET_SCHEMA:
          serviceImpl.getSchema((com.hilohq.hilo.protobufs.structure.Api.GetSchemaRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaResponse>) responseObserver);
          break;
        case METHODID_GET_SCHEMA_LOGS:
          serviceImpl.getSchemaLogs((com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.Api.GetSchemaLogsResponse>) responseObserver);
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

  private static abstract class SchemaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SchemaBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.hilohq.hilo.protobufs.structure.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Schema");
    }
  }

  private static final class SchemaFileDescriptorSupplier
      extends SchemaBaseDescriptorSupplier {
    SchemaFileDescriptorSupplier() {}
  }

  private static final class SchemaMethodDescriptorSupplier
      extends SchemaBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SchemaMethodDescriptorSupplier(String methodName) {
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
      synchronized (SchemaGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SchemaFileDescriptorSupplier())
              .addMethod(getGetSchemaIdsBySourceMethod())
              .addMethod(getGetSchemaMethod())
              .addMethod(getGetSchemaLogsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
