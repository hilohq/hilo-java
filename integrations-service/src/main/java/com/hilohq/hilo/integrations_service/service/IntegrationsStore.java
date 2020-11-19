package com.hilohq.hilo.integrations_service.service;

import com.hilohq.hilo.protobufs.integrations.prometheus.Prometheus;
import java.util.concurrent.CompletableFuture;

public interface IntegrationsStore {
  CompletableFuture<Prometheus.PrometheusMetricsModel> getPrometheusMetrics(String schemaId);
}
