package com.hilohq.hilo.pipelines;

@FunctionalInterface
public interface StreamExecutor {
  void execute(StreamPipeline pipeline) throws Exception;
}
