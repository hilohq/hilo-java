package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.protobufs.pipelines.Pipelines;

public final class StreamPipelineUtils {
  private StreamPipelineUtils() {}

  public static JsonPrometheusPushPipeline build(Pipelines.JsonPrometheusPushPipeline pipeline) {
    return JsonPrometheusPushPipeline.newBuilder()
        .inputPath(pipeline.getInputPath())
        .schemaId(pipeline.getSchemaId())
        .mongoUrl(pipeline.getMongoUrl())
        .prometheusAddress(pipeline.getPrometheusUrl())
        .build();
  }

  public static StreamPipeline build(Pipelines.StreamPipeline pipeline) {
    if (pipeline.hasJsonPrometheusPush()) {
      return build(pipeline.getJsonPrometheusPush());

    } else {
      throw new IllegalArgumentException("unknown pipeline type set");
    }
  }
}
