package com.hilohq.hilo.protobufs.integrations;

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
    comments = "Source: com/hilohq/hilo/protobufs/integrations/api.proto")
public final class IntegrationsGrpc {

  private IntegrationsGrpc() {}

  public static final String SERVICE_NAME = "hilo.protobufs.integrations.Integrations";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest,
      com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse> getGetPrometheusMetricsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetPrometheusMetrics",
      requestType = com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest.class,
      responseType = com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest,
      com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse> getGetPrometheusMetricsMethod() {
    io.grpc.MethodDescriptor<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest, com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse> getGetPrometheusMetricsMethod;
    if ((getGetPrometheusMetricsMethod = IntegrationsGrpc.getGetPrometheusMetricsMethod) == null) {
      synchronized (IntegrationsGrpc.class) {
        if ((getGetPrometheusMetricsMethod = IntegrationsGrpc.getGetPrometheusMetricsMethod) == null) {
          IntegrationsGrpc.getGetPrometheusMetricsMethod = getGetPrometheusMetricsMethod =
              io.grpc.MethodDescriptor.<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest, com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetPrometheusMetrics"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new IntegrationsMethodDescriptorSupplier("GetPrometheusMetrics"))
              .build();
        }
      }
    }
    return getGetPrometheusMetricsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IntegrationsStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IntegrationsStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IntegrationsStub>() {
        @java.lang.Override
        public IntegrationsStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IntegrationsStub(channel, callOptions);
        }
      };
    return IntegrationsStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IntegrationsBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IntegrationsBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IntegrationsBlockingStub>() {
        @java.lang.Override
        public IntegrationsBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IntegrationsBlockingStub(channel, callOptions);
        }
      };
    return IntegrationsBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IntegrationsFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IntegrationsFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IntegrationsFutureStub>() {
        @java.lang.Override
        public IntegrationsFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IntegrationsFutureStub(channel, callOptions);
        }
      };
    return IntegrationsFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class IntegrationsImplBase implements io.grpc.BindableService {

    /**
     */
    public void getPrometheusMetrics(com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetPrometheusMetricsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetPrometheusMetricsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest,
                com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse>(
                  this, METHODID_GET_PROMETHEUS_METRICS)))
          .build();
    }
  }

  /**
   */
  public static final class IntegrationsStub extends io.grpc.stub.AbstractAsyncStub<IntegrationsStub> {
    private IntegrationsStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IntegrationsStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IntegrationsStub(channel, callOptions);
    }

    /**
     */
    public void getPrometheusMetrics(com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest request,
        io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetPrometheusMetricsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class IntegrationsBlockingStub extends io.grpc.stub.AbstractBlockingStub<IntegrationsBlockingStub> {
    private IntegrationsBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IntegrationsBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IntegrationsBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse getPrometheusMetrics(com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetPrometheusMetricsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class IntegrationsFutureStub extends io.grpc.stub.AbstractFutureStub<IntegrationsFutureStub> {
    private IntegrationsFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IntegrationsFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IntegrationsFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse> getPrometheusMetrics(
        com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetPrometheusMetricsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PROMETHEUS_METRICS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final IntegrationsImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(IntegrationsImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_PROMETHEUS_METRICS:
          serviceImpl.getPrometheusMetrics((com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsRequest) request,
              (io.grpc.stub.StreamObserver<com.hilohq.hilo.protobufs.integrations.Api.GetPrometheusMetricsResponse>) responseObserver);
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

  private static abstract class IntegrationsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    IntegrationsBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.hilohq.hilo.protobufs.integrations.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Integrations");
    }
  }

  private static final class IntegrationsFileDescriptorSupplier
      extends IntegrationsBaseDescriptorSupplier {
    IntegrationsFileDescriptorSupplier() {}
  }

  private static final class IntegrationsMethodDescriptorSupplier
      extends IntegrationsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    IntegrationsMethodDescriptorSupplier(String methodName) {
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
      synchronized (IntegrationsGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IntegrationsFileDescriptorSupplier())
              .addMethod(getGetPrometheusMetricsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
