package com.hilohq.hilo.pipelines;

@FunctionalInterface
public interface AsyncStreamExecutor {
  String executeAsync(StreamPipeline pipeline) throws Exception;
}
