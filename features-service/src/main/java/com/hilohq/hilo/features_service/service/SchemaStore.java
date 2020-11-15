package com.hilohq.hilo.features_service.service;

import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface SchemaStore {
  CompletableFuture<FeatureOuterClass.FeaturesModel> getFeatures(String schemaId);

  CompletableFuture<Collection<FeatureOuterClass.FeatureLogsModel>> getFeatureLogs(String schemaId);
}
