package com.hilohq.hilo.pipelines;

@FunctionalInterface
public interface AsyncBatchExecutor {
  String executeAsync(BatchPipeline pipeline) throws Exception;
}
