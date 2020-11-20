package com.hilohq.hilo.pipelines;

@FunctionalInterface
public interface BatchExecutor {
  void execute(BatchPipeline pipeline) throws Exception;
}
