package com.hilohq.hilo.pipelines_service.service;

import static com.hilohq.hilo.protobufs.pipelines.Api.*;

import com.google.common.base.VerifyException;
import com.hilohq.hilo.pipelines.*;
import com.hilohq.hilo.protobufs.pipelines.Pipelines;
import com.hilohq.hilo.protobufs.pipelines.PipelinesGrpc;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class PipelinesService extends PipelinesGrpc.PipelinesImplBase {

  private final FlinkClient client;

  public PipelinesService() {
    this.client = new FlinkClient();
  }

  public static BatchPipeline build(Pipelines.BatchPipeline pipeline) {
    try {
      return BatchPipelineUtils.build(pipeline);
    } catch (NullPointerException | IllegalArgumentException | VerifyException e) {
      throw new StatusRuntimeException(Status.INVALID_ARGUMENT);
    }
  }

  private <V, R> void handle(
      final CompletableFuture<V> futureValue,
      final StreamObserver<R> responseObserver,
      final Function<V, R> mapper) {
    futureValue
        .thenAccept(
            value -> {
              final var response = mapper.apply(value);
              responseObserver.onNext(response);
              responseObserver.onCompleted();
            })
        .exceptionally(
            t -> {
              responseObserver.onError(t);
              return null;
            });
  }

  @Override
  public void executeBatchPipeline(
      final ExecuteBatchPipelineRequest request,
      final StreamObserver<ExecuteBatchPipelineResponse> responseObserver) {
    if (!request.hasPipeline()) {
      responseObserver.onError(new StatusException(Status.INVALID_ARGUMENT));
      return;
    }

    final var pipeline = BatchPipelineUtils.build(request.getPipeline());
    handle(
        client.executeBatchPipeline(pipeline),
        responseObserver,
        jobId -> ExecuteBatchPipelineResponse.newBuilder().setJobId(jobId).build());
  }

  @Override
  public void executeStreamPipeline(
      final ExecuteStreamPipelineRequest request,
      final StreamObserver<ExecuteStreamPipelineResponse> responseObserver) {
    if (!request.hasPipeline()) {
      responseObserver.onError(new StatusException(Status.INVALID_ARGUMENT));
      return;
    }

    final var pipeline = StreamPipelineUtils.build(request.getPipeline());
    handle(
        client.executeStreamPipeline(pipeline),
        responseObserver,
        jobId -> ExecuteStreamPipelineResponse.newBuilder().setJobId(jobId).build());
  }

  @Override
  public void getPipelineStatus(
      final GetPipelineStatusRequest request,
      final StreamObserver<GetPipelineStatusResponse> responseObserver) {
    handle(
        client.getPipelineStatus(request.getJobId()),
        responseObserver,
        status -> GetPipelineStatusResponse.newBuilder().setStatus(status.toString()).build());
  }

  @Override
  public void cancelPipeline(
      final CancelPipelineRequest request,
      final StreamObserver<CancelPipelineResponse> responseObserver) {
    handle(
        client.cancelPipeline(request.getJobId()),
        responseObserver,
        V -> CancelPipelineResponse.newBuilder().build());
  }
}
