package com.hilohq.hilo.protobufs.structure.feature;

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
    comments = "Source: com/hilohq/hilo/protobufs/structure/feature/api.proto")
public final class FeaturesGrpc {

  private FeaturesGrpc() {}

  public static final String SERVICE_NAME = "hilo.protobufs.structure.feature.Features";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest,
      com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse> getGetFeaturesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetFeatures",
      requestType = com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest.class,
      responseType = com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest,
      com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse> getGetFeaturesMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest, com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse> getGetFeaturesMethod;
    if ((getGetFeaturesMethod = FeaturesGrpc.getGetFeaturesMethod) == null) {
      synchronized (FeaturesGrpc.class) {
        if ((getGetFeaturesMethod = FeaturesGrpc.getGetFeaturesMethod) == null) {
          FeaturesGrpc.getGetFeaturesMethod = getGetFeaturesMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest, com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetFeatures"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FeaturesMethodDescriptorSupplier("GetFeatures"))
              .build();
        }
      }
    }
    return getGetFeaturesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest,
      com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse> getGetFeatureLogsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetFeatureLogs",
      requestType = com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest.class,
      responseType = com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest,
      com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse> getGetFeatureLogsMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest, com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse> getGetFeatureLogsMethod;
    if ((getGetFeatureLogsMethod = FeaturesGrpc.getGetFeatureLogsMethod) == null) {
      synchronized (FeaturesGrpc.class) {
        if ((getGetFeatureLogsMethod = FeaturesGrpc.getGetFeatureLogsMethod) == null) {
          FeaturesGrpc.getGetFeatureLogsMethod = getGetFeatureLogsMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest, com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetFeatureLogs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FeaturesMethodDescriptorSupplier("GetFeatureLogs"))
              .build();
        }
      }
    }
    return getGetFeatureLogsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FeaturesStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FeaturesStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FeaturesStub>() {
        @java.lang.Override
        public FeaturesStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FeaturesStub(channel, callOptions);
        }
      };
    return FeaturesStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FeaturesBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FeaturesBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FeaturesBlockingStub>() {
        @java.lang.Override
        public FeaturesBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FeaturesBlockingStub(channel, callOptions);
        }
      };
    return FeaturesBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FeaturesFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FeaturesFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FeaturesFutureStub>() {
        @java.lang.Override
        public FeaturesFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FeaturesFutureStub(channel, callOptions);
        }
      };
    return FeaturesFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class FeaturesImplBase implements io.grpc.BindableService {

    /**
     */
    public void getFeatures(com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetFeaturesMethod(), responseObserver);
    }

    /**
     */
    public void getFeatureLogs(com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetFeatureLogsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetFeaturesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest,
                com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse>(
                  this, METHODID_GET_FEATURES)))
          .addMethod(
            getGetFeatureLogsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest,
                com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse>(
                  this, METHODID_GET_FEATURE_LOGS)))
          .build();
    }
  }

  /**
   */
  public static final class FeaturesStub extends io.grpc.stub.AbstractAsyncStub<FeaturesStub> {
    private FeaturesStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FeaturesStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FeaturesStub(channel, callOptions);
    }

    /**
     */
    public void getFeatures(com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetFeaturesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getFeatureLogs(com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetFeatureLogsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class FeaturesBlockingStub extends io.grpc.stub.AbstractBlockingStub<FeaturesBlockingStub> {
    private FeaturesBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FeaturesBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FeaturesBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse getFeatures(com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetFeaturesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse getFeatureLogs(com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetFeatureLogsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class FeaturesFutureStub extends io.grpc.stub.AbstractFutureStub<FeaturesFutureStub> {
    private FeaturesFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FeaturesFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FeaturesFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse> getFeatures(
        com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetFeaturesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse> getFeatureLogs(
        com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetFeatureLogsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_FEATURES = 0;
  private static final int METHODID_GET_FEATURE_LOGS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FeaturesImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(FeaturesImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_FEATURES:
          serviceImpl.getFeatures((com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeaturesResponse>) responseObserver);
          break;
        case METHODID_GET_FEATURE_LOGS:
          serviceImpl.getFeatureLogs((com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.structure.feature.Api.GetFeatureLogsResponse>) responseObserver);
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

  private static abstract class FeaturesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FeaturesBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.hilohq.hilo.protobufs.structure.feature.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Features");
    }
  }

  private static final class FeaturesFileDescriptorSupplier
      extends FeaturesBaseDescriptorSupplier {
    FeaturesFileDescriptorSupplier() {}
  }

  private static final class FeaturesMethodDescriptorSupplier
      extends FeaturesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FeaturesMethodDescriptorSupplier(String methodName) {
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
      synchronized (FeaturesGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FeaturesFileDescriptorSupplier())
              .addMethod(getGetFeaturesMethod())
              .addMethod(getGetFeatureLogsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
