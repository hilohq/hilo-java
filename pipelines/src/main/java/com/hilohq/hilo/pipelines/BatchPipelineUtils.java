package com.hilohq.hilo.pipelines;

import com.hilohq.hilo.protobufs.pipelines.Pipelines;

public final class BatchPipelineUtils {
  public static JsonSchemaGeneratePipeline build(Pipelines.JsonSchemaGeneratePipeline pipeline) {
    return JsonSchemaGeneratePipeline.newBuilder()
        .inputPath(pipeline.getInputPath())
        .sourceId(pipeline.getSourceId())
        .schemaId(pipeline.getSchemaId())
        .projectId(pipeline.getProjectId())
        .mongoUrl(pipeline.getMongoUrl())
        .database(pipeline.getMongoDatabase())
        .build();
  }

  public static JsonFeaturesGeneratePipeline build(
      Pipelines.JsonFeaturesGeneratePipeline pipeline) {
    return JsonFeaturesGeneratePipeline.newBuilder()
        .inputPath(pipeline.getInputPath())
        .schemaId(pipeline.getSchemaId())
        .mongoUrl(pipeline.getMongoUrl())
        .database(pipeline.getMongoDatabase())
        .build();
  }

  public static PrometheusMetricsGeneratePipeline build(
      Pipelines.PrometheusMetricsGeneratePipeline pipeline) {
    return PrometheusMetricsGeneratePipeline.newBuilder()
        .inputPath(pipeline.getInputPath())
        .schemaId(pipeline.getSchemaId())
        .mongoUrl(pipeline.getMongoUrl())
        .database(pipeline.getMongoDatabase())
        .build();
  }

  public static PrometheusGrafanaDashboardGeneratePipeline build(
      Pipelines.PrometheusGrafanaDashboardGeneratePipeline pipeline) {
    return PrometheusGrafanaDashboardGeneratePipeline.newBuilder()
        .schemaId(pipeline.getSchemaId())
        .mongoUrl(pipeline.getMongoUrl())
        .database(pipeline.getMongoDatabase())
        .grafanaUrl(pipeline.getGrafanaUrl())
        .bearerToken(pipeline.getGrafanaBearerToken())
        .build();
  }

  public static JsonInfluxDbPushPipeline build(Pipelines.JsonInfluxDbPushPipeline pipeline) {
    return JsonInfluxDbPushPipeline.newBuilder()
        .schemaId(pipeline.getSchemaId())
        .mongoUrl(pipeline.getMongoUrl())
        .influxDbAddress(pipeline.getInfluxdbUrl())
        .inputPath(pipeline.getInputPath())
        .build();
  }

  public static BatchPipeline build(Pipelines.BatchPipeline pipeline) {
    if (pipeline.hasJsonSchemaGenerate()) {
      return BatchPipelineUtils.build(pipeline.getJsonSchemaGenerate());

    } else if (pipeline.hasJsonFeaturesGenerate()) {
      return BatchPipelineUtils.build(pipeline.getJsonFeaturesGenerate());

    } else if (pipeline.hasPrometheusGrafanaDashboardGenerate()) {
      return BatchPipelineUtils.build(pipeline.getPrometheusGrafanaDashboardGenerate());

    } else if (pipeline.hasPrometheusMetricsGenerate()) {
      return BatchPipelineUtils.build(pipeline.getPrometheusMetricsGenerate());

    } else if (pipeline.hasJsonInfluxdbPushPipeline()) {
      return BatchPipelineUtils.build(pipeline.getJsonInfluxdbPushPipeline());

    } else {
      throw new IllegalArgumentException("unknown pipeline type set");
    }
  }
}
