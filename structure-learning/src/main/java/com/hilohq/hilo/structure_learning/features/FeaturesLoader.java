package com.hilohq.hilo.structure_learning.features;

import com.hilohq.hilo.connectors_mongo.exceptions.MongoFindException;
import com.hilohq.hilo.features_service.service.FeaturesStore;
import com.hilohq.hilo.protobufs.structure.feature.FeatureOuterClass;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Loads the features already learned for a schema, keyed by feature key. {@link
 * FeaturesUpdateGenerate} merges them with the new statistics. Implementations are shipped to Flink
 * task managers, so they must be serializable and open their connections in {@link #load}.
 */
@FunctionalInterface
public interface FeaturesLoader extends Serializable {

  Map<String, FeatureOuterClass.Feature> load(String schemaId) throws Exception;

  /** A loader that knows no features, so every run is treated as a fresh feature set. */
  static FeaturesLoader none() {
    return schemaId -> Map.of();
  }

  /**
   * Reads the features of {@code schemaId} from {@code store}. A schema with no stored features
   * yields an empty map.
   */
  static Map<String, FeatureOuterClass.Feature> loadFrom(
      final FeaturesStore store, final String schemaId, final long timeoutSeconds)
      throws Exception {
    try {
      return store.getFeatures(schemaId).get(timeoutSeconds, TimeUnit.SECONDS).getFeaturesMap();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof MongoFindException) {
        return Map.of();
      }
      throw e;
    }
  }
}
